package cool.oriental.chatcove.configuration.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-16:45
 * @Description: Minio配置文件
 */

@Configuration
public class MinioConfiguration {
    private static final String minioUrl = "114.115.223.227:3306";
    private static final String account = "chatcoveminio";
    private static final String password = "chatcoveminio";
    @Bean
    public MinioClient minioCreator(){
        return minioInitialize();
    }

    public MinioClient minioInitialize(){
        return MinioClient
                .builder()
                .endpoint(minioUrl)
                .credentials(account, password)
                .build();
    }
}
