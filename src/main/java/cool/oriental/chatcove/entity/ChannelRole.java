package cool.oriental.chatcove.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("channel_role")
public class ChannelRole implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色信息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色颜色
     */
    @TableField("color")
    private String color;

    /**
     * 角色权限
     */
    @TableField("authority")
    private String authority;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
