package cool.oriental.chatcove.configuration.netty.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import cool.oriental.chatcove.configuration.netty.EnumMessageType;
import cool.oriental.chatcove.configuration.netty.NettyConfiguration;
import cool.oriental.chatcove.configuration.netty.WebSocketMessage;
import cool.oriental.chatcove.service.MessageService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: Oriental
 * @Date: 2023-07-02-10:45
 * @Description: netty的文本拦截器
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyTextHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Resource
    MessageService messageService;
    public NettyTextHandler() {
        log.info("NettyTextHandler正常启动");
    }

    /**
     * 在新的 Channel 被添加到 ChannelPipeline 中时被调用。这通常发生在连接建立时，即 Channel 已经被成功绑定并注册到 EventLoop 中。
     * 在连接建立时被调用一次
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        //将新连接的客户端Channel存储起来
        NettyConfiguration.getOnlineChannelGroup().add(channel);
        log.info("新客户端建立链接 --> {}，在线用户数量：{}", channel.id(), NettyConfiguration.getOnlineChannelGroup().size());
    }


    /**
     * 在 WebSocket 连接断开时，Netty 会自动触发 channelInactive 事件，并将该事件交给事件处理器进行处理。
     * 在 channelInactive 事件的处理过程中，会调用 handlerRemoved 方法，用于进行一些资源释放等操作，确保 WebSocket 连接正常断开。
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        //移除断开的客户端的Channel
        Channel channel = ctx.channel();
        cleanChannel(channel);
        log.debug("客户端断开链接 --> {}，在线用户数量：{}", channel.id(), NettyConfiguration.getOnlineChannelGroup().size());
    }

    /**
     * 处理客户端非正常断开（WebSocket 连接发生异常时调用）
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        //获取断开连接的客户端的Channel
        Channel channel = ctx.channel();
        //移除断开的客户端的Channel
        cleanChannel(channel);
        log.debug("客户端异常断开 --> {}，在线用户数量：{}", channel.id(), NettyConfiguration.getOnlineChannelGroup().size());
        //当发生异常时，手动关闭Channel
        channel.close();
    }

    /**
     * 当 Channel 的连接建立并准备好接收数据时被调用。这意味着连接已经成功建立，可以开始发送和接收数据了。
     * 在每次连接激活时被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
    }

    /**
     * 当接收到前端发送的WebSocket时处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        //接收到的文本信息的二进制形式
        ByteBuf content = msg.content();
        //接收到的文本信息
        String text = msg.text();
        String type = JSON.parseObject(text).get("type").toString();
        // 根据类型进行消息的出路
        try {
            switch (EnumMessageType.valueOf(type)){
                case PRIVATE_CHAT,GROUP_CHAT -> sendMsg(text , type, ctx.channel());
                case REGISTER -> register(ctx, text);
                case AUDIO -> sendAudio(text, ctx.channel());
                case HEART -> {
                }
            }
        } catch (IllegalArgumentException e) {
            log.debug("netty的Type校验异常");
            ctx.channel().writeAndFlush(new TextWebSocketFrame("type错误"));
        }
    }

    /*
     * 将连接的客户端注册到服务端中
     *
     * @param ctx
     * @param text
     */
    private void register(ChannelHandlerContext ctx, String text) {
        String tokenValue = JSON.parseObject(text).get("ChatCoveToken").toString();
        Long userId = (Long) StpUtil.getLoginIdByToken(tokenValue);
        if(userId == null){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("用户未登录"));
            ctx.channel().close();
        }
        //注册客户端
        //给 Channel 绑定一个存储 UserId 的 AttributeKey
        Channel channel = ctx.channel();
        AttributeKey<Object> userIdKey = AttributeKey.valueOf("userId");
        channel.attr(userIdKey).set(userId);

        // 通知好友上线,并且将用户加入用户组
        if(messageService.friendNotifyOnline(userId) == Boolean.FALSE){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("WebSocket连接失败，请稍后重试"));
            ctx.channel().close();
        }else{
            NettyConfiguration.getOnlineUserMap().put(userId, channel);
        }
    }

    /*
     * 给指定的用户发送消息
     *
     * @param message
     * @param type
     * @param channel
     */
    private void sendMsg(String message, String type, Channel channel) {
        //获取接收到的消息的实体类
        WebSocketMessage messageClass = JSON.parseObject(message, WebSocketMessage.class);
        switch (type){
            case "GROUP_CHAT" -> {
                Boolean groupSendFlag = messageService.groupSendMessage(channel, messageClass);
                if(!groupSendFlag){channel.writeAndFlush(new TextWebSocketFrame("发送失败"));}
            }
            case "PRIVATE_CHAT" -> {
                Boolean privateSendFlag = messageService.privateSendMessage(channel, messageClass);
                if(!privateSendFlag){channel.writeAndFlush(new TextWebSocketFrame("发送失败"));}
            }
        }
    }

    /*
     * 给群组用户发送语音消息
     *
     * @param textMessageJson
     */
    private void sendAudio(Object text, Channel channel){
        Object channelId = JSON.parseObject((String) text).get("channelId").toString();
        ChannelGroup channelList = NettyConfiguration.getOnlineChannelMap().get(channelId);
        if(channelList == null){
            return;
        }
        if(channelList.contains(channel)){
            channelList.writeAndFlush(JSON.parseObject((String) text).get("content").toString());
        }
    }

    /**
     * 删除断开连接的客户端在程序中的数据
     *
     * @param channel 断开连接的客户端的 Channel
     */
    private void cleanChannel(Channel channel) {
        //获取客户端 Channel 中存储的名为 userId 的 Attribute
        Long userId = (Long) channel.attr(AttributeKey.valueOf("userId")).get();
        //通知所有用户某用户下线了
        if(messageService.friendNotifyOffline(userId) == Boolean.FALSE){
            channel.writeAndFlush(new TextWebSocketFrame("WebSocket关闭连接失败，已强制关闭"));
            channel.close();
        }else {
            //从 ChannelGroup 中移除断开的 Channel
            NettyConfiguration.getOnlineChannelGroup().remove(channel);
            //从 Map 中移除 UserId 与 Channel 的对照关系
            NettyConfiguration.getOnlineUserMap().remove(userId);
        }
    }

}
