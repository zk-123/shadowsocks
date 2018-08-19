package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.server.context.ContextConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author zk
 * @since 2018/8/14
 */
public class ProxyInHandler extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(ProxyInHandler.class);

    /**
     * bootstrap
     */
    private Bootstrap bootstrap;
    /**
     * 客户端channel
     */
    private Channel clientChannel;
    /**
     * channelFuture
     */
    private Channel remoteChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (clientChannel == null) {
            clientChannel = ctx.channel();
        }
        proxyMsg(ctx, msg);
    }

    private void proxyMsg(ChannelHandlerContext clientCtx, ByteBuf msg) throws InterruptedException {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();

            InetSocketAddress clientRecipient = clientCtx.channel().attr(ContextConstant.REMOTE_INET_SOCKET_ADDRESS).get();


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
                                    .addLast("timeout", new IdleStateHandler(0, 0, 15, TimeUnit.MINUTES) {
                                        @Override
                                        protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
                                            logger.debug("{} state:{}", clientRecipient.toString(), state.toString());
                                            closeChannel();
                                            return super.newIdleStateEvent(state, first);
                                        }
                                    })
                                    .addLast("query", new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                            clientCtx.channel().writeAndFlush(msg.retain());
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            closeChannel();
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage());
                                            cause.printStackTrace();
                                            closeChannel();
                                        }
                                    });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(clientRecipient).sync();
            remoteChannel = channelFuture.channel();
        }
        remoteChannel.writeAndFlush(msg.retain());
    }

    /**
     * close future
     */
    private void closeChannel() {
        if (remoteChannel != null) {
            remoteChannel.close();
            remoteChannel = null;
        }

        if (clientChannel != null) {
            clientChannel.close();
            clientChannel = null;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannel();
    }
}
