package cool.oriental.chatcove.configuration.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-12-10:34
 * @Description: Minio枚举类
 */

@AllArgsConstructor
@Getter
public enum MinioEnum {
    CHANNEL_AVATAR("channel","服务器头像上传"),
    CHANNEL_EMOJI("channel", "服务器表情上传"),
    USER_AVATAR("user", "用户头像上传");

    private final String type;
    private final String description;

}
