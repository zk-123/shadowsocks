package com.zkdcloud.shadowsocks.server.config;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class ServerContextConstant {

    /**
     * default idle time out(second)
     */
    public static long DEFAULT_IDLE_TIMEOUT_SECOND = 20 * 60;

    /**
     * server cipher
     */
    public static AttributeKey<AbstractCipher> SERVER_CIPHER = AttributeKey.valueOf("server cipher");
    /**
     * serverConfig
     */
    public static AttributeKey<ServerConfig> SERVER_CONFIG = AttributeKey.valueOf("serverConfig");
    /**
     * remote address
     */
    public static AttributeKey<InetSocketAddress> REMOTE_INET_SOCKET_ADDRESS = AttributeKey.valueOf("remoteAddress");
}
