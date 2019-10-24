package com.zkdcloud.shadowsocks.common.cipher.aead;

import com.zkdcloud.shadowsocks.common.cipher.IncompleteDealException;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * description
 *
 * @author zk
 * @since 2019/10/24
 */
public class SSAeadCipherWrapper implements SSCipher {
    private SSAeadCipher cipher;
    private byte[] cumulationSecretBytes;

    public SSAeadCipherWrapper(SSAeadCipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] decodeSSBytes(byte[] secretBytes) throws Exception {
        byte[] originBytes;
        try {
            originBytes = cipher.decodeSSBytes(getAndAddCumulation(secretBytes));
            cumulationSecretBytes = null;
        } catch (IncompleteDealException e) {
            originBytes = e.getDealBytes();
            addCumulation(ByteUtils.subArray(secretBytes, e.getDealLength()));
        }
        return originBytes;
    }

    @Override
    public byte[] encodeSSBytes(byte[] originBytes) throws Exception {
        return cipher.encodeSSBytes(originBytes);
    }

    private byte[] getAndAddCumulation(byte[] secretBytes){
        if(cumulationSecretBytes == null){
            return secretBytes;
        }
        return ByteUtils.concatenate(cumulationSecretBytes, secretBytes);
    }
    private void addCumulation(byte[] bytes){
        if(cumulationSecretBytes == null){
            cumulationSecretBytes = bytes;
        } else {
            cumulationSecretBytes = ByteUtils.concatenate(cumulationSecretBytes, bytes);
        }
    }
}
