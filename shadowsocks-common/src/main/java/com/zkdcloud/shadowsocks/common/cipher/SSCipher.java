package com.zkdcloud.shadowsocks.common.cipher;


/**
 * description
 *
 * @author zk
 * @since 2019/10/23
 */
public interface SSCipher {
    /**
     * analysis ss protocol and decode bytes
     *
     * @param secretBytes secretBytes from stream
     * @return originBytes
     * @throws Exception ex
     */
    byte[] decodeSSBytes(byte[] secretBytes) throws Exception;

    /**
     * build as ss protocol and encode bytes
     *
     * @param originBytes originBytes
     * @return secretBytes
     * @throws Exception ex
     */
    byte[] encodeSSBytes(byte[] originBytes) throws Exception;
}
