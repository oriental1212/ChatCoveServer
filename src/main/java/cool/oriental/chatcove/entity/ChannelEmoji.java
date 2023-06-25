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
@TableName("channel_emoji")
public class ChannelEmoji implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 表情信息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 表情名称
     */
    @TableField("name")
    private String name;

    /**
     * 表情链接
     */
    @TableField("link")
    private String link;

    /**
     * 上传时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 上传用户id
     */
    @TableField("user_id")
    private Integer userId;
}
