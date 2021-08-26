package com.zkdcloud.shadowsocks.server;

import com.zkdcloud.shadowsocks.server.menu.ServerHelper;
import com.zkdcloud.shadowsocks.server.chananelHandler.ExceptionDuplexHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.DecodeSSHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.TcpProxyInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.outbound.EncodeSSOutHandler;
import com.zkdcloud.shadowsocks.server.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * server start
 *
 * @author zk
 * @since 2018/8/11
 */
public class ServerStart {

    private static final Logger logger = LoggerFactory.getLogger(ServerStart.class);

    /**
     * boosLoopGroup
     */
    private static final EventLoopGroup bossLoopGroup = new NioEventLoopGroup(ServerConfig.serverConfig.getBossThreadNumber());
    /**
     * worksLoopGroup
     */
    private static final EventLoopGroup worksLoopGroup = new NioEventLoopGroup(ServerConfig.serverConfig.getWorkersThreadNumber());
    /**
     * serverBootstrap
     */
    private static final ServerBootstrap serverBootstrap = new ServerBootstrap();

    public static void main(String[] args) throws InterruptedException {
        ServerHelper.useHelp(args);
        startupServer();
    }

    private static void startupServer() throws InterruptedException {
        serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, ServerConfig.serverConfig.getClientIdle(), TimeUnit.SECONDS))
                                .addLast(new CryptInitInHandler())
                                .addLast(new DecodeSSHandler())
                                .addLast(new TcpProxyInHandler())
                                .addLast(new EncodeSSOutHandler())
                                .addLast(new ExceptionDuplexHandler());
                    }
                });
        InetSocketAddress bindAddress = getAddress(ServerConfig.serverConfig.getLocalAddress());
        ChannelFuture channelFuture = serverBootstrap.bind(bindAddress).sync();
        logger.info("shadowsocks server [tcp] running at {}", bindAddress);
        channelFuture.channel().closeFuture().sync();
    }


    private static InetSocketAddress getAddress(String address) {
        if (!address.contains(":")) {
            throw new IllegalArgumentException("illegal address: " + address);
        }
        String host = address.substring(0, address.indexOf(":"));
        int port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
        return new InetSocketAddress(host, port);
    }
}
