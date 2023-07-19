package cool.oriental.chatcove.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-19-12:45
 * @Description: 频道隐私和通知设置
 */

@AllArgsConstructor
@Getter
public enum EnumChannelSetting {
    CHANNEL_DISTURB(1,"服务器免打扰"),
    SEND_PRIVATE_CHAT(2,"不允许发起私聊");

    private final Integer type;
    private final String content;
}
