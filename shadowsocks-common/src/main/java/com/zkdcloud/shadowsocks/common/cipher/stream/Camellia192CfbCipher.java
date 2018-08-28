package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

public class Camellia192CfbCipher extends LocalStreamCipher {
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Camellia192CfbCipher(String password) {
        super("camellia-192-cfb", password);
    }

    @Override
    public StreamCipher getNewCipherInstance() {
        return new CFBBlockCipher(new CamelliaEngine(), getKeyLength() * 8);
    }

    @Override
    public int getVILength() {
        return 16;
    }

    @Override
    public int getKeyLength() {
        return 24;
    }
}
