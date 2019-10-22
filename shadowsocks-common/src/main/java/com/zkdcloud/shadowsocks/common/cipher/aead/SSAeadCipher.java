package com.zkdcloud.shadowsocks.common.cipher.aead;

import com.zkdcloud.shadowsocks.common.util.HeapByteBufUtil;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.security.SecureRandom;

/**
 * aes 256 gcm
 *
 * @author zk
 * @since 2019/10/18
 */
public class SSAeadCipher {
    private String cipherMethod;
    private String password;

    private byte[] decodeSubKey;
    private byte[] decodeNonceBytes;
    private GCMBlockCipher decodeGcmCipher;

    private byte[] encodeSubKey;
    private byte[] encodeNonceBytes;
    private GCMBlockCipher encodeGcmCipher;

    public SSAeadCipher(String cipherMethod, String password) {
        this.cipherMethod = cipherMethod;
        this.password = password;
        decodeNonceBytes = new byte[getNonceSize()];
        encodeNonceBytes = new byte[getNonceSize()];
    }

    /**
     * analysis ss protocol and decode bytes
     *
     * @param secretBytes secretBytes from stream
     * @return originBytes
     * @throws InvalidCipherTextException ex
     */
    public byte[] decodeSSBytes(byte[] secretBytes) throws InvalidCipherTextException {
        int readIndex = 0;
        if (decodeSubKey == null) {
            byte[] salt = new byte[getSaltSize()];
            System.arraycopy(secretBytes, 0, salt, 0, salt.length);
            decodeSubKey = getSubKey(ShadowsocksUtils.getShadowsocksKey(password, getKeySize()), salt);
            readIndex += salt.length;
        }

        ByteBuf originBytesSummary = Unpooled.buffer();
        while (readIndex < secretBytes.length) {
            //decode length
            byte[] secretLength = new byte[2 + getTagSize()];
            System.arraycopy(secretBytes, readIndex, secretLength, 0, secretLength.length);
            int originLength = HeapByteBufUtil.getShort(aeDecodeBytes(aeDecodeBytes(secretLength)), 0);
            readIndex += secretLength.length;

            //decode payload
            byte[] secretPayload = new byte[originLength + getTagSize()];
            System.arraycopy(secretBytes, readIndex, secretPayload, 0, secretPayload.length);
            byte[] originPayload = aeDecodeBytes(secretPayload);
            readIndex += secretPayload.length;
            originBytesSummary.writeBytes(originPayload);
        }
        ReferenceCountUtil.release(originBytesSummary);
        return originBytesSummary.array();
    }

    public byte[] encodeSSBytes(byte[] originBytes){
        ByteBuf secretBytesSummary = Unpooled.buffer();
        if(encodeSubKey == null){
            byte[] salt = getRandomBytes(getSaltSize());
            getSubKey()
        }
    }

    /**
     * decode secret aeadBytes
     *
     * @param secretBytes [secretBytes][tag]
     * @return originBytes
     * @throws InvalidCipherTextException ex
     */
    private byte[] aeDecodeBytes(byte[] secretBytes) throws InvalidCipherTextException {
        if (decodeGcmCipher == null) {
            decodeGcmCipher = new GCMBlockCipher(new AESEngine());
        }
        decodeGcmCipher.init(false, new AEADParameters(new KeyParameter(decodeSubKey), getTagSize() * 8, getDecodeNonceBytes()));

        byte[] out = new byte[secretBytes.length - getTagSize()];
        int processLength = decodeGcmCipher.processBytes(secretBytes, 0, secretBytes.length, out, 0);
        decodeGcmCipher.doFinal(out, processLength);
        return out;
    }

    private byte[] getSubKey(byte[] key, byte[] salt) {
        byte[] result = new byte[32];
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA1Digest());
        HKDFParameters hkdfParameters = new HKDFParameters(key, salt, "ss-subkey".getBytes());
        hkdfBytesGenerator.init(hkdfParameters);
        hkdfBytesGenerator.generateBytes(result, 0, 32);
        return result;
    }

    private byte[] getDecodeNonceBytes() {
        try {
            byte[] nonceBytes = ByteUtils.clone(decodeNonceBytes);
            return nonceBytes;
        } finally {
            incrementDecodeNonce();
        }
    }

    // increment little-endian encoded unsigned integer b. Wrap around on overflow.
    private void incrementDecodeNonce() {
        for (int i = 0; i < decodeNonceBytes.length; i++) {
            decodeNonceBytes[i]++;
            if (decodeNonceBytes[i] != 0) {
                break;
            }
        }
    }

    private int getKeySize() {
        switch (cipherMethod) {
            case "aes-128-gcm":
                return 16;
            case "aes-192-gcm":
                return 24;
            case "aes-256-gcm":
                return 32;
            default:
                throw new IllegalArgumentException("not support method: " + cipherMethod);
        }
    }

    private int getSaltSize() {
        switch (cipherMethod) {
            case "aes-128-gcm":
                return 16;
            case "aes-192-gcm":
                return 24;
            case "aes-256-gcm":
                return 32;
            default:
                throw new IllegalArgumentException("not support method: " + cipherMethod);
        }
    }

    private int getTagSize() {
        return 16;
    }

    private int getNonceSize() {
        return 12;
    }

    /**
     * 生成随机数 byte
     *
     * @param size 位数
     * @return random of bytes
     */
    private byte[] getRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}