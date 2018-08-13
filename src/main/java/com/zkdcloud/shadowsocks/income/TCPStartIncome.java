package com.zkdcloud.shadowsocks.income;

import com.zkdcloud.shadowsocks.chananelHandler.inbound.CryptoInitInHandler;
import com.zkdcloud.shadowsocks.chananelHandler.inbound.DecodeCrypherInHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class TCPStartIncome extends AbstractIncome{
    /**
     * serverBootstrap
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    public void startup() throws InterruptedException {
        ChannelFuture channelFuture = this.serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new CryptoInitInHandler()).addLast(new DecodeCrypherInHandler());
                    }
                }).bind(8989).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
