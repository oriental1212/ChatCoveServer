package cool.oriental.chatcove.configuration.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: Oriental
 * @Date: 2023-07-02-9:54
 * @Description: netty的配置类
 */
@Configuration
public class NettyConfiguration {
    // 存储在线的客户端
    private static final ChannelGroup onlineChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 存储所有在线的UserId与之对应的Channel
    private static final ConcurrentMap<Integer, Channel> onlineUserChannelMap = new ConcurrentHashMap<>();
    /**
     * 获取所有在线的客户端Channel
     */
    public static ChannelGroup getOnlineChannelGroup() {
        return onlineChannelGroup;
    }

    /**
     * 获取所有在线的UserId与之对应的Channel
     */
    public static ConcurrentMap<Integer, Channel> getOnlineUserChannelMap() {
        return onlineUserChannelMap;
    }
}