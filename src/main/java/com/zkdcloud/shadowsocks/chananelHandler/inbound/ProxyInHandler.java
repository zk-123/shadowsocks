package com.zkdcloud.shadowsocks.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.context.ContextConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * description
 *
 * @author zk
 * @since 2018/8/14
 */
public class ProxyInHandler extends MessageToMessageDecoder<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(ProxyInHandler.class);

    /**
     * bootstrap
     */
    private Bootstrap bootstrap;
    /**
     * channelFuture
     */
    private ChannelFuture remoteChannelFuture;

    List<ByteBuf> byteBufList = new ArrayList<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        InetSocketAddress remoteAddress = ctx.channel().attr(ContextConstant.REMOTE_INET_SOCKET_ADDRESS).get();
        proxyMsg(remoteAddress, msg, ctx.channel());
    }

    private void proxyMsg(InetSocketAddress remoteAddress, ByteBuf msg, final Channel clientChannel) throws InterruptedException {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60 * 1000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)// 读缓冲区为32k
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(0,0,3))
                                    .addLast("query", new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                            clientChannel.writeAndFlush(msg);
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            remoteChannelFuture.channel().close();
                                            remoteChannelFuture = null;
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage());
                                        }
                                    });
                        }
                    });
        }

        remoteChannelFuture = bootstrap.connect(remoteAddress).addListener((ChannelFutureListener)(future)->{
            if(future.isSuccess()){
                for (ByteBuf byteBuf : byteBufList) {
                    future.channel().writeAndFlush(byteBuf);
                }
                byteBufList.clear();
            }
        });

        if(remoteChannelFuture == null){

        }
        remoteChannelFuture.channel().writeAndFlush(msg.retain());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remoteChannelFuture.channel().close();
        remoteChannelFuture = null;
    }
}
