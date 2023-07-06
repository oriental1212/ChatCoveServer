package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.vo.ChangePasswordInfo;
import cool.oriental.chatcove.vo.CheckByCaptchaInfo;
import cool.oriental.chatcove.vo.LoginInfo;
import cool.oriental.chatcove.vo.RegisterInfo;

/**
 * @Author: Oriental
 * @Date: 2023-07-04-15:10
 * @Description: 用户权限服务接口
 */

public interface AuthorityService {
    Result<String> Register(RegisterInfo registerInfo);
    Result<String> LoginDefault(LoginInfo loginInfo);
    Result<String> LoginByCaptcha(CheckByCaptchaInfo checkByCaptchaInfo);
    Result<String> SendCaptcha(String account, String sendFlag);
    Result<String> FindPassword(CheckByCaptchaInfo checkByCaptchaInfo);
    Result<String> ChangePassword(ChangePasswordInfo changePasswordInfo);
}
