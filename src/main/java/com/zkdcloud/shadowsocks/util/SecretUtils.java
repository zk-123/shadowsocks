package com.zkdcloud.shadowsocks.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class SecretUtils {

    public static byte[] getShadowsocksKey(String password, int keyLength) {
        byte[] result = new byte[keyLength];
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            for (int hasLength = 0; hasLength < keyLength; hasLength += 16) {
                byte[] passwordBytes = password.getBytes();

                //组合需要摘要的byte[]
                byte[] combineBytes = new byte[hasLength + passwordBytes.length];
                System.arraycopy(result, 0, combineBytes, 0, hasLength);
                System.arraycopy(passwordBytes, 0, combineBytes, hasLength, passwordBytes.length);

                //增加
                byte[] digestBytes = messageDigest.digest(combineBytes);
                int addLength = hasLength + 16 > keyLength ? keyLength - hasLength : 16;
                System.arraycopy(digestBytes,0,result,hasLength,addLength);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
