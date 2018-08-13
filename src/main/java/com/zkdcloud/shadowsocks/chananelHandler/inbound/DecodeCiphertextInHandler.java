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
public class DecodeCiphertextInHandler extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Aes128CfbCrypto aes128CfbCrypto = ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).get();
        byte[] bytes = aes128CfbCrypto.decode(msg.array());
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(" " + (short)bytes[i]);
        }
        System.out.println("end this");
    }
}
