package cool.oriental.chatcove.configuration.netty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-22-18:43
 * @Description: webSocket消息接收枚举类
 */

@Getter
@AllArgsConstructor
public enum EnumMessageType {
    // 私聊
    PRIVATE_CHAT,
    // 群聊
    GROUP_CHAT,
    // 注册
    REGISTER;
}
