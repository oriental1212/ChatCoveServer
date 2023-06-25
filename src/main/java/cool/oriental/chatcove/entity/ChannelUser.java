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
@TableName("channel_user")
public class ChannelUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户信息表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 频道内用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 角色id
     */
    @TableField("role_id")
    private Integer roleId;

    /**
     * 用户加入频道时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 离开进入频道时间
     */
    @TableField("active_time")
    private LocalDateTime activeTime;
}
