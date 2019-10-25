package com.zkdcloud.shadowsocks.client.socks5.channelHandler.outbound;

import com.zkdcloud.shadowsocks.client.socks5.config.ClientContextConstant;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipher;
import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipherWrapper;
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
    private SSCipher cipherWrapper;
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        if(cipherWrapper == null){
            SSCipher cipher = ctx.channel().attr(ClientContextConstant.SOCKS5_CLIENT_CIPHER).get();
            cipherWrapper = new SSAeadCipherWrapper((SSAeadCipher) cipher);
        }
        byte[] secretBytes = new byte[msg.readableBytes()];
        msg.readBytes(secretBytes);
        try {
            out.writeBytes(cipherWrapper.decodeSSBytes(secretBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
