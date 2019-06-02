package com.zkdcloud.shadowsocks.client.socks5.config;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class ClientContextConstant {

    /**
     * socks5 client cipher
     */
    public static AttributeKey<AbstractCipher> SOCKS5_CLIENT_CIPHER = AttributeKey.valueOf("socks5 client cipher");

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
