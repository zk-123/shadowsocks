package com.zkdcloud.shadowsocks.common.cipher.ahead;

import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.util.HeapByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * aes 256 gcm
 *
 * @author zk
 * @since 2019/10/18
 */
public class Aes256GcmCipher extends AbstractCipher {
    private int nonceSize = 12;
    private byte[] decodeNonceBytes;
    private GCMBlockCipher decodeGcmcopher;

    public Aes256GcmCipher(String password) {
        super("aes-256-gcm", password);
        decodeNonceBytes = new byte[12];
    }

    public Aes256GcmCipher(String cipherName, String password) {
        super(cipherName, password);
    }

    @Override
    public byte[] decodeBytes(ByteBuf secretByteBuf) {
        byte[] salt = new byte[32];
        secretByteBuf.readBytes(salt);

        byte[] subKey = getSubKey(getKey(), salt);

        ByteBuf byteBuf = Unpooled.buffer();
        while (secretByteBuf.isReadable()) {
            byte[] encryLength = new byte[18];
            secretByteBuf.readBytes(encryLength);

            int realLength = 0;
            try {
                realLength = HeapByteBufUtil.getShort(decodeAes256Gcm(encryLength, subKey), 0);
                System.out.println("realLength: " + realLength);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] encryPayload = new byte[realLength + 16];
            secretByteBuf.readBytes(encryPayload);
            try {
                byte[] result = decodeAes256Gcm(encryPayload, subKey);
                byteBuf.writeBytes(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteBuf.array();
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
    public int getKeyLength() {
        return 32;
    }

    private byte[] decodeAes256Gcm(byte[] encryBytes, byte[] keyBytes) throws InvalidCipherTextException {
        if (decodeGcmcopher == null) {
            decodeGcmcopher = new GCMBlockCipher(new AESEngine());
        }

        decodeGcmcopher.init(false, new AEADParameters(new KeyParameter(keyBytes), 16 * 8, getDecodeNonceBytes()));

        byte[] out = new byte[encryBytes.length - 16];
        int length1 = decodeGcmcopher.processBytes(encryBytes, 0, encryBytes.length, out, 0);
        decodeGcmcopher.doFinal(out, length1);
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
}
