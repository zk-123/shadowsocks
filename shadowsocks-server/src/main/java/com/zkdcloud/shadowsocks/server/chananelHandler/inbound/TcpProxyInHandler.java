package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import com.zkdcloud.shadowsocks.server.chananelHandler.ExceptionDuplexHandler;
import com.zkdcloud.shadowsocks.server.config.ServerConfig;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zkdcloud.shadowsocks.server.config.ServerContextConstant.CLIENT_CHANNEL;
import static com.zkdcloud.shadowsocks.server.config.ServerContextConstant.REMOTE_CHANNEL;

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
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (clientChannel == null) {
            clientChannel = ctx.channel();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        proxyMsg(ctx, msg);
    }

    private void proxyMsg(ChannelHandlerContext clientCtx, ByteBuf msg) {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();

            InetSocketAddress remoteAddress = clientCtx.channel().attr(ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS).get();

            bootstrap.group(clientCtx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(0, 0, ServerConfig.serverConfig.getRemoteIdle(), TimeUnit.SECONDS))
                                    .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                            clientCtx.channel().writeAndFlush(msg.retain());
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) {
                                            if (clientChannel != null && clientChannel.isOpen()) {
                                                reconnectRemote();
                                            } else {
                                                if (logger.isDebugEnabled()) {
                                                    logger.debug("remote [{}] [{}:{}]  is inactive", remoteChannel.id(), remoteAddress.getHostName(), remoteAddress.getPort());
                                                }
                                                remoteChannel = null;
                                            }
                                        }
                                    }).addLast(new ExceptionDuplexHandler());
                        }
                    });

            bootstrap.connect(remoteAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    remoteChannel = future.channel();
                    clientChannel.attr(REMOTE_CHANNEL).setIfAbsent(future.channel());
                    remoteChannel.attr(CLIENT_CHANNEL).setIfAbsent(clientChannel);

                    logger.info("host: [{}:{}] connect success, channelId [{}<->{}]", remoteAddress.getHostName(), remoteAddress.getPort(), clientChannel.id(), remoteChannel.id());
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
        dropBufList();
    }

    private void closeRemoteChannel() {
        if (remoteChannel != null) {
            remoteChannel.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
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
     * releaseBufList
     */
    private void dropBufList(){
        if(!clientBuffs.isEmpty()){
            for (ByteBuf clientBuff : clientBuffs) {
                if(clientBuff.refCnt() != 0){
                    clientBuff.retain(clientBuff.refCnt());
                    ReferenceCountUtil.release(clientBuff);
                }
            }
            clientBuffs.clear();
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
                clientChannel.attr(REMOTE_CHANNEL).setIfAbsent(future.channel());
                remoteChannel.attr(CLIENT_CHANNEL).setIfAbsent(clientChannel);

                if (logger.isDebugEnabled()) {
                    logger.debug("host: [{}:{}] reconnect success, remote channelId is [{}]", remoteAddress.getHostName(), remoteAddress.getPort(), remoteChannel.id());
                }
                writeAndFlushByteBufList();
            } else {
                logger.error(remoteAddress.getHostName() + ":" + remoteAddress.getPort() + " reconnection fail");
                closeClientChannel();
                closeRemoteChannel();
            }
        });
    }
}
