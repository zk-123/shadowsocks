package com.zkdcloud.shadowsocks.client.socks5.context;

import com.zkdcloud.shadowsocks.common.bean.ClientConfig;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * description
 *
 * @author zk
 * @since 2018/8/20
 */
public class ClientContextConstant {
    /**
     * socket 5 flag
     */
    public static byte SOCKS5_VERSION = 0x05;
    /**
     * queryAddress constant
     */
    public static AttributeKey<InetSocketAddress> QUERY_ADDRESS = AttributeKey.valueOf("queryAddress");
    /**
     * client Config constant
     */
    public static AttributeKey<ClientConfig> CLIENT_CONFIG = AttributeKey.valueOf("clientConfig");
    /**
     * client first encoding flag
     */
    public static AttributeKey<Boolean> FIRST_ENCODING = AttributeKey.valueOf("firstEncoding");
}
