package cool.oriental.chatcove.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cool.oriental.chatcove.configuration.netty.NettyConfiguration;
import cool.oriental.chatcove.configuration.netty.WebSocketMessage;
import cool.oriental.chatcove.dto.ChannelMessage;
import cool.oriental.chatcove.entity.*;
import cool.oriental.chatcove.mapper.*;
import cool.oriental.chatcove.service.MessageService;
import cool.oriental.chatcove.utils.MinioTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    ChannelEmojiMapper channelEmojiMapper;
    @Value("${minio.url}")
    private String minioServerUrl;

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
        // 存储消息类
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
            int insertFlag;
            // 好友在线
            if(receiverChannel != null){
                messagePrivate.setFlag(true);
                // 存储消息
                insertFlag = messagePrivateMapper.insert(messagePrivate);
                MessagePrivate messagePrivateOne = messagePrivateMapper.selectById(messagePrivate.getId());
                receiverChannel.writeAndFlush(new TextWebSocketFrame(messagePrivateOne.toString()));
            // 好友不在线
            }else{
                messagePrivate.setFlag(false);
                // 存储消息
                insertFlag = messagePrivateMapper.insert(messagePrivate);
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
            ChannelMessage channelMessageOne = messageGroupMapper.GetChannelTextMessageOne(sendId, messageGroup.getId());
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

    // 暂且弃用
    private Boolean privateSendMultipartFileMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        // 存储消息类
        MessagePrivate messagePrivate = new MessagePrivate();
        Long sendId = (Long) userChannel.attr(AttributeKey.valueOf("userId")).get();
        Channel receiverChannel = NettyConfiguration.getOnlineUserMap().get(Long.parseLong(webSocketMessage.getReceiverId()));
        Boolean uploadFlag = new MinioTools().UploadUserMiscFile((MultipartFile) webSocketMessage.getContent());
        String fileUrl;
        if(uploadFlag){
            fileUrl = minioServerUrl + "/" + "miscFile" + "/" + ((MultipartFile) webSocketMessage.getContent()).getName() + "-" + DateUtil.thisMinute();
        }else{
            return Boolean.FALSE;
        }
        messagePrivate
                .setSenderId(sendId)
                .setReceiverId(Long.parseLong(webSocketMessage.getReceiverId()))
                .setType(1)
                .setContent(fileUrl)
                .setReplyId(webSocketMessage.getReplyId())
                .setCreateTime(webSocketMessage.getCreateTime());
        try {
            int insertFlag;
            // 好友在线
            if(receiverChannel != null){
                messagePrivate.setFlag(true);
                // 存储消息
                insertFlag = messagePrivateMapper.insert(messagePrivate);
                MessagePrivate messagePrivateOne = messagePrivateMapper.selectById(messagePrivate.getId());
                receiverChannel.writeAndFlush(new TextWebSocketFrame(messagePrivateOne.toString()));
            // 好友不在线
            }else{
                messagePrivate.setFlag(false);
                // 存储消息
                insertFlag = messagePrivateMapper.insert(messagePrivate);
            }
            return (insertFlag>0) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("私聊发送文本消息业务异常");
            return Boolean.FALSE;
        }
    }

    private Boolean groupSendMultipartFileMessage(Channel userChannel, WebSocketMessage webSocketMessage){
        // 存储消息类
        Long sendId = (Long) userChannel.attr(AttributeKey.valueOf("userId")).get();
        ChannelEmoji channelEmojiOne = channelEmojiMapper.selectOne(new LambdaQueryWrapper<ChannelEmoji>()
                .eq(ChannelEmoji::getId, webSocketMessage.getContent())
        );
        MessageGroup messageGroup = new MessageGroup();
        messageGroup
                .setSenderId(sendId)
                .setChannelId(Integer.parseInt(webSocketMessage.getReceiverId()))
                .setType(1)
                .setContent(channelEmojiOne.getLink())
                .setReplyId(webSocketMessage.getReplyId())
                .setCreateTime(webSocketMessage.getCreateTime());
        try{
            // 存储消息
            int insertFlag = messageGroupMapper.insert(messageGroup);
            // 群发消息类
            ChannelMessage channelMessageOne = messageGroupMapper.GetChannelTextMessageOne(sendId, messageGroup.getId());
            // 群发消息
            ChannelGroup groupChannel = NettyConfiguration.getOnlineChannelMap().get(Integer.parseInt(webSocketMessage.getReceiverId()));
            groupChannel.writeAndFlush(new TextWebSocketFrame(channelMessageOne.toString()));
            return (insertFlag>0)?Boolean.TRUE:Boolean.FALSE;
        }catch (Exception e) {
            e.printStackTrace();
            log.error("群发图片消息业务异常");
            return Boolean.FALSE;
        }
    }
}
