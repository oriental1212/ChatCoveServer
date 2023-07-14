package cool.oriental.chatcove.vo.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-12-15:30
 * @Description: 频道日志枚举类
 */
@AllArgsConstructor
@Getter
public enum EnumChannelLog {
    CHANNEL_INSERT,
    CHANNEL_UPDATE,
    CHANNEL_DELETE,
    ROLE_INSERT,
    ROLE_UPDATE,
    ROLE_DELETE,
    GROUP_INSERT,
    GROUP_UPDATE,
    GROUP_DELETE;
}
