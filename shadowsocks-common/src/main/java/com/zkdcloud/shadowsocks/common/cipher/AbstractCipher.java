package com.zkdcloud.shadowsocks.common.cipher;

import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.StreamCipher;

import java.security.SecureRandom;

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
     * @param secretByteBuf 密文
     * @return 明文
     */
    public abstract byte[] decodeBytes(ByteBuf secretByteBuf);

    /**
     * 加密
     *
     * @param originBytes 明文
     * @return 密文
     */
    public abstract byte[] encodeBytes(byte[] originBytes);
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

    public abstract byte[] getEncodeViBytes();

    /**
     * 生成随机数 byte
     *
     * @param size 位数
     * @return random of bytes
     */
    protected byte[] getRandomBytes(int size){
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
