package cool.oriental.chatcove.service;

import cool.oriental.chatcove.configuration.netty.WebSocketMessage;
import io.netty.channel.Channel;

/**
 * @Author: Oriental
 * @Date: 2023-07-21-11:37
 * @Description: 消息服务接口
 */
public interface MessageService {
    Boolean friendNotifyOnline(Long userId);
    Boolean friendNotifyOffline(Long userId);
    Boolean groupSendMessage(Channel channel, WebSocketMessage webSocketMessage);
    Boolean privateSendMessage(Channel channel, WebSocketMessage webSocketMessage);
}
