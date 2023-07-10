package cool.oriental.chatcove.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.oriental.chatcove.entity.FriendInfo;
import cool.oriental.chatcove.vo.AllFriendsInfo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oriental
 * @since 2023-06-25 01:23:32
 */
public interface FriendInfoMapper extends BaseMapper<FriendInfo> {
    List<AllFriendsInfo> ShowAllFriends(Long userId);
}
