package cool.oriental.chatcove.configuration.netty;

import cool.oriental.chatcove.configuration.netty.handler.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

/**
 * @Author: Oriental
 * @Date: 2023-07-02-10:27
 * @Description: Netty的服务器
 */

@Component
@Slf4j
public class WebSocketServer {
    private static final int nettPort = 19999;
    private static final String nettyPath = "/webSocket";
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private void start() throws InterruptedException{
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        Channel channel = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerInitializer(nettyPath))
                .bind(nettPort)
                .sync()
                .channel();

        log.info("服务端启动成功，端口号：{}", nettPort);
        channel
                .closeFuture()
                .sync();
    }

    /**
     * 释放资源
     * PreDestroy注解：在容器销毁该组件之前被调用
     * 注解使用前提：该类的实例必须是由容器创建和管理的，如 Spring、JavaEE 容器等。
     */
    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(bossGroup)) {
            bossGroup.shutdownGracefully();
        }

        if (Objects.nonNull(workerGroup)) {
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 初始化WebSocketServer（调用init()）
     * PostConstruct注解：用于指示一个方法在容器创建该组件之后立即调用
     * 注解使用前提：该类的实例必须是由容器创建和管理的，如 Spring、JavaEE 容器等。
     */
    @PostConstruct
    public void init() {
        //这里要新开一个线程，否则会阻塞原本的controller等业务
        new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
