import com.zkdcloud.shadowsocks.common.cipher.AbstractCipher;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import com.zkdcloud.shadowsocks.server.config.ServerConfig;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * description
 *
 * @author zk
 * @since 2019/8/3
 */
public class SimpleHttpTest {
    public static void main(String[] args){
        byte[] originBytes = new byte[]{71,69,84,32,47,37,51,57,37,97,48,37,102,57,37,52,48,37,49,49,37,49,51,37,51,49,37,57,97,37,56,102,37,98,99,37,100,55,37,50,53,37,100,51,37,57,51,37,54,49,37,97,54,37,55,53,37,54,55,37,99,55,37,53,48,37,99,100,37,52,99,37,48,102,37,102,99,37,97,100,37,102,56,37,52,97,37,98,50,37,101,98,37,50,102,37,56,99,37,50,102,37,97,53,37,56,53,37,102,48,37,53,98,37,56,56,37,52,99,37,53,101,37,48,51,37,56,56,37,53,102,37,50,55,37,55,51,37,51,48,37,52,52,37,99,98,37,55,56,32,72,84,84,80,47,49,46,49,13,10,72,111,115,116,58,32,119,119,119,46,98,97,105,100,105,46,99,111,109,58,49,49,48,50,50,13,10,85,115,101,114,45,65,103,101,110,116,58,32,77,111,122,105,108,108,97,47,53,46,48,32,40,105,80,104,111,110,101,59,32,67,80,85,32,105,80,104,111,110,101,32,79,83,32,53,95,48,32,108,105,107,101,32,77,97,99,32,79,83,32,88,41,32,65,112,112,108,101,87,101,98,75,105,116,47,53,51,52,46,52,54,32,40,75,72,84,77,76,44,32,108,105,107,101,32,71,101,99,107,111,41,32,86,101,114,115,105,111,110,47,53,46,49,32,77,111,98,105,108,101,47,57,65,51,51,52,32,83,97,102,97,114,105,47,55,53,51,52,46,52,56,46,51,13,10,65,99,99,101,112,116,58,32,116,101,120,116,47,104,116,109,108,44,97,112,112,108,105,99,97,116,105,111,110,47,120,104,116,109,108,43,120,109,108,44,97,112,112,108,105,99,97,116,105,111,110,47,120,109,108,59,113,61,48,46,57,44,42,47,42,59,113,61,48,46,56,13,10,65,99,99,101,112,116,45,76,97,110,103,117,97,103,101,58,32,101,110,45,85,83,44,101,110,59,113,61,48,46,56,13,10,65,99,99,101,112,116,45,69,110,99,111,100,105,110,103,58,32,103,122,105,112,44,32,100,101,102,108,97,116,101,13,10,68,78,84,58,32,49,13,10,67,111,110,110,101,99,116,105,111,110,58,32,107,101,101,112,45,97,108,105,118,101,13,10,13,10,-95,-19,40,12,-5,62,-70,-108,2,7,51,-94,-55,-81,-69,22,-8,24,-6,-88,-23,9,38,-127,76,107,62,113,78,-120,-86,112,12,72,0,-33,-73,-86,116,-86,69,65,-36,-44,66,-122,-83,-59,104,72,0,-113,-44,-48,8,-108,-101,71,-126,-56,73,71,77,29,11,89,88,-9,-118,-73,64,-109,24,-106,-88,50,-87,-23,21,-34,48,-55,-40,120,83,-76,30,-55,85,-49,56,-57,56,-119,-48,5,-122,-106,60,-65,114,105,-84,-28,-106,15,64,120,99,-92,16,83,-69,38,-27,7,-77,107,-13,-21,-111,11,-68,-68,-7,-116,-3,95,109,63,-13,73,20,48,-10,81,110,42,-49,-87,-26,32,34,79,114,-88,-115,-43,-119,79,53,-53,-69,46,-88,-34,65,24,103,-102,10,-76,16,28,8,-125,-33,-1,67,119,-35,-93,35,-111,-70,115,-10,62,-128,-5,62,85,116,67,32,-1,-106,-41,-17,57,29,69,-43,11,-125,32,-113,-1,79,-110,37,64,-6,53,-38,4,-35,-128,-83,-95,-36,121,95,71,51,-97,-107,-15,105,116,38,7,80,1,11,13,-123,-79,12,-13,-14,0,66,55,-62,113,-76,93,-83,-97,73,101,-2,-68,52,18,-58,78,-25,31,36,-67,43,-56,-52,-86,25,-119,-33,-114,5,-114,-32,-13,29,33,-5,-28,-19,-9,-24,58,-79,4,-67,114,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        System.out.println(new String(ByteUtils.subArray(originBytes, 0, 505)));
        System.out.println("---分割线---");


        for (int i = 0; i < 7; i++) {
            byte[] realBytes = ByteUtils.subArray(originBytes, 507 + i, originBytes.length);
            System.out.println(new String(realBytes));

            //password 123456; method aes-128-cfb
            AbstractCipher cipher = CipherProvider.getByName("aes-128-cfb", "123456");
            byte[] afterByte = cipher.decodeBytes(realBytes);
            System.out.println("IP类型：" + afterByte[0]);
        }
    }
}
