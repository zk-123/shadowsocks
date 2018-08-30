package com.zkdcloud.shadowsocks.server.income;

import com.zkdcloud.shadowsocks.common.bean.ServerConfig;
import com.zkdcloud.shadowsocks.common.income.AbstractIncome;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksConfigUtil;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.CryptoInitInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.DecodeCipherStreamInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.TcpProxyInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.outbound.EncodeCipherStreamOutHandler;
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
 * shadowsocks tcp server start
 *
 * @author zk
 * @since 2018/8/11
 */
public class TCPServerIncome extends AbstractIncome {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(TCPServerIncome.class);

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
        serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                                .addLast(new CryptoInitInHandler())
                                .addLast(new DecodeCipherStreamInHandler())
                                .addLast(new TcpProxyInHandler())
                                .addLast(new EncodeCipherStreamOutHandler());
                    }
                });
        ServerConfig serverConfig = ShadowsocksConfigUtil.getServerConfigInstance();
        InetSocketAddress localAddress = new InetSocketAddress(serverConfig.getLocal_address(), serverConfig.getLocal_port());

        ChannelFuture channelFuture = serverBootstrap.bind(localAddress).sync();
        logger.info("shadowsocks tcp server running at {}:{}", localAddress.getHostName(), localAddress.getPort());
        channelFuture.channel().closeFuture().sync();
    }
}
