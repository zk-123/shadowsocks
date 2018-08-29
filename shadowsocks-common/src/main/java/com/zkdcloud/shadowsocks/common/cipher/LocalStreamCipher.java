package com.zkdcloud.shadowsocks.common.cipher;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

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
    protected byte[] encodeIVBytes = null;

    /**
     * localStreamCipher
     *
     * @param cipherName cipherName
     * @param password   password
     */
    public LocalStreamCipher(String cipherName, String password) {
        super(cipherName, password);
        decodeStreamCipher = getNewCipherInstance();
        encodeStreamCipher = getNewCipherInstance();
    }

    @Override
    public byte[] decodeBytes(ByteBuf secretByteBuf) {
        return decodeBytes(ShadowsocksUtils.readRealBytes(secretByteBuf));
    }

    @Override
    public byte[] decodeBytes(byte[] secretBytes) {
        int offset = 0;
        if (!decodeInit) {
            //parameter
            byte[] ivBytes = new byte[getVILength()];
            System.arraycopy(secretBytes, 0, ivBytes, 0, offset = getVILength());

            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(getKey()), ivBytes);

            //init
            decodeStreamCipher.init(false, viParameter);
            decodeInit = true;
        }

        byte[] target = new byte[secretBytes.length - offset];
        decodeStreamCipher.processBytes(secretBytes, offset, secretBytes.length - offset, target, 0);
        return target;
    }

    @Override
    public byte[] encodeBytes(byte[] originBytes) {
        byte[] target;
        target = encodeIVBytes == null ? new byte[getVILength() + originBytes.length] : new byte[originBytes.length];
        int outOff = 0;

        if (encodeIVBytes == null) {
            encodeIVBytes = getRandomBytes(getVILength());
            System.arraycopy(encodeIVBytes, 0, target, 0, encodeIVBytes.length);
            outOff = getVILength();

            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(getKey()), encodeIVBytes);
            encodeStreamCipher.init(true, viParameter);
        }

        encodeStreamCipher.processBytes(originBytes, 0, originBytes.length, target, outOff);
        return target;
    }

    /**
     * 获取新的StreamCipher
     *
     * @return streamCipher
     */
    public abstract StreamCipher getNewCipherInstance();

    /**
     * 获取向量长度
     *
     * @return 向量长度
     */
    public abstract int getVILength();

    public byte[] getEncodeViBytes() {
        return encodeIVBytes;
    }
}
