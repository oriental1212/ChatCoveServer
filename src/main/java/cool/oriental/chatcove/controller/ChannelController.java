package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.dto.ChannelByUserList;
import cool.oriental.chatcove.dto.ChannelLogList;
import cool.oriental.chatcove.dto.ChannelMessage;
import cool.oriental.chatcove.dto.GroupChannelList;
import cool.oriental.chatcove.service.ChannelService;
import cool.oriental.chatcove.vo.channel.ChannelChildrenInfo;
import cool.oriental.chatcove.vo.channel.ChannelFontInfo;
import cool.oriental.chatcove.vo.channel.EmojiInfo;
import cool.oriental.chatcove.vo.channel.RoleInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @PathVariable @Parameter(description = "频道id") Integer channelId,
            @PathVariable @Parameter(description = "更新频道头像标志") Boolean updateAvatarFlag
    ){
        return channelService.UpdateMasterChannel(channelFontInfo, channelId, updateAvatarFlag);
    }

    @GetMapping("/deleteChannel/{channelId}")
    @Operation(summary = "删除主频道")
    public Result<String> DeleteMasterChannel(
            @PathVariable @Parameter(description = "频道id") Integer channelId
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
            @PathVariable @Parameter(description = "子频道id") Integer childrenChannelId
    ){
        return channelService.UpdateChildrenChannel(channelChildrenInfo, childrenChannelId);
    }

    @GetMapping("/deleteChannel/{channelId}/{childrenChannelId}")
    @Operation(summary = "删除子频道")
    public Result<String> DeleteChildrenChannel(
            @PathVariable @Parameter(description = "频道id") Integer channelId,
            @PathVariable @Parameter(description = "子频道id") Integer childrenChannelId
    ){
        return channelService.DeleteChildrenChannel(channelId, childrenChannelId);
    }

    @PostMapping("/createRole")
    @Operation(summary = "增加新角色")
    public Result<String> CreateRole(
            @RequestBody @Valid @Parameter(description = "频道角色信息") RoleInfo roleInfo
    ){
        return channelService.CreateRole(roleInfo);
    }

    @PostMapping("/updateRole/{roleId}")
    @Operation(summary = "更新角色")
    public Result<String> UpdateRole(
            @RequestBody @Valid @Parameter(description = "频道角色信息") RoleInfo roleInfo,
            @PathVariable @Parameter(description = "频道角色Id") Integer roleId
    ){
        return channelService.UpdateRole(roleInfo,roleId);
    }

    @PostMapping("/deleteRole/{channelId}/{roleId}")
    @Operation(summary = "删除角色")
    public Result<String> DeleteRole(
            @PathVariable @Parameter(description = "频道id") Integer channelId,
            @PathVariable @Parameter(description = "频道角色id") Integer roleId
    ){
        return channelService.DeleteRole(channelId,roleId);
    }

    @PostMapping("/uploadEmoji")
    @Operation(summary = "上传表情")
    public Result<String> UploadEmoji(
            @RequestBody @Valid @Parameter(description = "频道表情信息") EmojiInfo emojiInfo
    ){
        if(emojiInfo.getEmojiFile().isEmpty()){
            return Result.error("上传文件为空");
        }
        if(emojiInfo.getEmojiFile().getSize() > (5*1024*1024)){
            return Result.error("文件大小超出限制");
        }
        return channelService.UploadEmoji(emojiInfo);
    }

    @PostMapping("/deleteEmoji/{channelId}/{emojiId}")
    @Operation(summary = "删除表情")
    public Result<String> DeleteEmoji(
            @PathVariable @Parameter(description = "频道id") Integer channelId,
            @PathVariable @Parameter(description = "频道表情id") Integer emojiId
    ){
        return channelService.DeleteEmoji(channelId, emojiId);
    }

    @PostMapping("/createUser")
    @Operation(summary = "新增频道人员")
    public Result<String> CreateUser(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "用户昵称") String nickName
    ){
        return channelService.CreateUser(channelId, nickName);
    }

    @PostMapping("/changeUserName")
    @Operation(summary = "改变用户频道昵称")
    public Result<String> ChangeUserName(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "用户修改昵称") String remarkNickName
    ){
        return channelService.ChangeUserName(channelId, remarkNickName);
    }

    @PostMapping("/deleteUser")
    @Operation(summary = "删除频道人员")
    public Result<String> DeleteUser(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "用户id") Long userId
    ){
        return channelService.DeleteUser(channelId, userId);
    }

    @PostMapping("/exitChannel")
    @Operation(summary = "用户主动退出频道接口")
    public Result<String> ExitChannel(
            @RequestParam @Parameter(description = "频道id") Integer channelId
    ){
        return channelService.ExitChannel(channelId);
    }

    @GetMapping("/getChannelLog")
    @Operation(summary = "获取频道日志接口")
    public Result<List<ChannelLogList>> GetChannelLog(
            @RequestParam @Parameter(description = "频道id") Integer channelId
    ){
        return channelService.GetChannelLog(channelId);
    }

    @PostMapping("/createGroup")
    @Operation(summary = "创建频道分组")
    public Result<String> CreateGroup(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "分组名称") String groupName
    ){
        return channelService.CreateGroup(channelId, groupName);
    }

    @PostMapping("/updateGroup")
    @Operation(summary = "更新频道分组")
    public Result<String> updateGroup(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "分组id") Integer groupId,
            @RequestParam @Parameter(description = "分组更新名称") String remarkGroupName
    ){
        return channelService.updateGroup(channelId, groupId, remarkGroupName);
    }

    @PostMapping("/deleteGroup")
    @Operation(summary = "删除频道分组")
    public Result<String> DeleteGroup(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "分组id") Integer groupId
    ){
        return channelService.DeleteGroup(channelId, groupId);
    }

    @PostMapping("/changeChannelSetting")
    @Operation(summary = "更改频道设置")
    public Result<String> ChangeChannelSetting(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "设置类型") Integer type
    ){
        return channelService.ChangeChannelSetting(channelId, type);
    }

    @GetMapping("/getChannelList")
    @Operation(summary = "获取用户频道列表")
    public Result<List<ChannelByUserList>> GetChannelList()
    {
        return channelService.GetChannelList();
    }

    @GetMapping("/getChildrenChannelList")
    @Operation(summary = "获取子频道列表")
    public Result<List<GroupChannelList>> GetChildrenChannelList(
            @RequestParam @Parameter(description = "频道id") Integer chanelId
    ){
        return channelService.GetChildrenChannelList(chanelId);
    }

    @GetMapping("/getChildrenChannelInfo")
    @Operation(summary = "获取子频道聊天信息")
    public Result<List<ChannelMessage>> GetChildrenChannelMessage(
            @RequestParam @Parameter(description = "频道id") Integer channelId,
            @RequestParam @Parameter(description = "子频道id") Integer childrenChannelId
    ){
        return channelService.GetChildrenChannelMessage(channelId ,childrenChannelId);
    }

}
