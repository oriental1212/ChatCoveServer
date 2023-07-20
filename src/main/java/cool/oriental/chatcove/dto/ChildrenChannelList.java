package cool.oriental.chatcove.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-20-13:03
 * @Description: 子频道列表
 */

@Data
@Accessors(chain = true)
public class ChildrenChannelList {
    private Integer childrenChannelId;
    private String childrenChannelName;
    private String childrenChannelDescription;
    private Integer childrenChannelType;
    private String childrenChannelSecret;
}
