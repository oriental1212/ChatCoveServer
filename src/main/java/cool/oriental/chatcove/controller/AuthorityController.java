package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.AuthorityService;
import cool.oriental.chatcove.vo.LoginByCaptchaInfo;
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
    public Result<String> SendCaptcha(@RequestParam(value = "account") @Parameter(description = "用户账户") String account){
        return authorityService.SendCaptcha(account);
    }

    @PostMapping("/loginByCaptcha")
    @Operation(summary = "验证码用户登录接口")
    public Result<String> LoginByCaptcha(@RequestBody @Valid @Parameter(description = "验证码用户登录vo类") LoginByCaptchaInfo loginByCaptchaInfo){
        return authorityService.LoginByCaptcha(loginByCaptchaInfo);
    }
}
