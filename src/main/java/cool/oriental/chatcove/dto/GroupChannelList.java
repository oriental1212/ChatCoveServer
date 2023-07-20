package cool.oriental.chatcove.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-20-12:59
 * @Description: 频道分组列表
 */

@Data
@Accessors(chain = true)
public class GroupChannelList {
    private Integer groupId;
    private String groupName;
    private List<ChildrenChannelList> childrenChannelList;
}
