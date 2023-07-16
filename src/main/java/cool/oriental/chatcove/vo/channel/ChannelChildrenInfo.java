package cool.oriental.chatcove.vo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-16-13:49
 * @Description: 子频道信息类
 */

@Data
@Accessors(chain = true)
public class ChannelChildrenInfo {
    @NotNull(message = "主频道id不能为空")
    private Integer masterChannelId;
    private Integer groupId;
    @NotNull(message = "子频道名称不能为空")
    private String childrenChannelName;
    private String childrenChannelDescription;
    private Integer type;
    private String secret;
}
