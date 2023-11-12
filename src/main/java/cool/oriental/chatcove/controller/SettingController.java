package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.SettingService;
import cool.oriental.chatcove.vo.ChangeUserSetting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Oriental
 * @Date: 2023-07-10-17:03
 * @Description: 用户设置接口
 */

@RestController
@RequestMapping("/setting")
@Tag(name = "用户设置接口")
public class SettingController {
    @Resource
    SettingService settingService;
    @PostMapping("/changeUserSetting")
    @Operation(summary = "更改用户设置")
    public Result<String> ChangeUserSetting(@RequestBody @Valid @Parameter(description = "用户设置修改类") ChangeUserSetting changeUserSetting){
        return settingService.ChangeUserSetting(changeUserSetting);
    }

    @PostMapping("/uploadUserAvatar")
    @Operation(summary = "上传用户头像")
    public Result<String> UploadUserAvatar(@RequestBody @Parameter(description = "用户头像") MultipartFile multipartFile){
        if(multipartFile.isEmpty()){
            return Result.error("上传文件为空");
        }
        if(multipartFile.getSize() > (5*1024*1024)){
            return Result.error("文件大小超出限制");
        }
        return settingService.UploadUserAvatar(multipartFile);
    }

    @PostMapping("/sendCaptchaToChangPassword")
    @Operation(summary = "发送验证码核验用户修改密码")
    public Result<String> SendCaptchaToChangPassword(){
        return settingService.SendCaptchaToChangPassword();
    }

    @PostMapping("/changePassword")
    @Operation(summary = "用户修改密码")
    public Result<String> ChangePassword(
            @RequestParam(value = "password") @Parameter(description = "用户密码") String password,
            @RequestParam(value = "captcha") @Parameter(description = "用户密码") String captcha
    ){
        return settingService.ChangePassword(password, captcha);
    }
}
