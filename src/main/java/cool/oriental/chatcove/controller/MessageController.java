package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Oriental
 * @Date: 2023-07-21-11:38
 * @Description: 消息接口
 */

@RestController
@RequestMapping("/message")
@Tag(name = "消息接口")
public class MessageController {
    @Resource
    MessageService messageService;

}
