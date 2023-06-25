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
@TableName("message_private")
public class MessagePrivate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 私聊消息表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送用户id
     */
    @TableField("sender_id")
    private Integer senderId;

    /**
     * 接收用户id
     */
    @TableField("receiver_id")
    private Integer receiverId;

    /**
     * 消息类型
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

    /**
     * 消息读取标志
     */
    @TableField("flag")
    private Boolean flag;
}
