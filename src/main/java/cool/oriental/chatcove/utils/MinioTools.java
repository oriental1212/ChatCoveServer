package cool.oriental.chatcove.utils;

import cool.oriental.chatcove.configuration.minio.MinioConfiguration;
import cool.oriental.chatcove.configuration.minio.MinioEnum;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Oriental
 * @Date: 2023-07-12-10:28
 * @Description: Minio的工具类
 */
@Slf4j
public class MinioTools {
    @Resource
    private MinioConfiguration minioConfiguration;

    public Boolean Upload(MultipartFile multipartFile, String name,MinioEnum minioEnum){
        switch (minioEnum.getType()){
            case "channel" -> {
                return ChannelAvatarUpload(multipartFile, name, minioEnum);
            }
            case "user" -> System.out.println("USER_AVATAR");
        }
        return Boolean.FALSE;
    }

    private Boolean ChannelAvatarUpload(MultipartFile multipartFile, String name, MinioEnum minioEnum){
        String objectName = name+"/"+"channelAvatar";
        try {
            ObjectWriteResponse channel = minioConfiguration.MinioCreator().putObject(
                    PutObjectArgs
                            .builder()
                            .bucket("channel")
                            .object(objectName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build()
            );
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件工具类异常，频道头像上传失败");
            return Boolean.FALSE;
        }
    }
}
