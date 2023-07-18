package cool.oriental.chatcove.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Oriental
 * @Date: 2023-07-18-16:02
 * @Description: 返回前端的日志实体类
 */

@Data
@Accessors(chain = true)
public class ChannelLogList implements Serializable {
    private String userName;
    private String avatarUrl;
    private Integer logType;
    private String content;
    private LocalDateTime createTime;
}
