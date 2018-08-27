package com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound;

import com.zkdcloud.shadowsocks.client.socks5.context.ClientContextConstant;
import com.zkdcloud.shadowsocks.client.socks5.context.Socks5Method;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description
 *
 * @author zk
 * @since 2018/8/20
 */
public class Socks5AuthenticateInbound extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(Socks5AuthenticateInbound.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //check version and is/not support
        if (ClientContextConstant.SOCKS5_VERSION != msg.readByte() || !isSupport(msg)) {
            logger.error("it's not sockets5 connection");
            ctx.channel().close();
            return;
        }

        // return '0x005'|'0x00'
        ByteBuf result = ctx.alloc().buffer().writeByte(ClientContextConstant.SOCKS5_VERSION).writeByte(Socks5Method.NO_APPROVE.getValue());
        ctx.channel().writeAndFlush(result);
        ctx.pipeline().remove(this);
        if(logger.isDebugEnabled()){
            logger.info("channel :{} sock5已握手",ctx.channel().id());
        }
    }

    /**
     * is or not support socks5 method
     *
     * @param msg socks5 msg
     * @return yes is true/ no is false
     */
    private boolean isSupport(ByteBuf msg){
        boolean isSupport = false;

        byte nmethods = msg.readByte();
        for (int i = 0; i < nmethods; i++) {
            if(Socks5Method.NO_APPROVE.getValue() == msg.readByte()){
                isSupport = true;
            }
        }
        return isSupport;
    }
}
