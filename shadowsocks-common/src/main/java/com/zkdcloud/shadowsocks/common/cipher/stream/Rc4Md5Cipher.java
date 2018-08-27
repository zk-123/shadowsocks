package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;

/**
 * Rc4 cipher
 *
 * @author zk
 * @since 2018/8/27
 */
public class Rc4Md5Cipher extends LocalStreamCipher {


    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Rc4Md5Cipher(String password) {
        super(password);

    }

    @Override
    public StreamCipher getNewCipherInstance() {
        return new RC4Engine();
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
