package cn.master.nuts.config;

import io.minio.*;
import io.minio.messages.Filter;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/23, 星期三
 **/
@Configuration
public class MinIOConfig {
    public static final String BUCKET = "nuts";

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        try {
            // 创建 MinioClient 客户端
            MinioClient client = MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
            // 设置临时目录下文件的过期时间
            setBucketLifecycle(client);
            setBucketLifecycleByExcel(client);
            boolean exist = client.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());
            if (!exist) {
                client.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            }
            return client;
        } catch (Exception e) {
            throw new RuntimeException("MinIOClient初始化失败！", e);
        }
    }

    private void setBucketLifecycleByExcel(MinioClient client) {
        try {
            List<LifecycleConfiguration.Rule> rules = new LinkedList<>();
            rules.add(new LifecycleConfiguration.Rule(
                    Status.ENABLED,
                    null,
                    new LifecycleConfiguration.Expiration((Time.S3Time) null, 7, null, null),
                    new Filter("system/export/excel"),
                    "excel-file", null, null, null
            ));
            LifecycleConfiguration configuration = new LifecycleConfiguration(rules);
            client.setBucketLifecycle(SetBucketLifecycleArgs.builder().bucket(BUCKET).config(configuration).build());
        } catch (Exception e) {
            throw new RuntimeException("设置存储桶生命周期失败！", e);
        }
    }

    private void setBucketLifecycle(MinioClient client) {
        try {
            List<LifecycleConfiguration.Rule> rules = new LinkedList<>();
            rules.add(new LifecycleConfiguration.Rule(
                    Status.ENABLED,
                    null,
                    new LifecycleConfiguration.Expiration((Time.S3Time) null, 7, null, null),
                    new Filter("system/temp/"),
                    "temp-file", null, null, null
            ));
            LifecycleConfiguration configuration = new LifecycleConfiguration(rules);
            client.setBucketLifecycle(SetBucketLifecycleArgs.builder().bucket(BUCKET).config(configuration).build());
        } catch (Exception e) {
            throw new RuntimeException("设置存储桶生命周期失败！", e);
        }
    }
}
