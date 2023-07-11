package cool.oriental.chatcove.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-9:17
 * @Description: 更改用户设置类
 */

@Data
@Accessors(chain = true)
public class ChangeUserSetting {
    private String nickName;
    private String avatar;
    private String summery;
    private String gender;
    private LocalDate birthday;
    private String location;
}
