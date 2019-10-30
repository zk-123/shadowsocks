package com.zkdcloud.shadowsocks.server.config;

import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import io.netty.channel.Channel;
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
    public static AttributeKey<SSCipher> SERVER_CIPHER = AttributeKey.valueOf("serverCipher");
    /**
     * serverConfig
     */
    public static AttributeKey<Channel> REMOTE_CHANNEL = AttributeKey.valueOf("remoteChannel");
    /**
     * clientConfig
     */
    public static AttributeKey<Channel> CLIENT_CHANNEL = AttributeKey.valueOf("clientChannel");
    /**
     * remote address
     */
    public static AttributeKey<InetSocketAddress> REMOTE_INET_SOCKET_ADDRESS = AttributeKey.valueOf("remoteAddress");
}
