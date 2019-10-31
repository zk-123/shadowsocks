package com.zkdcloud.shadowsocks.server.config;

import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class ServerContextConstant {

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
