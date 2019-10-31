package com.zkdcloud.shadowsocks.common.cipher.aead;

import com.zkdcloud.shadowsocks.common.cipher.exception.IncompleteDealException;
import com.zkdcloud.shadowsocks.common.cipher.SSCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * description
 *
 * @author zk
 * @since 2019/10/24
 */
public class SSAeadCipherWrapper implements SSCipher {
    private static short payloadSizeMask = 0x3FFF;

    private static byte[] EMPTY_BYTES = new byte[]{};
    private SSAeadCipher cipher;
    private byte[] cumulationSecretBytes = EMPTY_BYTES;

    public SSAeadCipherWrapper(SSAeadCipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] decodeSSBytes(byte[] secretBytes) throws Exception {
        byte[] originBytes;
        try {
            originBytes = cipher.decodeSSBytes(getAndAddCumulation(secretBytes));
            cumulationSecretBytes = EMPTY_BYTES;
        } catch (IncompleteDealException e) {
            originBytes = e.getDealBytes();
            addCumulation(e.getDealLength(), secretBytes);
        }
        return originBytes;
    }

    @Override
    public byte[] encodeSSBytes(byte[] originBytes) throws Exception {
        if(originBytes.length < payloadSizeMask){
            return cipher.encodeSSBytes(originBytes);
        }

        ByteBuf resultByteBuf = Unpooled.buffer();
        int readIndex = 0;
        while(originBytes.length - readIndex > 0){
            byte[] sliceOriginBytes = new byte[originBytes.length - readIndex > payloadSizeMask ? payloadSizeMask : originBytes.length - readIndex];
            System.arraycopy(originBytes, readIndex, sliceOriginBytes,0, sliceOriginBytes.length);
            resultByteBuf.writeBytes(cipher.encodeSSBytes(sliceOriginBytes));
            readIndex += sliceOriginBytes.length;
        }
        return ByteBufUtil.getBytes(resultByteBuf);
    }

    private byte[] getAndAddCumulation(byte[] secretBytes){
        return ByteUtils.concatenate(cumulationSecretBytes, secretBytes);
    }
    private void addCumulation(int dealLength, byte[] bytes){
        cumulationSecretBytes = ByteUtils.concatenate(cumulationSecretBytes, bytes);
        cumulationSecretBytes = ByteUtils.subArray(cumulationSecretBytes, dealLength);
    }
}
