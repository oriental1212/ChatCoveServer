package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Oriental
 * @Date: 2023-07-11-10:50
 * @Description: 频道接口
 */

@RestController
@RequestMapping("/channel")
@Tag(name = "频道接口")
public class ChannelController {
    @Resource
    private ChannelService channelService;

    @PostMapping("/createMasterChannel")
    @Operation(summary = "创建总频道")
    public Result<String> CreateMasterChannel(){
        return channelService.CreateMasterChannel();
    }
}
