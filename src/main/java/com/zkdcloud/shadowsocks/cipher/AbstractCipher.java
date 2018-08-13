package com.zkdcloud.shadowsocks.cipher;

import org.bouncycastle.crypto.StreamCipher;

/**
 * 解密/加密工具
 *
 * @author zk
 * @since 2018/8/11
 */
public abstract class AbstractCipher {
    /**
     * 密钥
     */
    private byte[] keySecret;
    /**
     * 解密
     */
    private StreamCipher streamCipher;

    public AbstractCipher(){

    }

    private void initKeySecret(){

    }
    /**
     * 解密
     *
     * @param secretBytes 密文
     * @return 明文
     */
    public abstract byte[] decodeBytes(byte[] secretBytes);

    /**
     * 获取向量长度
     *
     * @return 向量长度
     */
    public abstract int getVILength();

    /**
     * 获取密钥长度
     *
     * @return 密钥长度
     */
    public abstract int getKeyLength();
}
