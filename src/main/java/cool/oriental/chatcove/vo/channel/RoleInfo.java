package cool.oriental.chatcove.vo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-17-9:38
 * @Description: 频道角色实体类
 */

@Data
@Accessors(chain = true)
public class RoleInfo {
    @NotNull(message = "频道id不能为空")
    Integer channelId;
    @NotNull(message = "角色名称不能为空")
    String name;
    @NotNull(message = "角色颜色不能为空")
    String color;
    @NotNull(message = "角色权限不能为空")
    String authority;
}
