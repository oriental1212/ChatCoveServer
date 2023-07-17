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
    CHANNEL_AVATAR("channel_avatar","服务器头像上传"),
    CHANNEL_DELETE("channel_delete", "服务器头像删除"),
    CHANNEL_EMOJI_UPLOAD("channel_emoji_upload", "服务器表情上传"),
    CHANNEL_EMOJI_DELETE("channel_emoji_delete", "服务器表情删除"),
    USER_AVATAR("user_avatar", "用户头像上传");

    private final String type;
    private final String description;

}
