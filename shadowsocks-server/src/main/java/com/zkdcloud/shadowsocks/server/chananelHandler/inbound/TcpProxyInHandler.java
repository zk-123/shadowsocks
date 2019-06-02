package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
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
 * proxy handler
 *
 * @author zk
 * @since 2018/8/14
 */
public class TcpProxyInHandler extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(TcpProxyInHandler.class);
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
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (clientChannel == null) {
            clientChannel = ctx.channel();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
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
                                    .addLast("timeout", new IdleStateHandler(0, 0, 30, TimeUnit.HOURS) {
                                        @Override
                                        protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
                                            return super.newIdleStateEvent(state, first);
                                        }
                                    })
                                    .addLast("transfer", new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                            clientCtx.channel().writeAndFlush(msg.retain());
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            if (clientChannel != null && clientChannel.isOpen()) {
                                                reconnectRemote();
                                            } else {
                                                if (logger.isDebugEnabled()) {
                                                    logger.debug("remote [{}] [{}:{}]  is inactive", remoteChannel.id(), remoteAddress.getHostName(), remoteAddress.getPort());
                                                }
                                                remoteChannel = null;
                                            }
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            logger.error("remote channel [{}], cause:{}", ctx.channel().id(), cause.getMessage());
                                            closeRemoteChannel();
                                            closeClientChannel();
                                        }

                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                            if (evt instanceof IdleStateEvent) {
                                                if (logger.isDebugEnabled()) {
                                                    logger.debug("[{}] [{}] state:{} happened", remoteChannel.id(), remoteAddress.toString(), evt.toString());
                                                }
                                                closeClientChannel();
                                                closeRemoteChannel();
                                                return;
                                            }
                                            super.userEventTriggered(ctx, evt);
                                        }
                                    });
                        }
                    });

            bootstrap.connect(remoteAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    remoteChannel = future.channel();

                    if (logger.isDebugEnabled()) {
                        try {
                            logger.debug("host: [{}:{}] connect success, client channelId is [{}],  remote channelId is [{}]", remoteAddress.getHostName(), remoteAddress.getPort(), clientChannel.id(), remoteChannel.id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    clientBuffs.add(msg.retain());
                    writeAndFlushByteBufList();
                } else {
                    logger.error(remoteAddress.getHostName() + ":" + remoteAddress.getPort() + " connection fail");
                    ReferenceCountUtil.release(msg);
                    closeClientChannel();
                }
            });
        }

        clientBuffs.add(msg.retain());
        writeAndFlushByteBufList();
    }

    private void closeClientChannel() {
        if (clientChannel != null) {
            clientChannel.close();
        }
    }

    private void closeRemoteChannel() {
        if (remoteChannel != null) {
            remoteChannel.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("client [{}] is inactive", ctx.channel().id());
        }

        clientChannel = null;
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

            if (logger.isDebugEnabled()) {
                logger.debug("write to remote channel [{}] {} bytes", remoteChannel.id().toString(), willWriteMsg.readableBytes());
            }
            remoteChannel.writeAndFlush(willWriteMsg);
        }
    }

    /**
     * reconnect remote
     */
    private void reconnectRemote() {
        InetSocketAddress remoteAddress = clientChannel.attr(ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS).get();
        bootstrap.connect(remoteAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                remoteChannel = future.channel();
                if (logger.isDebugEnabled()) {
                    logger.debug("host: {}:{} reconnect success, remote channelId is {}", remoteAddress.getHostName(), remoteAddress.getPort(), remoteChannel.id());
                }
                writeAndFlushByteBufList();
            } else {
                logger.error(remoteAddress.getHostName() + ":" + remoteAddress.getPort() + " reconnection fail");
                closeClientChannel();
                closeRemoteChannel();
                //todo release bytes
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage());
        ctx.channel().close();
    }
}
