package com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound;

import com.zkdcloud.shadowsocks.common.bean.ClientConfig;
import com.zkdcloud.shadowsocks.common.bean.ServerConfig;
import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksConfigUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class CryptInitInHandler extends ChannelInboundHandlerAdapter{
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(CryptInitInHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx.channel().attr(ContextConstant.CIPHER).get() == null) {
            initAttribute(ctx);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channelId:{}, cause:{}", ctx.channel().id(), cause.getMessage(), cause);
        ctx.channel().close();
    }

    /**
     * init client attribute
     *
     * @param ctx client context
     */
    private void initAttribute(ChannelHandlerContext ctx) {
        //client config
        ClientConfig clientConfig = ShadowsocksConfigUtil.getClientConfigInstance();
        ctx.channel().attr(ContextConstant.CLIENT_CONFIG).setIfAbsent(clientConfig);

        // cipher
        AbstractCipher cipher = CipherProvider.getByName(clientConfig.getMethod(), clientConfig.getPassword());
        if (cipher == null) {
            ctx.channel().close();
            throw new IllegalArgumentException("un support server method: " + clientConfig.getMethod());
        } else {
            ctx.channel().attr(ContextConstant.CIPHER).set(cipher);
        }
    }
}
