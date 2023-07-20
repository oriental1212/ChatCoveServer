package cool.oriental.chatcove.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author: Oriental
 * @Date: 2023-07-20-15:57
 * @Description: 频道消息类
 */

@Data
@Accessors(chain = true)
public class ChannelMessage {
    private Integer messageId;
    private String senderName;
    private String senderAvatar;
    private Integer replyId;
    private Integer messageType;
    private String messageContent;
    private LocalDateTime createTime;
}
