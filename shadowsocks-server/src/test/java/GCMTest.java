import com.zkdcloud.shadowsocks.common.util.ShadowsocksUtils;
import io.netty.buffer.Unpooled;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;

import java.util.UUID;

/**
 * description
 *
 * @author zk
 * @since 2019/10/14
 */
public class GCMTest {
    public static void main(String[] args) throws Exception{
//        bcrpGcm(args);
        testRealBytes();
    }

    private static void testRealBytes() throws InvalidCipherTextException {
        byte[] realBytes = new byte[]{67,79,78,78,69,67,84,32,119,119,119,46,98,97,105,100,117,46,99,111,109,58,52,52,51,32,72,84,84,80,47,49,46,49,13,10,85,115,101,114,45,65,103,101,110,116,58,32,77,111,122,105,108,108,97,47,53,46,48,32,40,87,105,110,100,111,119,115,32,78,84,32,49,48,46,48,59,32,87,105,110,54,52,59,32,120,54,52,59,32,114,118,58,55,48,46,48,41,32,71,101,99,107,111,47,50,48,49,48,48,49,48,49,32,70,105,114,101,102,111,120,47,55,48,46,48,13,10,80,114,111,120,121,45,67,111,110,110,101,99,116,105,111,110,58,32,107,101,101,112,45,97,108,105,118,101,13,10,67,111,110,110,101,99,116,105,111,110,58,32,107,101,101,112,45,97,108,105,118,101,13,10,72,111,115,116,58,32,119,119,119,46,98,97,105,100,117,46,99,111,109,58,52,52,51,13,10,13,10};
        System.out.println("realBytes len: " + realBytes.length);

        byte[] salt = new byte[32];
        System.arraycopy(realBytes,0,salt,0, 32);
        byte[] subKey = getSubKey(getKeyBytes(), salt);
        System.out.println("subKey");
        printBytes(subKey);

        byte[] encryBytesLength = new byte[18];
        System.arraycopy(realBytes,32, encryBytesLength, 0, 18);
        decodeAes256Gcm(encryBytesLength, subKey);
    }

    private static void decodeAes256Gcm(byte[] encryBytes, byte[] keyBytes) throws InvalidCipherTextException {
        AESEngine aesEngine = new AESEngine();
        GCMBlockCipher gcmBlockCipher = new GCMBlockCipher(aesEngine);
        AEADParameters aeadParameters = new AEADParameters(new KeyParameter(keyBytes), 128, getNonceBytes());
        gcmBlockCipher.init(false, aeadParameters);

        byte[] out = new byte[encryBytes.length - 16];
        gcmBlockCipher.processBytes(encryBytes,0,encryBytes.length, out, encryBytes.length - 16);
        gcmBlockCipher.doFinal(out,0);
        System.out.println(new java.lang.String(out));
    }

    private static void bcrpGcm(String[] args) throws InvalidCipherTextException {
        AESEngine aesEngine = new AESEngine();
        GCMBlockCipher gcmBlockCipher = new GCMBlockCipher(aesEngine);

        byte[] realBytes = "he".getBytes();
        printBytes(realBytes);
        byte[] nonceBytes = getNonceBytes();
        byte[] keyBytes = getKeyBytes();
        byte[] outBytes = new byte[realBytes.length + 16];

        System.out.println("realBytes length :" + realBytes.length );
        AEADParameters aeadParameters = new AEADParameters(new KeyParameter(keyBytes), 128, nonceBytes);
        gcmBlockCipher.init(true, aeadParameters);
        int len = gcmBlockCipher.processBytes(realBytes,0, realBytes.length,outBytes,0);
        System.out.println(len);
        gcmBlockCipher.doFinal(outBytes, 0);

        printBytes(outBytes);
        printBytes(gcmBlockCipher.getMac());

        System.out.println("decode");
        decodeAes256Gcm(outBytes, keyBytes);
    }

    private static byte[] getNonceBytes() {
        byte[] result = new byte[12];
        return result;
    }

    private static byte[] getKeyBytes(){
        return ShadowsocksUtils.getShadowsocksKey("123456", 32);
    }

    private static byte[] getSubKey(byte[] key, byte[] salt){
        byte[] result = new byte[32];
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA1Digest());
        HKDFParameters hkdfParameters = new HKDFParameters(key, salt, "ss-subkey".getBytes());
        hkdfBytesGenerator.init(hkdfParameters);
        hkdfBytesGenerator.generateBytes(result,0, 32);
        return result;
    }

    private static void printBytes(byte[] bytes){
        for (int i = 0; i < bytes.length; i++) {
            if(i != bytes.length - 1){
                System.out.print(bytes[i] + ",");
            } else {
                System.out.println(bytes[i]);
            }
        }
    }
}
