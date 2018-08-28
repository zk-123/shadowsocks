package com.zkdcloud.shadowsocks.common.context;

import com.zkdcloud.shadowsocks.common.bean.ClientConfig;
import com.zkdcloud.shadowsocks.common.bean.ServerConfig;
import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import io.netty.util.AttributeKey;

/**
 * the constant of context
 *
 * @author zk
 * @since 2018/8/11
 */
public class ContextConstant {
    /**
     * cipher
     */
    public static AttributeKey<AbstractCipher> CIPHER = AttributeKey.valueOf("cipher");
    /**
     * serverConfig
     */
    public static AttributeKey<ServerConfig> SERVER_CONFIG = AttributeKey.valueOf("serverConfig");
    /**
     * clientConfig
     */
    public static AttributeKey<ClientConfig> CLIENT_CONFIG = AttributeKey.valueOf("clientConfig");

    /**
     * configProperties contains client and server
     */
    public interface ConfigProperties {
        /**
         * config.json of client
         */
        String CLIENT_CONFIG_FILE = "config.json";
        /**
         * server.json of server
         */
        String SERVER_CONFIG_FILE = "server.json";
    }
}
