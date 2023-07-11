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
    public Result<String> ChangeUserSetting(@RequestBody @Valid @Parameter(description = "用户注册vo类") ChangeUserSetting changeUserSetting){
        return settingService.ChangeUserSetting(changeUserSetting);
    }

    @PostMapping("/sendCaptchaToChangPassword")
    @Operation(summary = "发送验证码核验用户修改密码")
    public Result<String> SendCaptchaToChangPassword(){
        return settingService.SendCaptchaToChangPassword();
    }

    @PostMapping("/sendCaptchaToChangPassword")
    @Operation(summary = "发送验证码核验用户修改密码")
    public Result<String> ChangePassword(
            @RequestParam(value = "password") @Parameter(description = "用户密码") String password,
            @RequestParam(value = "captcha") @Parameter(description = "用户密码") String captcha
    ){
        return settingService.ChangePassword(password, captcha);
    }
}
