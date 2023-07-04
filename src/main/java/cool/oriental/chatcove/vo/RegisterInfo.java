package cool.oriental.chatcove.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-04-15:19
 * @Description: 用户注册信息
 */

@Data
@Accessors(chain = true)
public class RegisterInfo {
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "用户密码不能为空")
    private String password;
    @NotNull(message = "用户邮箱不能为空")
    private String email;
}
