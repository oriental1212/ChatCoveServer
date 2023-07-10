package cool.oriental.chatcove.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-10-14:49
 * @Description: 返回所有用户的信息
 */

@Data
@Accessors(chain = true)
public class AllFriendsInfo {
    private Long userId;
    private Long friendId;
    private Integer status;
    private String remarkName;
    private String friendAvatar;
    private Boolean friendStatus;
}
