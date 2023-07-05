package cool.oriental.chatcove.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Oriental
 * @Date: 2023-07-05-11:23
 * @Description: 验证码用户登录信息
 */

@Data
@Accessors(chain = true)
public class LoginByCaptchaInfo {
    @NotNull(message = "用户账户不能为空")
    private String account;
    @NotNull(message = "用户验证码不能为空")
    private String captcha;
}
