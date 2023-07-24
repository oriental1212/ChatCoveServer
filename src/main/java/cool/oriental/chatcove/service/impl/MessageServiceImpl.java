package cool.oriental.chatcove.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.netty.NettyConfiguration;
import cool.oriental.chatcove.configuration.netty.WebSocketMessage;
import cool.oriental.chatcove.dto.ChannelMessage;
import cool.oriental.chatcove.entity.FriendInfo;
import cool.oriental.chatcove.entity.MessageGroup;
import cool.oriental.chatcove.entity.MessagePrivate;
import cool.oriental.chatcove.entity.UserInfo;
import cool.oriental.chatcove.mapper.FriendInfoMapper;
import cool.oriental.chatcove.mapper.MessageGroupMapper;
import cool.oriental.chatcove.mapper.MessagePrivateMapper;
import cool.oriental.chatcove.mapper.UserInfoMapper;
import cool.oriental.chatcove.service.MessageService;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
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
    @Resource
    MessagePrivateMapper messagePrivateMapper;
    @Resource
    MessageGroupMapper messageGroupMapper;

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
    public Boolean groupSendMessage(Channel userChannel, WebSocketMessage webSocketMessage) {
        switch (webSocketMessage.getMessageType()){
            case "text" -> {
                return groupSendTextMessage(userChannel, webSocketMessage);
            }
            case "multipartFile" -> {
                return groupSendMultipartFileMessage(userChannel, webSocketMessage);
            }
            default -> {
                return Boolean.FALSE;
            }
        }
    }

    @Override
    public Boolean privateSendMessage(Channel userChannel, WebSocketMessage webSocketMessage) {
        switch (webSocketMessage.getMessageType()){
            case "text" ->{
                return privateSendTextMessage(userChannel, webSocketMessage);
            }
            case "multipartFile" -> {
                return privateSendMultipartFileMessage(userChannel, webSocketMessage);
            }
            default -> {
                return Boolean.FALSE;
            }
        }
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
                channel.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));
            }
        }
    }

    private Boolean privateSendTextMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        MessagePrivate messagePrivate = new MessagePrivate();
        Long sendId = (Long) userChannel.attr(AttributeKey.valueOf("userId")).get();
        messagePrivate
                .setSenderId(sendId)
                .setReceiverId(Long.parseLong(webSocketMessage.getReceiverId()))
                .setType(0)
                .setContent((String) webSocketMessage.getContent())
                .setReplyId(webSocketMessage.getReplyId())
                .setCreateTime(webSocketMessage.getCreateTime());
        Channel receiverChannel = NettyConfiguration.getOnlineUserMap().get(Long.parseLong(webSocketMessage.getReceiverId()));
        try {
            // 存储消息
            int insertFlag = messagePrivateMapper.insert(messagePrivate);
            // 好友在线
            if(receiverChannel != null){
                messagePrivate.setFlag(true);
                LambdaQueryWrapper<MessagePrivate> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper
                        .eq(MessagePrivate::getSenderId, sendId)
                        .eq(MessagePrivate::getReceiverId, webSocketMessage.getReceiverId())
                        .eq(MessagePrivate::getCreateTime, webSocketMessage.getCreateTime());
                MessagePrivate messagePrivateOne = messagePrivateMapper.selectOne(queryWrapper);
                receiverChannel.writeAndFlush(new TextWebSocketFrame(messagePrivateOne.toString()));
            // 好友不在线
            }else{
                messagePrivate.setFlag(false);
            }
            return (insertFlag>0) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("私聊发送文本消息业务异常");
            return Boolean.FALSE;
        }

    }

    private Boolean groupSendTextMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        Long sendId = (Long) userChannel.attr(AttributeKey.valueOf("userId")).get();
        // 存储消息类
        MessageGroup messageGroup = new MessageGroup();
        messageGroup
                .setSenderId(sendId)
                .setChannelId(Integer.parseInt(webSocketMessage.getReceiverId()))
                .setType(0)
                .setContent((String) webSocketMessage.getContent())
                .setReplyId(webSocketMessage.getReplyId())
                .setCreateTime(webSocketMessage.getCreateTime());
        // 存储消息
        try {
            int insertFlag = messageGroupMapper.insert(messageGroup);
            // 群发消息类
            ChannelMessage channelMessageOne = messageGroupMapper.GetChannelMessageOne(sendId, Integer.parseInt(webSocketMessage.getReceiverId()), webSocketMessage.getCreateTime());
            // 群发消息
            ChannelGroup groupChannel = NettyConfiguration.getOnlineChannelMap().get(Integer.parseInt(webSocketMessage.getReceiverId()));
            groupChannel.writeAndFlush(new TextWebSocketFrame(channelMessageOne.toString()));
            return (insertFlag>0)?Boolean.TRUE:Boolean.FALSE;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("群发发送文本消息业务异常");
            return Boolean.FALSE;
        }
    }

    private Boolean privateSendMultipartFileMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        return Boolean.TRUE;
    }

    private Boolean groupSendMultipartFileMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        return Boolean.TRUE;
    }
}
