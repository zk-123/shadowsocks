package com.zkdcloud.shadowsocks.cipher;

import com.zkdcloud.shadowsocks.util.ShadowsocksUtils;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.Arrays;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class Aes128CfbCipher extends AbstractCipher{
    /**
     * key length
     */
    private byte[] key = null;

    public Aes128CfbCipher(String password) {
        key = ShadowsocksUtils.getShadowsocksKey(password,getKeyLength());
    }

    @Override
    public byte[] decodeBytes(byte[] secretBytes) {
        byte[] target = new byte[secretBytes.length - getVILength()];

        //parameter
        CipherParameters viParameter = new ParametersWithIV(new KeyParameter(key),Arrays.copyOfRange(secretBytes,0,getVILength()));

        //init
        CFBBlockCipher streamCipher = new CFBBlockCipher(new AESEngine(),getKeyLength() * 8);
        streamCipher.init(false,viParameter);

        byte[] data = new byte[secretBytes.length - getVILength()];
        System.arraycopy(secretBytes,16,data,0,data.length);
        streamCipher.processBytes(data,0,data.length,target,0);
        return target;
    }

    public byte[] encodeBytes(byte[] originBytes){
        byte[] target = new byte[originBytes.length];
        CipherParameters viParameter = new ParametersWithIV(new KeyParameter(key),getRandomBytes(getVILength()));

        CFBBlockCipher streamCipher = new CFBBlockCipher(new AESEngine(),getKeyLength() * 8);
        streamCipher.init(true,viParameter);

        streamCipher.processBytes(originBytes,0,originBytes.length,target,0);
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
