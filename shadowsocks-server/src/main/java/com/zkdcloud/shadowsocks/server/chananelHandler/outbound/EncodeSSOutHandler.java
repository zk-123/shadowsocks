package com.zkdcloud.shadowsocks.server.chananelHandler.outbound;

import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * encode originBytes
 *
 * @author zk
 * @since 2018/8/14
 */
public class EncodeSSOutHandler extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        SSCipher cipher = ctx.channel().attr(ServerContextConstant.SERVER_CIPHER).get();

        byte[] originBytes = new byte[msg.readableBytes()];
        msg.getBytes(0, originBytes);
        byte[] secretBytes = cipher.encodeSSBytes(originBytes);

        if(secretBytes != null && secretBytes.length > 0){
            out.add(Unpooled.buffer().writeBytes(secretBytes));
        }
    }
}
