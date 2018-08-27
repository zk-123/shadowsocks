package com.zkdcloud.shadowsocks.common.cipher;

import io.netty.buffer.ByteBuf;

/**
 * Rc4 cipher
 *
 * @author zk
 * @since 2018/8/27
 */
public class Rc4Md5CipherLocal extends LocalStreamCipher {
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Rc4Md5CipherLocal(String password) {
        super(password);
    }

    @Override
    public byte[] decodeBytes(ByteBuf secretByteBuf) {
        return new byte[0];
    }

    @Override
    public byte[] decodeBytes(byte[] secretBytes) {
        return new byte[0];
    }

    @Override
    public byte[] encodeBytes(byte[] originBytes) {
        return new byte[0];
    }

    @Override
    public int getVILength() {
        return 0;
    }

    @Override
    public int getKeyLength() {
        return 0;
    }

    @Override
    public byte[] getEncodeViBytes() {
        return new byte[0];
    }
}
