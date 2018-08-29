package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

/**
 * aes-128
 *
 * @author zk
 * @since 2018/8/11
 */
public class Aes128CfbCipher extends LocalStreamCipher {

    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Aes128CfbCipher(String password) {
        super("aes-128-cfb", password);
    }

    @Override
    public StreamCipher getNewCipherInstance() {
        return new CFBBlockCipher(new AESEngine(), getVILength() * 8);
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
