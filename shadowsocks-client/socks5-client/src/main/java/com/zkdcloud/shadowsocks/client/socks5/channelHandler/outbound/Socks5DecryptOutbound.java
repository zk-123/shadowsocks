package com.zkdcloud.shadowsocks.client.socks5.channelHandler.outbound;

import com.zkdcloud.shadowsocks.client.socks5.config.ClientContextConstant;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * decrypt remote rec secret message to origin text
 *
 * @author zk
 * @since 2018/8/29
 */
public class Socks5DecryptOutbound extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        SSCipher cipher = ctx.channel().attr(ClientContextConstant.SOCKS5_CLIENT_CIPHER).get();

        byte[] secretBytes = new byte[msg.readableBytes()];
        msg.readBytes(secretBytes);
        byte[] originBytes = cipher.decodeSSBytes(secretBytes);
        out.writeBytes(originBytes);
    }
}
