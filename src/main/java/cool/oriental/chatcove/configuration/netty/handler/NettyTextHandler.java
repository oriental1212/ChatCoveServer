package cool.oriental.chatcove.configuration.netty.handler;

import cool.oriental.chatcove.configuration.netty.NettyConfiguration;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @Author: Oriental
 * @Date: 2023-07-02-10:45
 * @Description: netty的文本拦截器
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyTextHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public static final String USER_ID = "userId";
    public NettyTextHandler() {
        log.info("初始化NettyTextHandler中...");
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
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //接收到的文本信息的二进制形式
        ByteBuf content = msg.content();

        //接收到的文本信息
        String text = msg.text();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("你好"));
        /*根据类型进行消息的出路*/
        //获取到该条消息的标识，前端的字段必须后端的枚举名大小写一致
        //根据消息类型进行不同业务处理
        // String type = JSON.parseObject(text).get("type").toString();
        // MessageTypeEnum messageTypeEnum = MessageTypeEnum.valueOf(type);
        //
        // //普通文本消息
        // if (MessageTypeEnum.TEXT.compareTo(messageTypeEnum) == 0) {
        //     //发送消息
        //     sendMsg(text);
        // } else if (MessageTypeEnum.HEARTBEAT.compareTo(messageTypeEnum) == 0) {
        //     HeartbeatMessage heartbeatMessage = JSON.parseObject(text, HeartbeatMessage.class);
        //     String userId = heartbeatMessage.getUserId();
        //     //接收到客户端的心跳
        //     log.debug("来自【{}】的心跳", userId);
        // } else if (MessageTypeEnum.REGISTER.compareTo(messageTypeEnum) == 0) {
        //     //注册
        //     register(ctx, text);
        // }

    }

    /*
     * 将连接的客户端注册到服务端中
     *
     * @param ctx
     * @param text
     */
    // private void register(ChannelHandlerContext ctx, String text) {
    //     RegisterMessage registerMessage = JSON.parseObject(text, RegisterMessage.class);
    //     String userId = registerMessage.getUserId();
    //     //注册客户端
    //     //给 Channel 绑定一个存储 UserId 的 AttributeKey
    //     Channel channel = ctx.channel();
    //     //设置一个名为 userId 的 AttributeKey
    //     AttributeKey<Object> userIdKey = AttributeKey.valueOf("userId");
    //     //将 Channel 的 attr 设置一个名为 userId
    //     channel
    //             //在 Channel 中寻找名为 userIdKey 的 AttributeKey
    //             .attr(userIdKey)
    //             //给这个 AttributeKey 设置值
    //             .set(userId);
    //     //当自定义属性在属性集合中不存在时才进行添加
    //     //.setIfAbsent(userId);
    //
    //     //将UserId与Channel建立联系
    //     NettyConfiguration.getOnlineUserChannelMap().put(userId, channel);
    //     log.debug("在线用户 --> {}", NettyConfiguration.getOnlineUserChannelMap().keySet());
    //
    //     //通知所有用户都上线了
    //     NettyConfiguration.getOnlineChannelGroup().writeAndFlush(new TextWebSocketFrame(
    //             "用户：【" + userId + "】上线啦！"
    //     ));
    // }

    /*
     * 给指定的用户发送消息
     *
     * @param textMessageJson
     */
    // private void sendMsg(String textMessageJson) {
    //     //获取接收到的消息的实体类
    //     TextMessage textMessage = JSON.parseObject(textMessageJson, TextMessage.class);
    //     String userId = textMessage.getUserId();
    //     String userMsg = textMessage.getMsg();
    //     String receiver = textMessage.getReceiver();
    //     //给指定的用户发送消息
    //     Channel receiverChannel = NettyConfiguration.getOnlineUserChannelMap().get(receiver);
    //     if (Objects.nonNull(receiverChannel)) {
    //         //TODO 这里可以设计为结构化的数据，以返回JSON数据便于解析
    //         receiverChannel.writeAndFlush(new TextWebSocketFrame(userId + "：" + userMsg));
    //     }
    //     log.debug("用户【{}】给【{}】发送的消息：{}", userId, receiver, userMsg);
    //     //TODO 服务端给客户端回复消息（可以设计为失败时返回）
    //     //channel.writeAndFlush(new TextWebSocketFrame("服务端已接收到消息"));
    // }

    /**
     * 删除断开连接的客户端在程序中的数据
     *
     * @param channel 断开连接的客户端的 Channel
     */
    private void cleanChannel(Channel channel) {
        //获取客户端 Channel 中存储的名为 userId 的 Attribute
        Attribute<String> userIdKey = channel.attr(AttributeKey.valueOf(String.valueOf(USER_ID)));
        Long userId = Long.parseLong(userIdKey.get());
        //从 ChannelGroup 中移除断开的 Channel
        NettyConfiguration.getOnlineChannelGroup().remove(channel);
        //从 Map 中移除 UserId 与 Channel 的对照关系
        NettyConfiguration.getOnlineUserChannelMap().remove(userId);

        //通知所有用户某用户下线了
        NettyConfiguration.getOnlineChannelGroup().writeAndFlush(new TextWebSocketFrame(
                "用户：【" + userId + "】下线啦！"
        ));
    }

    /**
     * 检查给定的字符串是否不是空串、空格、null
     *
     * @param strs 需要检查的字符串
     */
    private boolean checkHasText(String... strs) {
        return Arrays.stream(strs).sequential().allMatch(StringUtils::hasText);
    }
}
