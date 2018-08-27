package com.zkdcloud.shadowsocks.common.cipher;

import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * aes-128
 *
 * @author zk
 * @since 2018/8/11
 */
public class Aes128CfbCipher extends LocalStreamCipher {


    public Aes128CfbCipher(String password) {
        super(password);
        encodeStreamCipher = new CFBBlockCipher(new AESEngine(), getKeyLength() * 8);
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

    @Override
    public int getVILength() {
        return 16;
    }

    @Override
    public int getKeyLength() {
        return 16;
    }

    @Override
    public byte[] getEncodeViBytes() {
        return encodeViBytes;
    }

}
