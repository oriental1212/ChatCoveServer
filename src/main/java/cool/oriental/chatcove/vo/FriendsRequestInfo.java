package cool.oriental.chatcove.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-10-15:48
 * @Description: 获取好友请求返回类
 */

@Data
@Accessors(chain = true)
public class FriendsRequestInfo {
    private Integer requestId;
    private String requestName;
    private String requestAvatar;
}
