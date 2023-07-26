package cool.oriental.chatcove.configuration.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-16:45
 * @Description: Minio配置文件
 */

@Configuration
@Slf4j
public class MinioConfiguration {
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.account}")
    private String account;
    @Value("${minio.password}")
    private String password;
    @Bean
    public MinioClient MinioCreator(){
        return MinioInitialize();
    }

    private MinioClient MinioInitialize(){
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint(minioUrl)
                .credentials(account, password)
                .build();
        try {
            if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket("channel").build())){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("channel").build());
            }
            if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket("user").build())){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user").build());
            }
            return minioClient;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化Minio桶异常");
        }
        return minioClient;
    }
}
