package cn.master.nuts.handler.jwt;

import cn.master.nuts.module.auth.RefreshTokenNotFoundException;
import cn.master.nuts.module.auth.RefreshTokenReuseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author : 11's papa
 * @since : 2026/9/4, 星期五
 **/
@Component
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final StringRedisTemplate redisTemplate;

    private static final String RT_PREFIX = "refresh_token:";
    private static final String FAMILY_PREFIX = "token_family:";

    public record RefreshResult(String username, String newRefreshToken) {
    }

    public JwtTokenProvider(JwtProperties jwtProperties, Environment environment, StringRedisTemplate redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(resolveSecret(jwtProperties, environment).getBytes(StandardCharsets.UTF_8));
        this.redisTemplate = redisTemplate;
    }

    private String resolveSecret(JwtProperties prop, Environment environment) {
        if (StringUtils.isNotBlank(prop.getSecret())) {
            if (prop.getSecret().length() < 32) {
                throw new IllegalStateException("JWT secret 长度至少 32 个字符");
            }
            return prop.getSecret();
        }
        // 生产环境必须配置密钥
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            throw new IllegalStateException("生产环境必须配置 APP_SECURITY_JWT_SECRET");
        }
        // 非生产环境自动生成临时密钥（重启后旧 Token 失效）
        byte[] randomBytes = new byte[48];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenActive(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && isTokenActive(token);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), jwtProperties.getAccessTtl());
    }

    private String secureRandom() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private String sha256(String input) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().withoutPadding().encodeToString(d.digest(input.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(String username) {
        String token = secureRandom();
        String hash = sha256(token);
        String familyId = UUID.randomUUID().toString();
        storeToken(hash, username, familyId);
        addToFamily(familyId, hash);
        return token;
    }

    public void revokeToken(String token) {
        redisTemplate.opsForHash().put(RT_PREFIX + sha256(token), "revoked", "true");
    }

    public RefreshResult rotateRefreshToken(String incoming) {
        String hash = sha256(incoming);
        String key = RT_PREFIX + hash;

        String revoked = (String) redisTemplate.opsForHash().get(key, "revoked");
        String username = (String) redisTemplate.opsForHash().get(key, "username");
        String familyId = (String) redisTemplate.opsForHash().get(key, "familyId");

        if (revoked == null) {
            throw new RefreshTokenNotFoundException();
        }
        if ("true".equals(revoked)) {
            revokeFamily(familyId);
            throw new RefreshTokenReuseException();
        }

        redisTemplate.opsForHash().put(key, "revoked", "true");

        String newToken = secureRandom();
        String newHash = sha256(newToken);
        storeToken(newHash, username, familyId);
        addToFamily(familyId, newHash);
        return new RefreshResult(username, newToken);
    }

    private void revokeFamily(String familyId) {
        var members = redisTemplate.opsForSet().members(FAMILY_PREFIX + familyId);
        if (members != null) {
            members.forEach(h ->
                    redisTemplate.opsForHash().put(RT_PREFIX + h, "revoked", "true"));
        }
    }

    private void addToFamily(String familyId, String hash) {
        String familyKey = FAMILY_PREFIX + familyId;
        redisTemplate.opsForSet().add(familyKey, hash);
        redisTemplate.expire(familyKey, jwtProperties.getRefreshTtl());
    }

    private void storeToken(String hash, String username, String familyId) {
        String key = RT_PREFIX + hash;
        redisTemplate.opsForHash().put(key, "username", username);
        redisTemplate.opsForHash().put(key, "familyId", familyId);
        redisTemplate.opsForHash().put(key, "revoked", "false");
        redisTemplate.expire(key, jwtProperties.getRefreshTtl());
    }

    public String generateToken(String username, Duration duration) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + duration.toMillis());

        return Jwts.builder().subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return Jwts.claims().subject(null).expiration(new Date(0)).build();
        }
    }
}
