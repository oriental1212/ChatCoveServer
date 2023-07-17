package cool.oriental.chatcove.vo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Oriental
 * @Date: 2023-07-17-14:13
 * @Description: 频道表情信息
 */

@Data
@Accessors(chain = true)
public class EmojiInfo {
    @NotNull(message = "频道为空")
    private Integer channelId;
    @NotNull(message = "上传用户为空")
    private Long userId;
    private MultipartFile emojiFile;
}
