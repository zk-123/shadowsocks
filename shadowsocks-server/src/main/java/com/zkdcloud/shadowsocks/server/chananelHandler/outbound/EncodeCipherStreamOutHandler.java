package com.zkdcloud.shadowsocks.server.chananelHandler.outbound;

import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * encode laws text
 *
 * @author zk
 * @since 2018/8/14
 */
public class EncodeCipherStreamOutHandler extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        SSCipher cipher = ctx.channel().attr(ServerContextConstant.SERVER_CIPHER).get();

        byte[] realData = new byte[msg.readableBytes()];
        msg.getBytes(0, realData);

        byte[] resultData = cipher.encodeSSBytes(realData);
        out.add(Unpooled.buffer().writeBytes(resultData));
    }
}
