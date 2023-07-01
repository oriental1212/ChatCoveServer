package cool.oriental.chatcove.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Oriental
 * @Date: 2023-07-01-15:09
 * @Description:
 */

@Tag(name = "控制器")
@RestController
public class TestController {
    @Operation(summary = "测试")
    @GetMapping("/test")
    public String getUserInfo(){
        return "Hello";
    }
}
