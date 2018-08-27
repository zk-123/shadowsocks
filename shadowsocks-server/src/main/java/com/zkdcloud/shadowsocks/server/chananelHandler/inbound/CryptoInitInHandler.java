package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.cipher.stream.Aes128CfbCipher;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class CryptoInitInHandler extends MessageToMessageDecoder<ByteBuf> {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(CryptoInitInHandler.class);

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if(ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).get() == null){
            ctx.channel().attr(ContextConstant.AES_128_CFB_KEY).set(new Aes128CfbCipher("123456"));
        }
        out.add(ctx.alloc().heapBuffer(msg.readableBytes()).writeBytes(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channelId:{}, cause:{}",ctx.channel().id(),cause.getMessage());
    }
}
