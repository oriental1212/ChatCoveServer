package cool.oriental.chatcove.controller;


import cool.oriental.chatcove.configuration.mail.EnumMail;
import cool.oriental.chatcove.configuration.mail.MailConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-15:09
 * @Description: test
 */

@Tag(name = "Test")
@RestController
public class TestController {
    @Resource
    MailConfiguration mailConfiguration;
    @Operation(summary = "测试")
    @GetMapping("/test")
    public String test(){
        mailConfiguration.sendTemplateMail("1304206691@qq.com", "123456", EnumMail.EMAIL_CAPTCHA);
        return "Success";
    }
}
