package com.zkdcloud.shadowsocks.server.chananelHandler.inbound;

import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.server.config.ServerConfig;
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx.channel().attr(ServerContextConstant.SERVER_CIPHER).get() == null) {
            initAttribute(ctx);
        }

        super.channelRead(ctx, msg);
    }

    /**
     * init client attribute
     *
     * @param ctx client context
     */
    private void initAttribute(ChannelHandlerContext ctx) {
        // cipher
        SSCipher cipher = CipherProvider.getByName(ServerConfig.serverConfig.getMethod(), ServerConfig.serverConfig.getPassword());
        if (cipher == null) {
            ctx.channel().close();
            throw new IllegalArgumentException("un support server method: " + ServerConfig.serverConfig.getMethod());
        } else {
            ctx.channel().attr(ServerContextConstant.SERVER_CIPHER).set(cipher);
        }
    }
}
