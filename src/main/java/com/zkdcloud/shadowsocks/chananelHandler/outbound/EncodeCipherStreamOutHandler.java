package com.zkdcloud.shadowsocks.chananelHandler.outbound;

import com.zkdcloud.shadowsocks.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.context.ContextConstant;
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
        byte[] resultData = cipher.encodeBytes(msg.array());
        out.add(Unpooled.buffer().writeBytes(resultData));
    }
}
