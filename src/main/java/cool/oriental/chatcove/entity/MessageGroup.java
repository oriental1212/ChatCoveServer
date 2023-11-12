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
@TableName("message_group")
public class MessageGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群聊消息表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送用户id
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 子频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 消息类型（0是文本消息，1是图片消息，2是音频消息）
     */
    @TableField("type")
    private Integer type;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 回复消息id
     */
    @TableField("reply_id")
    private Integer replyId;

    /**
     * 创建消息时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
