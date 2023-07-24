package cool.oriental.chatcove.configuration.netty;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-11:00
 * @Description:
 */

@Data
@Accessors(chain = true)
public class WebSocketMessage {
    /*
     * private: 私聊
     * group: 群聊
    */
    private String type;
    // 接收id，频道id或者是用户id，取决于type
    private String receiverId;
    // 消息类型
    private String messageType;
    // 消息内容
    private Object content;
    // 回复id，没有为null
    private Integer replyId;
    // 创建时间
    private LocalDateTime createTime;
}
