package com.zkdcloud.shadowsocks.client.socks5.income;

import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.Socks5AnalysisInbound;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.Socks5AuthenticateInbound;
import com.zkdcloud.shadowsocks.common.income.AbstractIncome;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author zk
 * @since 2018/8/20
 */
public class TcpClientIncome extends AbstractIncome {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(TcpClientIncome.class);

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

    @Override
    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("idle",new IdleStateHandler(0, 0, 3, TimeUnit.MINUTES) {
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                logger.error("channelId: {} has exception, cause : {}", ctx.channel().id(), cause.getMessage());
                                ctx.channel().close();
                            }
                        })
                                .addLast("authenticate",new Socks5AuthenticateInbound())
                                .addLast("accept",new Socks5AnalysisInbound());
                    }
                }).bind(1081).sync();
        logger.info("shadowsocks tcp client start at {}",port);
        channelFuture.channel().closeFuture().sync();
    }
}
