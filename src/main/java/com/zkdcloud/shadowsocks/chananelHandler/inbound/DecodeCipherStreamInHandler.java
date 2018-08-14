package com.zkdcloud.shadowsocks.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.context.ContextConstant;
import com.zkdcloud.shadowsocks.cipher.Aes128CfbCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class DecodeCipherStreamInHandler extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Aes128CfbCipher cipher = ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).get();
        byte[] realBytes = cipher.decodeBytes(msg.array());
//        out.add(ctx.alloc().heapBuffer().)
    }
}
