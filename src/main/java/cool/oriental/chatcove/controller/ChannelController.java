package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.ChannelService;
import cool.oriental.chatcove.vo.channel.ChannelChildrenInfo;
import cool.oriental.chatcove.vo.channel.ChannelFontInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public Result<String> CreateMasterChannel(
            @RequestBody @Valid @Parameter(description = "创建频道信息") ChannelFontInfo channelFontInfo
    ){
        if(channelFontInfo.getAvatar().isEmpty()){
            return Result.error("上传文件为空");
        }
        if(channelFontInfo.getAvatar().getSize() > (5*1024*1024)){
            return Result.error("文件大小超出限制");
        }
        return channelService.CreateMasterChannel(channelFontInfo);
    }

    @PostMapping("/updateMasterChannel/{channelId}/{updateAvatarFlag}")
    @Operation(summary = "更新主频道信息")
    public Result<String> UpdateMasterChannel(
            @RequestBody @Valid @Parameter(description = "更新频道信息") ChannelFontInfo channelFontInfo,
            @PathVariable Integer channelId,
            @PathVariable Boolean updateAvatarFlag
    ){
        return channelService.UpdateMasterChannel(channelFontInfo, channelId, updateAvatarFlag);
    }

    @GetMapping("/deleteChannel/{channelId}")
    @Operation(summary = "删除主频道")
    public Result<String> DeleteMasterChannel(
            @PathVariable Integer channelId
    ){
        return channelService.DeleteMasterChannel(channelId);
    }

    @PostMapping("/createChildrenChannel")
    @Operation(summary = "创建子频道")
    public Result<String> CreateChildrenChannel(
            @RequestBody @Valid @Parameter(description = "子频道信息") ChannelChildrenInfo channelChildrenInfo
    ){
        return channelService.CreateChildrenChannel(channelChildrenInfo);
    }

    @PostMapping("/updateChildrenChannel/{childrenChannelId}")
    @Operation(summary = "更新子频道")
    public Result<String> UpdateChildrenChannel(
            @RequestBody @Valid @Parameter(description = "子频道信息") ChannelChildrenInfo channelChildrenInfo,
            @PathVariable Integer childrenChannelId
    ){
        return channelService.UpdateChildrenChannel(channelChildrenInfo, childrenChannelId);
    }

    @GetMapping("/deleteChannel/{channelId}/{childrenChannelId}")
    @Operation(summary = "删除子频道")
    public Result<String> DeleteChildrenChannel(
            @PathVariable Integer channelId,
            @PathVariable Integer childrenChannelId
    ){
        return channelService.DeleteChildrenChannel(channelId, childrenChannelId);
    }

}
