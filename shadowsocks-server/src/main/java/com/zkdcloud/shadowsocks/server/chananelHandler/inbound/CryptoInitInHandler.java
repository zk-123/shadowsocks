package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.bean.ServerConfig;
import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksConfigUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * init crypto
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
        if (ctx.channel().attr(ContextConstant.CIPHER).get() == null) {
            initAttribute(ctx);
        }
        out.add(ctx.alloc().heapBuffer(msg.readableBytes()).writeBytes(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage());
    }

    /**
     * init client attribute
     *
     * @param ctx client context
     */
    private void initAttribute(ChannelHandlerContext ctx){
        //server config
        ServerConfig serverConfig = ShadowsocksConfigUtil.getServerConfigInstance();
        ctx.channel().attr(ContextConstant.SERVER_CONFIG).setIfAbsent(serverConfig);

        // cipher
        AbstractCipher cipher = CipherProvider.getByName(serverConfig.getMethod(), serverConfig.getPassword());
        if (cipher == null) {
            ctx.channel().close();
            throw new IllegalArgumentException("un support server method: " + serverConfig.getMethod());
        } else {
            ctx.channel().attr(ContextConstant.CIPHER).set(cipher);
        }
    }
}
