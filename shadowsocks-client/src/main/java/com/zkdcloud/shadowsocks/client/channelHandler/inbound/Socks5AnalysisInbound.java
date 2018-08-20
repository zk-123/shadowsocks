package com.zkdcloud.shadowsocks.client.channelHandler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * socks5 解析
 *
 * @author zk
 * @since 2018/8/20
 */
public class Socks5AnalysisInbound extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

    }
}
