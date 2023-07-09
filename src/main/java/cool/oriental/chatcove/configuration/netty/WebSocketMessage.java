package cool.oriental.chatcove.configuration.netty;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-11:00
 * @Description:
 */

@Data
@Accessors(chain = true)
public class WebSocketMessage {
    private String messageType;
    private String receiverType;
    private String receiverName;
    private String content;
    private String createTime;
}
