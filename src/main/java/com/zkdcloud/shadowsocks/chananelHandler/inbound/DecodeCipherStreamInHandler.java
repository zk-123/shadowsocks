package com.zkdcloud.shadowsocks.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.cipher.Aes128CfbCipher;
import com.zkdcloud.shadowsocks.context.ContextConstant;
import com.zkdcloud.shadowsocks.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import static com.zkdcloud.shadowsocks.context.ContextConstant.REMOTE_INET_SOCKET_ADDRESS;

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

        ByteBuf realMsg = ctx.alloc().heapBuffer().writeBytes(realBytes);
        // get Ip
        InetSocketAddress inetSocketAddress = ShadowsocksUtils.getIp(realMsg);
        ctx.channel().attr(REMOTE_INET_SOCKET_ADDRESS).set(inetSocketAddress);
        out.add(realMsg.retain());
    }
}
