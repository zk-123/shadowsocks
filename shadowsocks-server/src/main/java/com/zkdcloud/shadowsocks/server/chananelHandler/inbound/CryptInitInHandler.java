package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.server.config.ServerConfig;
import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import com.zkdcloud.shadowsocks.common.context.ContextConstant;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksConfigUtil;
import com.zkdcloud.shadowsocks.server.config.ServerContextConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * init crypt
 *
 * @author zk
 * @since 2018/8/11
 */
public class CryptInitInHandler extends ChannelInboundHandlerAdapter {
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
        // cipher
        AbstractCipher cipher = CipherProvider.getByName(ServerConfig.serverConfig.getMethod(), ServerConfig.serverConfig.getPassword());
        if (cipher == null) {
            ctx.channel().close();
            throw new IllegalArgumentException("un support server method: " + ServerConfig.serverConfig.getMethod());
        } else {
            ctx.channel().attr(ContextConstant.CIPHER).set(cipher);
        }
    }
}
