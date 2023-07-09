package cool.oriental.chatcove.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-14:48
 * @Description: 好友关系状态
 */

@AllArgsConstructor
@Getter
public enum FriendsStatus {
    // 正常状态
    DEFAULT_STATUS(0),
    // 发送好友请求状态
    REQUESTING_STATUS(1),
    // 拉黑状态
    BLACK_STATUS(2);
    // 删除状态
    private final Integer status;
}
