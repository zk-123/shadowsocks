package com.zkdcloud.shadowsocks.server.context;

import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * serverContextConstant
 *
 * @author zk
 * @since 2018/8/11
 */
public class ServerContextConstant {
    /**
     * remote address
     */
    public static AttributeKey<InetSocketAddress> REMOTE_INET_SOCKET_ADDRESS = AttributeKey.valueOf("remoteAddress");
}
