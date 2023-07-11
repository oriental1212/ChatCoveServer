package cool.oriental.chatcove.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-06-15:24
 * @Description: 更改用户密码类
 */

@Data
@Accessors(chain = true)
public class ChangePasswordInfo {
    @NotNull(message = "用户账户不能为空")
    private String account;
    @NotNull(message = "用户验密码不能为空")
    private String password;
    @NotNull(message = "用户验重复密码不能为空")
    private String repeatPassword;
}
