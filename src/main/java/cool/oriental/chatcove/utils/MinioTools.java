package cool.oriental.chatcove.utils;

import cool.oriental.chatcove.configuration.minio.MinioConfiguration;
import cool.oriental.chatcove.configuration.minio.MinioEnum;
import io.minio.*;
import io.minio.messages.Item;
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

    public Boolean Upload(MultipartFile multipartFile, Integer channelId, MinioEnum minioEnum){
        switch (minioEnum.getType()){
            case "channel_avatar" -> {
                return ChannelAvatarUpload(multipartFile, channelId);
            }
            case "user" -> System.out.println("USER_AVATAR");
        }
        return Boolean.FALSE;
    }

    public Boolean Delete(Integer channelId, MinioEnum minioEnum){
        switch (minioEnum.getType()){
            case "channel_delete" -> {
                return ChannelDelete(channelId);
            }
            case "user" -> System.out.println("USER_AVATAR");
        }
        return Boolean.FALSE;
    }

    private Boolean ChannelAvatarUpload(MultipartFile multipartFile, Integer channelId){
        String objectName = channelId+"/"+"channelAvatar";
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

    private Boolean ChannelDelete(Integer channelId){
        MinioClient minioClient = minioConfiguration.MinioCreator();
        try {
            Iterable<Result<Item>> objectList = minioClient.listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket("channel")
                            .prefix(channelId.toString())
                            .recursive(true)
                            .build()
            );
            for (Result<Item> item : objectList) {
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket("channel")
                                .object(item.get().objectName())
                                .build()
                );
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除文件工具类异常，频道删除失败");
            return Boolean.FALSE;
        }
    }
}
