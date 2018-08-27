package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

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
