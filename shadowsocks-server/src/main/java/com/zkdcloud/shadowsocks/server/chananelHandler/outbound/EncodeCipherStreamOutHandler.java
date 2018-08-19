package com.zkdcloud.shadowsocks.server.chananelHandler.outbound;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.server.context.ContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * description
 *
 * @author zk
 * @since 2018/8/14
 */
public class EncodeCipherStreamOutHandler extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        AbstractCipher cipher = ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).get();

        byte[] realData = new byte[msg.readableBytes()];
        msg.getBytes(0,realData);

        byte[] resultData = cipher.encodeBytes(realData);
        out.add(Unpooled.buffer().writeBytes(resultData));
    }
}
