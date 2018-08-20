package com.zkdcloud.shadowsocks.client.channelHandler.inbound;

import com.zkdcloud.shadowsocks.client.context.ClientContextConstant;
import com.zkdcloud.shadowsocks.client.context.CmdType;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * socks5 请求解析
 *
 * @author zk
 * @since 2018/8/20
 */
public class Socks5AnalysisInbound extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(Socks5AnalysisInbound.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (ClientContextConstant.SOCKS5_VERSION != msg.readByte()) {
            logger.error("it's not sockets5 connection");
            ctx.channel().close();
        }

        // get cmd type
        byte aType = msg.readByte();
        switch (CmdType.valueOf(String.valueOf(aType))) {
            case CONNECT:
                addConnect(ctx, msg);
                break;
            case BIND:
                throw new UnsupportedOperationException("un support bind cmd");
            case UDP:
                throw new UnsupportedOperationException("un support udp cmd");
            default:
                throw new UnsupportedOperationException("un support others cmd");
        }
        ctx.pipeline().remove(this);
    }

    /**
     * add Connect channelHandler
     *
     * @param ctx ctx
     * @param msg msg
     */
    private void addConnect(ChannelHandlerContext ctx, ByteBuf msg) {
        msg.skipBytes(1);

        //get query remote address
        InetSocketAddress queryAddress = ShadowsocksUtils.getIp(msg);
        ctx.channel().attr(ClientContextConstant.QUERY_ADDRESS).setIfAbsent(queryAddress);
        if (logger.isDebugEnabled()) {
            logger.debug("channelId:{} queryHost: {} queryPort: {}", ctx.channel().id(), queryAddress.getHostName(), queryAddress.getPort());
        }
    }
}
