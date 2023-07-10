package cool.oriental.chatcove.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.oriental.chatcove.entity.FriendRequest;
import cool.oriental.chatcove.vo.FriendsRequestInfo;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-09-15:25
 * @Description: 好友关系mapper接口
 */
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {
    List<FriendsRequestInfo> GetFriendsRequest(Long userId);
}
