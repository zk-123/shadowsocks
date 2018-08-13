package com.zkdcloud.shadowsocks.cipher;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class Aes128CfbCrypto {
    /**
     * key length
     */
    private byte[] key = new byte[16];

    public Aes128CfbCrypto(String password) {
        initKey(password);
    }

    private void initKey(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            key = messageDigest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] decode(byte[] originByte) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(originByte,0,16));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivSpec);
            return cipher.doFinal(originByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
