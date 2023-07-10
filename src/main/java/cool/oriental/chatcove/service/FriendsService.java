package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.exception.Result;
import cool.oriental.chatcove.vo.AllFriendsInfo;
import cool.oriental.chatcove.vo.FriendsRequestInfo;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-14:29
 * @Description: 好友服务接口
 */
public interface FriendsService {
    Result<String> AddFriends(String friendsName);
    Result<String> ConfirmFriendsRequest(Integer friendRequestId, String senderName);
    Result<String> DeleteFriends(Long friendId);
    Result<String> BlacklistFriends(Long friendId);
    Result<String> RemarkFriends(Long friendId, String remarkName);
    Result<List<AllFriendsInfo>> ShowAllFriends();
    Result<List<FriendsRequestInfo>> GetFriendsRequest();
}
