package com.zkdcloud.shadowsocks.client.socks5.context;

import io.netty.channel.Channel;
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
     * client first encoding flag
     */
    public static AttributeKey<Boolean> FIRST_ENCODING = AttributeKey.valueOf("firstEncoding");

    /**
     * 目标地址
     */
    public static AttributeKey<InetSocketAddress> DST_ADDRESS = AttributeKey.valueOf("dstAddress");
    /**
     * 代理通道
     */
    public static AttributeKey<Channel> REMOTE_CHANNEL = AttributeKey.valueOf("remoteChannel");

}
