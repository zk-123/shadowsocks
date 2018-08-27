package com.zkdcloud.shadowsocks.common.cipher;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
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
    protected byte[] encodeViBytes = null;
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public LocalStreamCipher(String password) {
        super(password);
    }

    public LocalStreamCipher(String password){

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
            byte[] viBytes = new byte[getVILength()];
            System.arraycopy(secretBytes, 0, viBytes, 0, offset = getVILength());

            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(getKey()), viBytes);

            //init
            decodeStreamCipher = new CFBBlockCipher(new AESEngine(), getKeyLength() * 8);
            decodeStreamCipher.init(false, viParameter);
            decodeInit = true;
        }

        byte[] target = new byte[secretBytes.length - offset];
        decodeStreamCipher.processBytes(secretBytes, offset, secretBytes.length - offset, target, 0);
        return target;
    }

    @Override
    public byte[] encodeBytes(byte[] originBytes) {
        byte[] target = encodeViBytes == null ? new byte[getVILength() + originBytes.length] : new byte[originBytes.length];
        int outOff = 0;

        if (encodeViBytes == null) {
            encodeViBytes = getRandomBytes(getVILength());
            System.arraycopy(encodeViBytes, 0, target, 0, encodeViBytes.length);
            outOff = getVILength();

            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(getKey()), encodeViBytes);
            encodeStreamCipher.init(true, viParameter);
        }

        encodeStreamCipher.processBytes(originBytes, 0, originBytes.length, target, outOff);
        return target;
    }

    /**
     * 获取向量长度
     *
     * @return 向量长度
     */
    public abstract int getVILength();

    public abstract byte[] getEncodeViBytes();
}
