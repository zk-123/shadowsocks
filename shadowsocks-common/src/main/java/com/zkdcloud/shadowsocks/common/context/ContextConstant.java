package com.zkdcloud.shadowsocks.common.context;

import com.zkdcloud.shadowsocks.common.config.ClientConfig;
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
        /**
         * default timeOut seconds
         */
        Long DEFAULT_TIMEOUT_TIME = 300L;
    }
}
