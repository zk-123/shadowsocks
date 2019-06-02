package com.zkdcloud.shadowsocks.server.config;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class ServerContextConstant {

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
