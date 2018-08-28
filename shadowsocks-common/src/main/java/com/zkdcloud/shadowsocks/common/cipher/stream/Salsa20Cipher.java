package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.Salsa20Engine;

public class Salsa20Cipher extends LocalStreamCipher {
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Salsa20Cipher(String password) {
        super("salsa20", password);
    }

    @Override
    public StreamCipher getNewCipherInstance() {
        return new Salsa20Engine();
    }

    @Override
    public int getVILength() {
        return 8;
    }

    @Override
    public int getKeyLength() {
        return 32;
    }
}
