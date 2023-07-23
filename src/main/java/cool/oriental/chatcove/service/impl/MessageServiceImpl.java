package cool.oriental.chatcove.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.netty.NettyConfiguration;
import cool.oriental.chatcove.entity.FriendInfo;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.FriendInfoMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.MessageService;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Oriental
 * @Date: 2023-07-21-11:37
 * @Description: 消息服务实现类
 */

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    @Resource
    FriendInfoMapper friendInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;

    // 通知好友上线
    @Override
    public Boolean friendNotifyOnline(Long userId) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(UserInfo::getId, userId)
                .set(UserInfo::getStatus, Boolean.TRUE);
        try {
            friendNotify(userId, "online");
            userInfoMapper.update(null, updateWrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("好友通知用户上线业务异常");
            return false;
        }
    }

    @Override
    public Boolean friendNotifyOffline(Long userId) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(UserInfo::getId, userId)
                .set(UserInfo::getStatus, Boolean.FALSE);
        try {
            friendNotify(userId, "offline");
            userInfoMapper.update(null, updateWrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("好友通知用户下线业务异常");
            return false;
        }
    }

    @Override
    public Boolean groupSendMessage() {
        return null;
    }

    @Override
    public Boolean privateSendMessage() {
        return null;
    }

    private void friendNotify(Long userId, String type){
        List<FriendInfo> friendList = friendInfoMapper.selectList(
                new LambdaQueryWrapper<FriendInfo>()
                        .eq(FriendInfo::getFriendId, userId)
        );
        if(friendList == null){
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        for (FriendInfo friendInfo : friendList) {
            Channel channel = NettyConfiguration.getOnlineUserMap().get(friendInfo.getUserId());
            if(channel != null){
                jsonObject.put("userId", userId);
                channel.writeAndFlush(jsonObject);
            }
        }
    }
}
