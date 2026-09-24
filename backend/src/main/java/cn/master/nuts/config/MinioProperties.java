package cn.master.nuts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : 11's papa
 * @since : 2026/9/23, 星期三
 **/
@ConfigurationProperties(prefix = MinioProperties.MINIO_PREFIX)
@Getter
@Setter
public class MinioProperties {
    public static final String MINIO_PREFIX = "minio";

    private String endpoint;
    private String accessKey;
    private String secretKey;
}

