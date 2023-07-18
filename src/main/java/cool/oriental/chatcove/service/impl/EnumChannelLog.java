package cool.oriental.chatcove.service.impl;

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
    CHANNEL_INSERT(10,"添加频道"),
    CHANNEL_UPDATE(11,"更新频道"),
    CHANNEL_DELETE(12,"删除频道"),
    ROLE_INSERT(20,"添加角色"),
    ROLE_UPDATE(21,"更新角色"),
    ROLE_DELETE(22,"删除角色"),
    GROUP_INSERT(30,"添加组"),
    GROUP_UPDATE(31,"更新组"),
    GROUP_DELETE(32,"删除组"),
    EMOJI_INSERT(40,"上传图片"),
    EMOJI_DELETE(41,"删除图片"),
    USER_DELETE(50,"删除用户");

    private final Integer type;
    private final String content;
}
