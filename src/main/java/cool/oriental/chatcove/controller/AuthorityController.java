package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.AuthorityService;
import cool.oriental.chatcove.vo.RegisterInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    AuthorityService authorityService;
    @PostMapping("/register")
    public Result<String> Register(@RequestBody @Valid RegisterInfo registerInfo){
        return authorityService.Register(registerInfo);
    }
}
