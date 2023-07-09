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
 * @Author: Oriental
 * @Date: 2023-07-09-15:20
 * @Description: 好友请求实体类
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("friend_request")
public class FriendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 好友请求id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送者id
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者id
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
