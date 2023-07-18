package cool.oriental.chatcove.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-18-14:32
 * @Description: 角色权限枚举类
 */

@AllArgsConstructor
@Getter
public enum EnumRole {
    // 0 是管理员
    NORMAL_USER("1","普通用户"),
    REVIEW_LOG("2","查看日志"),
    CHILDREN_CHANNEL_CONTROLLER("3","子频道管理"),
    GROUP_CONTROLLER("4","分组管理"),
    DELETE_USER_CONTROLLER("5","删除频道人员"),
    EMOJI_CONTROLLER("6","频道自定义表情管理"),
    CHANGE_USER_NAME("7","修改频道用户名称");

    private final String type;
    private final String authority;
}
