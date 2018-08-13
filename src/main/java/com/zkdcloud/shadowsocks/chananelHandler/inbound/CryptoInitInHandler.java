package com.zkdcloud.shadowsocks.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.context.ContextConstant;
import com.zkdcloud.shadowsocks.cipher.Aes128CfbCrypto;
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
public class CryptoInitInHandler extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).set(new Aes128CfbCrypto("123456"));
        out.add(ctx.alloc().heapBuffer().writeBytes(msg));
        ctx.pipeline().remove(this);
    }
}
