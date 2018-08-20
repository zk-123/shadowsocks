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
    private EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("boss"));
    /**
     * worksLoopGroup
     */
    private EventLoopGroup worksLoopGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("works"));
    /**
     * serverBootstrap
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = this.serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .option(ChannelOption.SO_BACKLOG, 5120)
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)// 读缓冲区为32k
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 1) //关闭时等待1s发送关闭
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0,0,3))
                                .addLast(new CryptoInitInHandler())
                                .addLast(new DecodeCipherStreamInHandler())
                                .addLast(new PackageWaitingInHandler())
                                .addLast(new ProxyInHandler())
                                .addLast(new EncodeCipherStreamOutHandler());
                    }
                }).bind(8989).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
