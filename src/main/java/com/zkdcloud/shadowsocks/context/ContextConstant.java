package com.zkdcloud.shadowsocks.context;

import com.zkdcloud.shadowsocks.cipher.Aes128CfbCrypto;
import io.netty.util.AttributeKey;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class ContextConstant {
    public static AttributeKey<Aes128CfbCrypto> AES_128_CFB_KEY = AttributeKey.valueOf("aes128");
}
