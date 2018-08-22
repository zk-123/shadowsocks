package com.zkdcloud.shadowsocks.client.channelHandler.inbound;

import com.zkdcloud.shadowsocks.client.context.ClientContextConstant;
import com.zkdcloud.shadowsocks.client.context.RepType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.zkdcloud.shadowsocks.client.context.ClientContextConstant.SOCKS5_VERSION;

public class Socks5ConnectOperatorInbound extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(Socks5ConnectOperatorInbound.class);

    /**
     * remote connection
     */
    private Channel remoteChannel;
    /**
     * clientChannel
     */
    private Channel clientChannel;
    /**
     * remote bootstrap
     */
    private Bootstrap remoteBootstrap;
    /**
     * one thread eventLoopGroup
     */
    public static NioEventLoopGroup singleEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("singleEventLoopGroup"));

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (remoteChannel == null) {
            buildConnect(ctx);

            if (remoteChannel != null) {
                ByteBuf repMessage = ctx.alloc().heapBuffer();
                repMessage.writeByte(SOCKS5_VERSION);//version
                repMessage.writeByte(RepType.OPERATOR_SUCCESS.getValue()); //ok
                repMessage.writeByte(0x00);//rsv
                repMessage.writeByte(0x01);
                repMessage.writeInt(323212334);
                repMessage.writeShort(3030);

                ctx.writeAndFlush(repMessage);
            } else {
                closeChannel();
            }
        }

        remoteChannel.writeAndFlush(msg);
    }

    /**
     * build remote connection
     *
     * @param ctx ctx
     */
    private void buildConnect(ChannelHandlerContext ctx) throws InterruptedException {
        remoteBootstrap = new Bootstrap();
        clientChannel = ctx.channel();

        //get queryAddress
        InetSocketAddress queryAddress = ctx.channel().attr(ClientContextConstant.QUERY_ADDRESS).get();
        if (queryAddress == null) {
            closeChannel();
            throw new IllegalArgumentException("no remote address");
        }

        remoteBootstrap.group(singleEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 3))
                                .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                        clientChannel.writeAndFlush(msg.retain());
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        closeChannel();
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        logger.error("channelId: {}, cause : {}", ctx.channel().id(), cause.getMessage());
                                        closeChannel();
                                    }
                                });
                    }
                });
        remoteBootstrap.connect(queryAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    remoteChannel = future.channel();
                } else {
                    logger.error("channelId: {}, cause : {}", future.channel().id(), future.cause().getMessage());
                    closeChannel();
                }
            }
        }).sync();
    }

    /**
     * close channel
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

        if (remoteBootstrap != null) {
            remoteBootstrap = null;
        }
    }
}
