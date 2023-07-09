package cool.oriental.chatcove.controller;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-14:31
 * @Description: 好友接口
 */

@RestController
@RequestMapping("/friends")
@Tag(name = "好友接口")
public class FriendsController {
    @Resource
    FriendsService friendsService;

    @PostMapping("/addFriends")
    @Operation(summary = "添加好友接口")
    public Result<String> AddFriends(@RequestParam(value = "friendsName") @Parameter(description = "添加好友姓名") String friendsName){
        return friendsService.AddFriends(friendsName);
    }

    @PostMapping("/confirmFriendsRequest")
    @Operation(summary = "确认好友请求接口")
    public Result<String> ConfirmFriendsRequest(
            @RequestParam(value = "friendRequestId") @Parameter(description = "好友请求表id") Integer friendRequestId,
            @RequestParam(value = "senderName") @Parameter(description = "发送者id") String senderName){
        return friendsService.ConfirmFriendsRequest(friendRequestId, senderName);
    }

    @PostMapping("/deleteFriends")
    @Operation(summary = "删除好友接口")
    public Result<String> DeleteFriends(@RequestParam(value = "friendId") @Parameter(description = "删除好友id") Long friendId){
        return friendsService.DeleteFriends(friendId);
    }

    @PostMapping("/blacklistFriends")
    @Operation(summary = "更改黑名单好友接口")
    public Result<String> BlacklistFriends(@RequestParam(value = "friendId") @Parameter(description = "黑名单好友id") Long friendId){
        return friendsService.BlacklistFriends(friendId);
    }

    @PostMapping("/showAllFriends")
    @Operation(summary = "展示所有用户接口")
    public Result<String> ShowAllFriends(){
        return friendsService.ShowAllFriends();
    }
}
