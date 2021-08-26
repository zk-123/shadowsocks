package com.zkdcloud.shadowsocks.client.socks5;

import com.zkdcloud.shadowsocks.client.socks5.menu.ClientHelper;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.Socks5ServerDoorHandler;
import com.zkdcloud.shadowsocks.client.socks5.config.ClientConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * client Start
 *
 * @author zk
 * @since 2018/8/20
 */
public class ClientStart {

    private static final Logger logger = LoggerFactory.getLogger(ClientStart.class);
    /**
     * boosLoopGroup
     */
    private static final EventLoopGroup bossLoopGroup = new NioEventLoopGroup(ClientConfig.clientConfig.getBossThreadNumber());
    /**
     * worksLoopGroup
     */
    private static final EventLoopGroup worksLoopGroup = new NioEventLoopGroup(ClientConfig.clientConfig.getBossThreadNumber());
    /**
     * clientBootstrap
     */
    private static final ServerBootstrap clientBootstrap = new ServerBootstrap();

    public static void main(String[] args) throws InterruptedException {
        ClientHelper.useHelp(args);
        startupClient();
    }

    private static void startupClient() throws InterruptedException {
        clientBootstrap.group(bossLoopGroup, worksLoopGroup)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast("idle", new IdleStateHandler(0, 0, ClientConfig.clientConfig.getIdleTime(), TimeUnit.SECONDS))
                                .addLast("crypt-init", new CryptInitInHandler())
                                .addLast("socks5-door", new Socks5ServerDoorHandler());
                    }
                });
        InetSocketAddress localAddress = getLocalAddress(ClientConfig.clientConfig.getLocal());
        ChannelFuture channelFuture = clientBootstrap.bind(localAddress).sync();

        //start log
        logger.info("shadowsocks socks5 client [TCP] running at {}", localAddress);
        channelFuture.channel().closeFuture().sync();
    }

    private static InetSocketAddress getLocalAddress(String address) {
        if (!address.contains(":")) {
            throw new IllegalArgumentException("illegal server address: " + address + ", address format: ip:port");
        }
        String host = address.substring(0, address.indexOf(":"));
        int port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
        return new InetSocketAddress(host, port);
    }
}
