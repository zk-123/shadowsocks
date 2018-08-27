import com.zkdcloud.shadowsocks.common.cipher.stream.Aes128CfbCipher;
import com.zkdcloud.shadowsocks.common.util.SocksIpUtils;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.CryptoInitInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.DecodeCipherStreamInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.ProxyInHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.socks.SocksAddressType;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * description
 *
 * @author zk
 * @since 2018/8/11
 */
public class CryptoTest {
    public static byte[] orginByte = new byte[]{123,115,21,26,-96,97,108,87,97,-66,-10,38,18,-106,-123,98,51,60,51,-70,-115,-29,46,-74,-27,-117,-110,-91,69,83,-1,-17,48,-24,76,102,-35,106,68,61,-101,98,-80,52,-51,-55,-3,98,-64,40,-77,127,-1,-44,-42,42,39,-26,57,45,-105,106,-85,-49,-63,78,98,-89,83,-30,-114,-20,55,86,-68,126,105,-53,116,113,-107,-21,-89,84,-75,-68,-22,-112,103,41,-86,-64,-107,-85,86,-9,-104,-10,-9,113,-9,43,-5,14,90,3,-125,123,-72,7,44,-22,-125,108,63,-92,-98,-71,-4,83,-40,63,125,41,-36,111,-4,94,65,-36,-44,90,122,9,37,-128,7,15,96,54,-124,-58,42,89,54,31,-67,-44,-11,-104,-31,-26,33,20,-109,-49,109,-72,-28,-91,-41,-48,121,-64,117,45,-66,85,-123,-99,17,121,-35,-12,16,80,-116,8,105,101,108,77,-123,-33,-1,98,-42,-53,42,78,-27,59,-50,64,4,-114,83,42,39,17,-42,78,-72,120,-127,-21,41,-86,-73,-91,8,93,13,-65,10,-101,122,-98,69,-94,-119,-89,116,-2,69,28,-47,-107,14,-55,39,-39,-83,-61,-62,15,73,123,124,-3,-29,-11,-125,-20,105,120,-24,38,-31,28,93,-106,-89,-37,113,-107,-85,35,-16,14,-14,83,-12,-109,104,65,21,113,25,121,-88,-11,110,13,121,49,-85,60,118,21,-12,100,-94,-107,122,-123,48,-22,45,124,-8,-103,-111,-97,80,-8,-44,16,56,86,-65,84,-105,-72,-74,-97,94,82,55,12,-72,-55,126,-66,-6,79,110,53,110,55,-2,89,-20,34,-7,-22,-46,-26,30,-111,29,71,-15,111,-16,-55,-76,-64,-44,-23,-126,-9,66,-73,55,3,38,-56,102,11,0,-70,-12,-49,22,64,101,60,42,71,43,58,54,-87,-52,125,-118,117,-49,48,-101,-102,-85,-60,0,6,126,-73,46,116,62,75,106,27,71,-91,54,109,-74,96,72,-44,37,13,-50,114,-93,106,109,33,112,-119,-30,58,-43,95,15,-70,96,-95,44,-105,-72,50,-38,61,-95,121,-19,4,-89,-64,-109,74,105,93,-77,104,52,-120,-116,-6,53,-75,24,52,-71,-66,12,-70,12,60,-71,95,-28,81,19,-128,18,39,96,-4,-90,-116,-4,88,-68,-4,18,-32,53,44,20,-90,-48,-21,-68,-107,-62,10,126,-92,42,116,-31,-114,92,38,-40,118,-127,57,-60,112,-120,-62,-9,104,-111,-25,-106,121,-75,124,80,-30,35,33,23,-15,-48,-29,-3,3,21,-4,-52,47,50,-7,89,-59,71,-114,-3,-125,15,-62,-44,-93,-125,27,32,5,-42,96,8,-116,4,36,-84,86,-63,102,78,-38,-98,126,3,113,-48,15,-114,-51,112,95,127,-68,118,1,-33,110};
    private byte[] rightByte = new byte[]{3,15,103,105,115,116,46,103,105,116,104,117,98,46,99,111,109,1,-69,22,3,1,2,0,1,0,1,-4,3,3,-8,1,-36,124,-5,-111,69,107,17,113,-65,23,122,-14,-39,-28,49,61,-3,-116,108,104,-117,70,-108,-83,91,67,-54,108,-53,63,32,-92,-111,46,-52,76,3,122,29,-65,-118,-64,-95,59,91,-116,24,-24,-13,9,-22,-86,-84,42,13,-78,102,-68,72,-107,99,-122,-70,0,34,122,122,19,3,19,1,19,2,-52,-87,-52,-88,-64,43,-64,47,-64,44,-64,48,-64,19,-64,20,0,-100,0,-99,0,47,0,53,0,10,1,0,1,-111,-22,-22,0,0,-1,1,0,1,0,0,0,0,20,0,18,0,0,15,103,105,115,116,46,103,105,116,104,117,98,46,99,111,109,0,23,0,0,0,35,0,0,0,13,0,20,0,18,4,3,8,4,4,1,5,3,8,5,5,1,8,6,6,1,2,1,0,5,0,5,1,0,0,0,0,0,18,0,0,0,16,0,14,0,12,2,104,50,8,104,116,116,112,47,49,46,49,117,80,0,0,0,11,0,2,1,0,0,51,0,43,0,41,-38,-38,0,1,0,0,29,0,32,-8,24,101,-93,-66,74,60,24,120,90,41,60,75,-53,-45,60,-73,-60,-115,67,5,64,-24,-26,-71,-11,2,-29,110,15,13,41,0,45,0,2,1,1,0,43,0,11,10,-22,-22,127,23,3,3,3,2,3,1,0,10,0,10,0,8,-38,-38,0,29,0,23,0,24,90,90,0,1,0,0,21,0,-52,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private byte[] key = null;

    @Before
    public void initKey(){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            key = messageDigest.digest("123456".getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rightDecode(){
        System.out.println(orginByte.length);
        System.out.println(rightByte.length);


        byte[] target = new byte[orginByte.length - 16];
        CipherParameters viParameter = new ParametersWithIV(new KeyParameter(key),Arrays.copyOfRange(orginByte,0,16));

        CFBBlockCipher streamCipher = new CFBBlockCipher(new AESEngine(),16 * 8);
        streamCipher.init(false,viParameter);

        byte[] data = new byte[orginByte.length - 16];
        System.arraycopy(orginByte,16,data,0,data.length);
        streamCipher.processBytes(data,0,data.length,target,0);

        System.out.println("right:");
        for (int i = 0; i < rightByte.length; i++) {
            System.out.print( rightByte[i] + ",");
        }
        System.out.println();
        System.out.println("guess:");
        for (int i = 0; i < target.length; i++) {
            System.out.print(target[i] + ",");
        }

        System.out.println();
        boolean result = true;
        for (int i = 0; i < rightByte.length; i++) {
            if(rightByte[i] != target[i]){
                result = false;
                break;
            }
        }
        System.out.println(result);


        getIp(Unpooled.buffer().writeBytes(target));
    }
    @Test
    public void testRealDecode(){
        Aes128CfbCipher aes128CfbCipher = new Aes128CfbCipher("123456",new AESEngine());
        byte[] target = aes128CfbCipher.decodeBytes(Unpooled.buffer().writeBytes(orginByte));

        System.out.println("right:");
        for (int i = 0; i < rightByte.length; i++) {
            System.out.print( rightByte[i] + ",");
        }
        System.out.println();
        System.out.println("guess:");
        for (int i = 0; i < target.length; i++) {
            System.out.print(target[i] + ",");
        }

        System.out.println();
        boolean result = true;
        for (int i = 0; i < rightByte.length; i++) {
            if(rightByte[i] != target[i]){
                result = false;
                break;
            }
        }
        System.out.println(result);
    }

    @Test
    public void testEmbeddedChannel(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new CryptoInitInHandler(),new DecodeCipherStreamInHandler(),new ProxyInHandler());

        ByteBuf byteBuf = Unpooled.buffer().writeBytes(orginByte);
        embeddedChannel.writeInbound(byteBuf);

    }

    private void getIp(ByteBuf msg){
        SocksAddressType addressType = SocksAddressType.valueOf(msg.readByte());
        String host = null;
        int port = 0;

        switch (addressType){
            case IPv4:{
                host = SocksIpUtils.intToIp(msg.readInt());
                port = msg.readUnsignedShort();
                break;
            }
            case DOMAIN:{
                int length = msg.readByte();
                host = msg.readBytes(length).toString(Charset.forName("ASCII"));
                port = msg.readUnsignedShort();
                break;
            }
            case IPv6:{
                host = SocksIpUtils.ipv6toStr(msg.readBytes(16).array());
                port = msg.readUnsignedShort();
                break;
            }
            case UNKNOWN:{
                System.out.println("未知类型");
                break;
            }
            default:{
                System.out.println("unknown addressType");
            }
        }
        System.out.println("host is : " + host);
        System.out.println("port is : " + port);

        while(msg.readableBytes() > 0){
            System.out.print(msg.readByte());
        }
    }
}
