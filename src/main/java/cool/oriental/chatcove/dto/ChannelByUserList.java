package cool.oriental.chatcove.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-19-15:21
 * @Description: 用户加入频道列表
 */

@Data
@Accessors(chain = true)
public class ChannelByUserList {
    private Integer channelId;
    private String channelName;
    private String channelAvatar;
}
