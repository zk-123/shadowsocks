package com.zkdcloud.shadowsocks.client.socks5.channelHandler.outbound;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * decrypt remote rec secret message to clear text
 *
 * @author zk
 * @since 2018/8/29
 */
public class Socks5DecryptOutbound extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        AbstractCipher cipher = ctx.channel().attr(ContextConstant.CIPHER).get();
        out.writeBytes(cipher.decodeBytes(msg));
    }
}
