package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.AuthorityService;
import cool.oriental.chatcove.vo.ChangePasswordInfo;
import cool.oriental.chatcove.vo.CheckByCaptchaInfo;
import cool.oriental.chatcove.vo.LoginInfo;
import cool.oriental.chatcove.vo.RegisterInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Oriental
 * @Date: 2023-07-04-14:35
 * @Description: 用户权限接口
 */

@RestController
@RequestMapping("/authority")
@Tag(name = "用户权限接口")
public class AuthorityController {
    @Resource
    private AuthorityService authorityService;
    @PostMapping("/register")
    @Operation(summary = "用户注册接口")
    public Result<String> Register(@RequestBody @Valid @Parameter(description = "用户注册vo类") RegisterInfo registerInfo){
        return authorityService.Register(registerInfo);
    }

    @PostMapping("/loginDefault")
    @Operation(summary = "普通用户登录接口")
    public Result<String> LoginDefault(@RequestBody @Valid @Parameter(description = "用户登录vo类") LoginInfo loginInfo){
        return authorityService.LoginDefault(loginInfo);
    }

    @PostMapping("/sendCaptcha")
    @Operation(summary = "发送验证码接口")
    public Result<String> SendCaptcha(@RequestParam(value = "account") @Parameter(description = "用户账户") String account,@RequestParam(value = "sendFlag") @Parameter(description = "发送标志位") String sendFlag){
        return authorityService.SendCaptcha(account,sendFlag);
    }

    @PostMapping("/loginByCaptcha")
    @Operation(summary = "验证码用户登录接口")
    public Result<String> LoginByCaptcha(@RequestBody @Valid @Parameter(description = "验证码校验vo类") CheckByCaptchaInfo checkByCaptchaInfo){
        return authorityService.LoginByCaptcha(checkByCaptchaInfo);
    }

    @PostMapping("/findPassword")
    @Operation(summary = "请求找回密码接口")
    public Result<String> FindPassword(@RequestBody @Valid @Parameter(description = "验证码校验vo类") CheckByCaptchaInfo checkByCaptchaInfo){
        return authorityService.FindPassword(checkByCaptchaInfo);
    }

    @PostMapping("/changePassword")
    @Operation(summary = "修改密码接口")
    public Result<String> ChangePassword(@RequestBody @Valid @Parameter(description = "用户修改密码vo类") ChangePasswordInfo changePasswordInfo){
        return authorityService.ChangePassword(changePasswordInfo);
    }
}
