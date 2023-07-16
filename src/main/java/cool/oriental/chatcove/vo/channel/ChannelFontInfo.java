package cool.oriental.chatcove.vo.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Oriental
 * @Date: 2023-07-12-9:19
 * @Description: 主频道信息类
 */

@Data
@Accessors(chain = true)
public class ChannelFontInfo {
    @NotNull(message = "频道名称不能为空")
    private String channelName;
    private MultipartFile avatar;
    private String description;
}
