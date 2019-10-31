package com.zkdcloud.shadowsocks.common.cipher;

import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipher;
import com.zkdcloud.shadowsocks.common.cipher.aead.SSAeadCipherWrapper;
import com.zkdcloud.shadowsocks.common.cipher.stream.SSStreamCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * cipherProvider
 *
 * @author zk
 * @since 2018/8/28
 */
public class CipherProvider {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(CipherProvider.class);

    private static Set<String> streamCiphers = new HashSet<>();

    private static Set<String> aeadCiphers = new HashSet<>();


    static {
        /* stream */
        streamCiphers.add("aes-128-cfb");
        streamCiphers.add("aes-192-cfb");
        streamCiphers.add("aes-256-cfb");
        streamCiphers.add("camellia-128-cfb");
        streamCiphers.add("camellia-192-cfb");
        streamCiphers.add("camellia-256-cfb");
        streamCiphers.add("chacha20");
        streamCiphers.add("chacha20-ietf");
        streamCiphers.add("rc4-md5");
        streamCiphers.add("salsa20");

        /* aead */
        aeadCiphers.add("aes-128-gcm");
        aeadCiphers.add("aes-192-gcm");
        aeadCiphers.add("aes-256-gcm");
    }

    /**
     * get Cipher by standard cipherName
     *
     * @param cipherMethodName cipherMethodName
     * @param password     password
     * @return new cipher instance
     */
    public static SSCipher getByName(String cipherMethodName, String password) {
        if (streamCiphers.contains(cipherMethodName)) {
            try {
                Constructor<SSStreamCipher> cipherClazzConstructor = SSStreamCipher.class.getConstructor(String.class, String.class);
                return cipherClazzConstructor.newInstance(cipherMethodName, password);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                logger.error("get init cipher fail: {}", e.getMessage());
                return null;
            }
        }

        if (aeadCiphers.contains(cipherMethodName)) {
            try {
                Constructor<SSAeadCipher> cipherClazzConstructor = SSAeadCipher.class.getConstructor(String.class, String.class);
                return new SSAeadCipherWrapper(cipherClazzConstructor.newInstance(cipherMethodName, password));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                logger.error("get init cipher fail: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    public static List<String> getSupportCiphersNames(){
        List<String> result = new ArrayList<>(streamCiphers.size() + aeadCiphers.size());
        result.addAll(streamCiphers);
        result.addAll(aeadCiphers);
        return result;
    }
}
