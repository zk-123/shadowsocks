package com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound;

import com.zkdcloud.shadowsocks.client.socks5.channelHandler.outbound.Socks5DecryptOutbound;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.outbound.Socks5ServerEncoder;
import com.zkdcloud.shadowsocks.client.socks5.context.ClientContextConstant;
import com.zkdcloud.shadowsocks.common.config.ClientConfig;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksConfigUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * socks5建立连接
 *
 * @author zk
 * @since 2018/10/9
 */
public class Socks5ServerDoorHandler extends ChannelInboundHandlerAdapter {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(Socks5ServerDoorHandler.class);

    enum ACCSTATE {UN_INIT, UN_CONNECT, FINISHED}

    private ACCSTATE state;

    private Bootstrap remoteBootstrap;

    private Channel remoteChannel;

    private Channel clientChannel;

    public Socks5ServerDoorHandler() {
        this.state = ACCSTATE.UN_INIT;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.clientChannel = ctx.channel();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ctx.pipeline()
                .addFirst("socks5-init", new Socks5InitialRequestDecoder())//init socks5 request
                .addLast(new Socks5ServerEncoder());//socks5 response
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        switch (state) {
            case UN_INIT:
                Socks5InitialResponse response;
                if (msg instanceof Socks5InitialRequest) {
                    response = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
                    ctx.channel().writeAndFlush(response);

                    ctx.pipeline().addBefore(ctx.name(), "socks5-command", new Socks5CommandRequestDecoder());
                    this.state = ACCSTATE.UN_CONNECT;

                    if (logger.isDebugEnabled()) {
                        logger.debug("{} init success", ctx.channel().id());
                    }
                } else {
                    response = new DefaultSocks5InitialResponse(Socks5AuthMethod.UNACCEPTED);
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    logger.error("{} init is not socks5InitRequest", ctx.channel().id());
                }
                ReferenceCountUtil.release(msg);
                break;
            case UN_CONNECT:
                if (msg instanceof DefaultSocks5CommandRequest) {
                    DefaultSocks5CommandRequest commandRequest = (DefaultSocks5CommandRequest) msg;
                    InetSocketAddress dstAddress = new InetSocketAddress(commandRequest.dstAddr(), commandRequest.dstPort());

                    ctx.channel().attr(ClientContextConstant.DST_ADDRESS).setIfAbsent(dstAddress);
                    buildRemoteConnect();
                    this.state = ACCSTATE.FINISHED;
                } else {
                    ctx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4))
                            .addListener(ChannelFutureListener.CLOSE);
                    logger.error("{} is not a commanderRequest", ctx.channel().id());
                }

                ReferenceCountUtil.release(msg);
                break;
            case FINISHED:
                ctx.fireChannelRead(msg);
                break;
        }
    }

    private void buildRemoteConnect() {
        if (remoteBootstrap == null) {
            remoteBootstrap = new Bootstrap();
            remoteBootstrap.group(new NioEventLoopGroup(1))
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            Long timeOutSeconds = ShadowsocksConfigUtil.getClientConfigInstance().getTimeout();
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(0, 0, timeOutSeconds, TimeUnit.SECONDS))
                                    .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                            if(clientChannel.pipeline().get(Socks5DecryptOutbound.class) == null){
                                                clientChannel.pipeline().addLast("decryptRemote",new Socks5DecryptOutbound());
                                            }
                                            clientChannel.writeAndFlush(msg.retain());
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            logger.debug("remoteId {} is inactive", ctx.channel().id());
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

            //init remote attr and getProxy
            InetSocketAddress proxyAddress = getProxyAddress();
            remoteBootstrap.connect(proxyAddress).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    this.remoteChannel = future.channel();
                    this.clientChannel.attr(ClientContextConstant.REMOTE_CHANNEL).setIfAbsent(this.remoteChannel);
                    //send the connection is build
                    clientChannel.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4));
                    clientChannel.pipeline().addLast("socks5-transfer", new TransferFlowHandler());

                    if (logger.isDebugEnabled()) {
                        logger.debug("-------------------> remote channel {} is connected", remoteChannel.id());
                    }
                } else {
                    logger.error("channelId: {}, cause : {}", future.channel().id(), future.cause().getMessage());
                    closeChannel();
                }
            });
        }
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

    /**
     * get proxyAddress from 'config.json'
     *
     * @return inetSocketAddress
     */
    private InetSocketAddress getProxyAddress() {
        ClientConfig clientConfig = ShadowsocksConfigUtil.getClientConfigInstance();
        return new InetSocketAddress(clientConfig.getServer(), clientConfig.getServer_port());
    }
}
