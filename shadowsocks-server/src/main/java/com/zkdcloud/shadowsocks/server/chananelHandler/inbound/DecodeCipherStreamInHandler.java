package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.cipher.IncompleteDealException;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipher;
import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipherWrapper;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * decode secret bytes
 *
 * @author zk
 * @since 2018/8/11
 */
public class DecodeCipherStreamInHandler extends ReplayingDecoder {

    private SSCipher cipherWrapper;

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        SSCipher cipher = ctx.channel().attr(ServerContextConstant.SERVER_CIPHER).get();
        if(cipherWrapper == null){
            cipherWrapper = new SSAeadCipherWrapper((SSAeadCipher) cipher);
        }
        byte[] secretBytes = new byte[msg.writerIndex()];
        msg.readBytes(secretBytes);

        byte[] originBytes;
        try {
            originBytes = cipherWrapper.decodeSSBytes(secretBytes);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }


        if (originBytes != null && originBytes.length != 0) {
            ByteBuf nextMsg = Unpooled.buffer().writeBytes(originBytes);
            // get Ip
            if (ctx.channel().attr(ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS).get() == null) {
                InetSocketAddress inetSocketAddress = ShadowsocksUtils.getIp(nextMsg);
                if (inetSocketAddress == null) {
                    ctx.channel().close();
                    return;
                }

                ctx.channel().attr(ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS).set(inetSocketAddress);
            }
            out.add(nextMsg);
        }
    }
}
