package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import com.zkdcloud.shadowsocks.server.context.ServerContextConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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

    private List<ByteBuf> clientBuffs = new ArrayList<>();

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

            InetSocketAddress remoteAddress = clientCtx.channel().attr(ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS).get();

            bootstrap.group(clientCtx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("timeout", new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS) {
                                        @Override
                                        protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
                                            logger.debug("{} state:{}", remoteAddress.toString(), state.toString());
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
                                            if(logger.isDebugEnabled()){
                                                logger.debug("{}:{} channelId:{} is inactive",remoteAddress.getHostName(),remoteAddress.getPort(),remoteChannel.id());
                                            }
                                            closeChannel();
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage());
                                            closeChannel();
                                        }
                                    });
                        }
                    });

            bootstrap.connect(remoteAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    remoteChannel = future.channel();

                    if (logger.isDebugEnabled()) {
                        logger.debug("host: {}:{} remoteChannel {}, writeByteBuf {}", remoteAddress.getHostName(), remoteAddress.getPort(), remoteChannel.id(), msg.readableBytes());
                    }
                    clientBuffs.add(msg.retain());
                    writeAndFlushByteBufList();
                } else {
                    logger.error(remoteAddress.getHostName() + ":" + remoteAddress.getPort() + " connection fail");
                    ReferenceCountUtil.release(msg);
                    closeChannel();
                }
            });
        }

        clientBuffs.add(msg.retain());
        writeAndFlushByteBufList();
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

    /**
     * print ByteBufList to remote channel
     */
    private void writeAndFlushByteBufList() {
        if (remoteChannel != null && !clientBuffs.isEmpty()) {

            ByteBuf willWriteMsg = PooledByteBufAllocator.DEFAULT.heapBuffer();
            for (ByteBuf messageBuf : clientBuffs) {
                willWriteMsg.writeBytes(ShadowsocksUtils.readRealBytes(messageBuf));
                ReferenceCountUtil.release(messageBuf);
            }
            clientBuffs.clear();

            if(logger.isDebugEnabled()){
                logger.debug("channel id {},remote channel write {} bytes", remoteChannel.id().toString(), willWriteMsg.readableBytes());
            }
            remoteChannel.writeAndFlush(willWriteMsg);
        }
    }
}
