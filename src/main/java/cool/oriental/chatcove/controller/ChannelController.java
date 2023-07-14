package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.ChannelService;
import cool.oriental.chatcove.vo.channel.CreateChannelInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Result<String> CreateMasterChannel(@RequestBody @Valid @Parameter(description = "创建频道信息") CreateChannelInfo createChannelInfo){
        if(createChannelInfo.getAvatar().isEmpty()){
            return Result.error("上传文件为空");
        }
        if(createChannelInfo.getAvatar().getSize() > (5*1024*1024)){
            return Result.error("文件大小超出限制");
        }
        return channelService.CreateMasterChannel(createChannelInfo);
    }
}
