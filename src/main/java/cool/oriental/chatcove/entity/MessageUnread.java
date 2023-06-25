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
@TableName("message_unread")
public class MessageUnread implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群聊信息未读表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 子频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 频道中用户最后活跃时间
     */
    @TableField("active_time")
    private LocalDateTime activeTime;
}
