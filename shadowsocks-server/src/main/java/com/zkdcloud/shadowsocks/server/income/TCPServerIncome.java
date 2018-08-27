package com.zkdcloud.shadowsocks.server.income;

import com.zkdcloud.shadowsocks.common.income.AbstractIncome;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.*;
import com.zkdcloud.shadowsocks.server.chananelHandler.outbound.EncodeCipherStreamOutHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class TCPServerIncome extends AbstractIncome {
    /**
     * boosLoopGroup
     */
    private EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
    /**
     * worksLoopGroup
     */
    private EventLoopGroup worksLoopGroup = new NioEventLoopGroup();
    /**
     * serverBootstrap
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = this.serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0,0,10,TimeUnit.SECONDS))
                                .addLast(new CryptoInitInHandler())
                                .addLast(new DecodeCipherStreamInHandler())
                                .addLast(new ProxyInHandler())
                                .addLast(new EncodeCipherStreamOutHandler());
                    }
                }).bind(8989).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
