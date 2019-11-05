package com.zkdcloud.shadowsocks.common.cipher.stream;

import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * stream cipher
 *
 * @author zk
 * @since 2018/8/27
 */
public class SSStreamCipher implements SSCipher {

    private String cipherMethodName;
    private String password;
    /**
     * decode cipher
     */
    private StreamCipher decodeStreamCipher;
    /**
     * decode cipher
     */
    private StreamCipher encodeStreamCipher;
    /**
     * decodeCipher is/not init
     */
    private boolean decodeInit;
    /**
     * encode iv bytes
     */
    private byte[] encodeIVBytes = null;

    public SSStreamCipher(String cipherMethodName, String password) {
        this.cipherMethodName = cipherMethodName;
        this.password = password;

        decodeStreamCipher = getNewCipherInstance();
        encodeStreamCipher = getNewCipherInstance();
    }

    @Override
    public byte[] decodeSSBytes(byte[] secretBytes) throws Exception {
        int offset = 0;
        if (!decodeInit) {
            //parameter
            byte[] ivBytes = new byte[getVILength()];
            System.arraycopy(secretBytes, 0, ivBytes, 0, offset = getVILength());

            CipherParameters viParameter;
            if("rc4-md5".equals(cipherMethodName)){
                viParameter = new KeyParameter(getRc4KeyBytes(ivBytes));
            } else {
                viParameter = new ParametersWithIV(new KeyParameter(ShadowsocksUtils.getShadowsocksKey(password, getKeySize())), ivBytes);
            }

            //init
            decodeStreamCipher.init(false, viParameter);
            decodeInit = true;
        }

        byte[] target = new byte[secretBytes.length - offset];
        decodeStreamCipher.processBytes(secretBytes, offset, secretBytes.length - offset, target, 0);
        return target;
    }

    @Override
    public byte[] encodeSSBytes(byte[] originBytes) throws Exception {
        byte[] target;
        target = encodeIVBytes == null ? new byte[getVILength() + originBytes.length] : new byte[originBytes.length];
        int outOff = 0;

        if (encodeIVBytes == null) {
            encodeIVBytes = getRandomBytes(getVILength());
            System.arraycopy(encodeIVBytes, 0, target, 0, encodeIVBytes.length);
            outOff = getVILength();

            CipherParameters viParameter;
            if("rc4-md5".equals(cipherMethodName)){
                viParameter = new KeyParameter(getRc4KeyBytes(encodeIVBytes));
            } else {
                viParameter = new ParametersWithIV(new KeyParameter(ShadowsocksUtils.getShadowsocksKey(password, getKeySize())), encodeIVBytes);
            }
            encodeStreamCipher.init(true, viParameter);
        }

        encodeStreamCipher.processBytes(originBytes, 0, originBytes.length, target, outOff);
        return target;
    }

    /**
     * get newStreamCipher
     *
     * @return streamCipher
     */
    private StreamCipher getNewCipherInstance() {
        switch (cipherMethodName) {
            case "aes-128-cfb":
            case "aes-192-cfb":
            case "aes-256-cfb":
                return new CFBBlockCipher(new AESEngine(), getVILength() * 8);
            case "camellia-128-cfb":
            case "camellia-192-cfb":
            case "camellia-256-cfb":
                return new CFBBlockCipher(new CamelliaEngine(), getVILength() * 8);
            case "rc4-md5":
                return new RC4Engine();
            case "chacha20-ietf":
                return new ChaCha7539Engine();
            case "chacha20":
                return new ChaChaEngine();
            case "salsa20":
                return new Salsa20Engine();
            default:
                throw new IllegalArgumentException("not support method：" + cipherMethodName);
        }
    }

    /**
     * get keySize
     *
     * @return keySize
     */
    private int getKeySize() {
        switch (cipherMethodName) {
            case "aes-128-cfb":
                return 16;
            case "aes-192-cfb":
                return 24;
            case "aes-256-cfb":
                return 32;
            case "camellia-128-cfb":
                return 16;
            case "camellia-192-cfb":
                return 24;
            case "camellia-256-cfb":
                return 32;
            case "rc4-md5":
                return 16;
            case "chacha20-ietf":
                return 32;
            case "chacha20":
                return 32;
            case "salsa20":
                return 32;
            default:
                throw new IllegalArgumentException("not support method：" + cipherMethodName);
        }
    }

    /**
     * get iv length
     *
     * @return iv length
     */
    private int getVILength() {
        switch (cipherMethodName) {
            case "aes-128-cfb":
            case "aes-192-cfb":
            case "aes-256-cfb":
            case "camellia-128-cfb":
            case "camellia-192-cfb":
            case "camellia-256-cfb":
            case "rc4-md5":
                return 16;
            case "chacha20-ietf":
                return 12;
            case "chacha20":
            case "salsa20":
                return 8;
            default:
                throw new IllegalArgumentException("not support method：" + cipherMethodName);
        }
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

    /**
     * rc5-md5 keyBytes
     *
     * @param ivBytes ivBytes
     * @return keyParameterBytes
     */
    private byte[] getRc4KeyBytes(byte[] ivBytes) {
        byte[] result = new byte[getKeySize() + getVILength()];
        System.arraycopy(ShadowsocksUtils.getShadowsocksKey(password, getKeySize()), 0, result, 0, getKeySize());
        System.arraycopy(ivBytes, 0, result, getKeySize(), ivBytes.length);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            return messageDigest.digest(result);
        } catch (NoSuchAlgorithmException e) {
            //ignore it
        }
        return new byte[]{};
    }
}
