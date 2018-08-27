package com.zkdcloud.shadowsocks.common.cipher;

import org.bouncycastle.crypto.StreamCipher;

/**
 * 流加密
 *
 * @author zk
 * @since 2018/8/27
 */
public abstract class LocalStreamCipher extends AbstractCipher {

    /**
     * 解密cipher
     */
    protected StreamCipher decodeStreamCipher;
    /**
     * 加密cipher
     */
    protected StreamCipher encodeStreamCipher;
    /**
     * 解密cipher是否已初始化
     */
    protected boolean decodeInit;
    /**
     * 加密向量
     */
    protected byte[] encodeViBytes = null;
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public LocalStreamCipher(String password) {
        super(password);
    }

    /**
     * 获取向量长度
     *
     * @return 向量长度
     */
    public abstract int getVILength();

    public abstract byte[] getEncodeViBytes();
}
