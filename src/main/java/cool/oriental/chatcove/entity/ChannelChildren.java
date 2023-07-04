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
@TableName("channel_children")
public class ChannelChildren implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 子频道信息表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 分组id
     */
    @TableField("group_id")
    private Integer groupId;

    /**
     * 创建者id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 子频道名称
     */
    @TableField("name")
    private String name;

    /**
     * 子频道描述
     */
    @TableField("description")
    private String description;

    /**
     * 子频道类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 子频道密码
     */
    @TableField("secret")
    private String secret;

    /**
     * 子频道创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
