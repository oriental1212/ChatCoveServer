package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.vo.ChangeUserSetting;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Oriental
 * @Date: 2023-07-10-16:59
 * @Description: 用户设置服务接口
 */
public interface SettingService {
    Result<String> ChangeUserSetting(ChangeUserSetting changeUserSetting);
    Result<String> SendCaptchaToChangPassword();
    Result<String> UploadUserAvatar(MultipartFile multipartFile);
    Result<String> ChangePassword(String password, String captcha);
}
