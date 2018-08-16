package com.zkdcloud.shadowsocks.cipher;

import com.zkdcloud.shadowsocks.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class Aes128CfbCipher extends AbstractCipher {
    /**
     * key length
     */
    private byte[] key = null;
    /**
     * 解密cipher
     */
    private StreamCipher decodeStreamCipher;
    /**
     * 加密cipher
     */
    private StreamCipher encodeStreamCipher;
    /**
     * 解密cipher是否已初始化
     */
    private boolean decodeInit;
    /**
     * 加密向量
     */
    private byte[] encodeViBytes = null;

    public Aes128CfbCipher(String password) {
        key = ShadowsocksUtils.getShadowsocksKey(password, getKeyLength());
        encodeStreamCipher = new CFBBlockCipher(new AESEngine(), getKeyLength() * 8);
    }

    @Override
    public byte[] decodeBytes(ByteBuf secretByteBuf) {
        if (!decodeInit) {
            //parameter
            byte[] viBytes = new byte[getVILength()];
            secretByteBuf.readBytes(viBytes);
            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(key), viBytes);

            //init
            decodeStreamCipher = new CFBBlockCipher(new AESEngine(), getKeyLength() * 8);
            decodeStreamCipher.init(false, viParameter);
            decodeInit = true;
        }

        //read bytes
        byte[] secretBytes = new byte[secretByteBuf.readableBytes()];
        secretByteBuf.readBytes(secretBytes, 0, secretByteBuf.readableBytes());

        byte[] target = new byte[secretBytes.length];
        decodeStreamCipher.processBytes(secretBytes, 0, secretBytes.length, target, 0);
        return target;
    }

    public byte[] encodeBytes(byte[] originBytes) {
        byte[] target = encodeViBytes == null ? new byte[getVILength() + originBytes.length] : new byte[originBytes.length];
        int outOff = 0;

        if (encodeViBytes == null) {
            encodeViBytes = getRandomBytes(getVILength());
            System.arraycopy(encodeViBytes, 0, target, 0, encodeViBytes.length);
            outOff = getVILength();

            CipherParameters viParameter = new ParametersWithIV(new KeyParameter(key), encodeViBytes);
            encodeStreamCipher.init(true, viParameter);
        }

        encodeStreamCipher.processBytes(originBytes, 0, originBytes.length, target, outOff);
        return target;
    }

    @Override
    public int getVILength() {
        return 16;
    }

    @Override
    public int getKeyLength() {
        return 16;
    }
}
