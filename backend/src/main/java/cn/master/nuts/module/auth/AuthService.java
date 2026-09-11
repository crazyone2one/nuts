package cn.master.nuts.module.auth;

import cn.master.nuts.dto.LoginRequest;
import cn.master.nuts.dto.system.UserDTO;
import cn.master.nuts.handler.jwt.JwtTokenProvider;
import cn.master.nuts.handler.result.NsHttpResultCode;
import cn.master.nuts.handler.result.ResultHolder;
import cn.master.nuts.handler.security.CustomUserDetailsService;
import cn.master.nuts.module.system.entity.User;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/9/4, 星期五
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;


    public UserDTO login(LoginRequest loginRequest, HttpServletResponse res) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        context.setAuthentication(authenticationResponse);
        SecurityContextHolder.setContext(context);
        UserDetails userDetails = (UserDetails) authenticationResponse.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());
        setRefreshCookie(res, refreshToken);

        UserDTO userDTO = QueryChain.of(User.class).where(User::getName).eq(userDetails.getUsername()).oneAs(UserDTO.class);
        userDTO.setAccessToken(accessToken);
        return userDTO;
    }

    private void setRefreshCookie(HttpServletResponse res, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // 强烈建议添加，防御 CSRF
                .path("/")   // 限制 Cookie 仅在刷新接口发送
                .maxAge(Duration.ofDays(1))
                .build();

        // 仅通过 Set-Cookie 下发，绝不在响应头或响应体中返回 Token
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest req, HttpServletResponse res) {
        String rt = extractRefreshToken(req);
        // 场景①：cookie 缺失
        if (Objects.isNull(rt)) {
            clearRefreshCookie(res); // 兜底清一次，避免残留过期 cookie
            return ResponseEntity.status(401).body(Map.of(
                    "code", "REFRESH_TOKEN_MISSING",
                    "message", "未找到刷新令牌"));
        }
        try {
            JwtTokenProvider.RefreshResult result = jwtTokenProvider.rotateRefreshToken(rt);
            UserDetails ud = userDetailsService.loadUserByUsername(result.username());
            setRefreshCookie(res, result.newRefreshToken());
            return ResponseEntity.ok(Map.of("accessToken", jwtTokenProvider.generateAccessToken(ud)));
        } catch (RefreshTokenReuseException e) {
            // 场景③：安全事件——必须告警/埋点，不能只静默 401
            log.error("REFRESH TOKEN REUSE DETECTED, family revoked, remote={}",
                    req.getRemoteAddr(), e);
            clearRefreshCookie(res);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_TOKEN_REUSED"));
        } catch (RefreshTokenNotFoundException e) {
            // 场景②：会话已失效（过期/被清库/伪造），正常登出流程
            log.info("Refresh token not found: {}", e.getMessage());
            clearRefreshCookie(res);
            return ResponseEntity.status(401).body(Map.of("code", "REFRESH_TOKEN_INVALID"));
        }
    }


    private String extractRefreshToken(HttpServletRequest req) {
        if (req.getCookies() == null) {
            return null;
        }
        return Arrays.stream(req.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public ResultHolder logout(HttpServletRequest request, HttpServletResponse response) {
        String rt = extractRefreshToken(request);
        if (rt != null) {
            jwtTokenProvider.revokeToken(rt);
        }
        clearRefreshCookie(response);
        return ResultHolder.success("logout successful");
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // 立即过期，实现清除
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
