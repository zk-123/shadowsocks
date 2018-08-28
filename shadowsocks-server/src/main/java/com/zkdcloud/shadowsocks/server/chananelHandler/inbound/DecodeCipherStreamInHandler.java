package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.cipher.stream.Aes128CfbCipher;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import static com.zkdcloud.shadowsocks.server.context.ServerContextConstant.REMOTE_INET_SOCKET_ADDRESS;

/**
 * decode secret bytes
 *
 * @author zk
 * @since 2018/8/11
 */
public class DecodeCipherStreamInHandler extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        AbstractCipher cipher = ctx.channel().attr(ContextConstant.CIPHER).get();
        byte[] realBytes = cipher.decodeBytes(msg);

        msg.clear().writeBytes(realBytes);
        // get Ip
        if(ctx.channel().attr(REMOTE_INET_SOCKET_ADDRESS).get() == null){
            InetSocketAddress inetSocketAddress = ShadowsocksUtils.getIp(msg);
            if(inetSocketAddress == null){
                ctx.channel().close();
                return;
            }

            ctx.channel().attr(REMOTE_INET_SOCKET_ADDRESS).set(inetSocketAddress);
        }
        out.add(msg.retain());
    }
}
