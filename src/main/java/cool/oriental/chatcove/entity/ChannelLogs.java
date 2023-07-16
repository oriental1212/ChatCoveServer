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
@TableName("channel_logs")
public class ChannelLogs implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 频道日志id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 主频道id
     */
    @TableField("channel_id")
    private Integer channelId;

    /**
     * 生成日志用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 日志生成时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 日志类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 日志内容
     */
    @TableField("content")
    private String content;
}
