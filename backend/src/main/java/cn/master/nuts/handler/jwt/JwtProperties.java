package cn.master.nuts.handler.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author : 11's papa
 * @since : 2026/9/4, 星期五
 **/
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Duration accessTtl = Duration.ofMinutes(5);
    private Duration refreshTtl = Duration.ofDays(1);
}
