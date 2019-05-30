package com.zkdcloud.shadowsocks.server.config;

import io.netty.util.AttributeKey;

public class ServerContextConstant {
    /**
     * serverConfig
     */
    public static AttributeKey<ServerConfig> SERVER_CONFIG = AttributeKey.valueOf("serverConfig");
}
