import com.zkdcloud.shadowsocks.common.cipher.stream.SSStreamCipher;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.junit.Test;

/**
 * description
 *
 * @author zk
 * @since 2018/8/14
 */
public class proxyTest {
    byte[] originRight = new byte[]{3, 9, 103, 105, 116, 101, 101, 46, 99, 111, 109, 1, -69, 22, 3, 1, 2, 0, 1, 0, 1, -4, 3, 3, -123, 11, -31, 115, 64, 37, 79, -105, -57, 121, 35, 107, -89, 111, 52, 60, -17, -52, 95, 25, 88, -37, 50, 11, -70, 42, 41, 54, 91, 3, -63, -126, 32, 44, -100, -111, 64, 96, 77, -107, 61, -81, -9, -117, -123, -84, 72, 34, -110, 28, 6, 94, -119, 101, 65, -67, 67, -103, 72, -87, 66, 7, 65, -62, 8, 0, 34, 58, 58, 19, 3, 19, 1, 19, 2, -52, -87, -52, -88, -64, 43, -64, 47, -64, 44, -64, 48, -64, 19, -64, 20, 0, -100, 0, -99, 0, 47, 0, 53, 0, 10, 1, 0, 1, -111, 122, 122, 0, 0, -1, 1, 0, 1, 0, 0, 0, 0, 14, 0, 12, 0, 0, 9, 103, 105, 116, 101, 101, 46, 99, 111, 109, 0, 23, 0, 0, 0, 35, 0, -64, -88, -105, -105, 54, 64, 111, 16, -19, -128, -28, -68, 12, -90, 40, 85, -62, -32, 66, 16, -7, 108, -71, 30, -66, -3, 48, 44, 50, -118, 44, 100, 78, -102, 87, 116, 41, 49, -53, 20, -102, -6, -122, -38, -21, -34, -127, 8, 74, -96, -54, 78, 24, -126, 27, -38, -92, 112, 110, -106, 88, 37, -1, 23, -9, -79, 104, 24, -63, -82, 52, 1, -83, -31, -16, -92, -104, 111, 115, 42, -55, -125, -76, 11, -87, 19, -124, 24, -27, 94, -94, 86, -12, 70, 84, -62, -4, 75, 100, 80, 80, 40, -122, -76, 7, -26, -23, 114, 30, -56, -65, -93, -123, -119, 1, 111, -26, 3, 13, 103, 6, 48, 60, -89, 35, -58, -33, -6, 46, 34, -40, -60, -57, 102, 93, 15, 87, -86, -111, -66, -65, 11, 61, -74, -97, -27, -49, -48, -43, 100, -86, -100, 104, -127, -89, 89, 11, -125, 49, -56, -73, 107, -108, -18, -105, 78, 89, 107, -109, -30, -107, 33, -27, -85, -80, 100, -91, 28, -9, 42, 119, -70, 40, -64, 112, -3, 118, 121, -58, -92, -81, -9, 57, 0, 13, 0, 20, 0, 18, 4, 3, 8, 4, 4, 1, 5, 3, 8, 5, 5, 1, 8, 6, 6, 1, 2, 1, 0, 5, 0, 5, 1, 0, 0, 0, 0, 0, 18, 0, 0, 0, 16, 0, 14, 0, 12, 2, 104, 50, 8, 104, 116, 116, 112, 47, 49, 46, 49, 117, 80, 0, 0, 0, 11, 0, 2, 1, 0, 0, 51, 0, 43, 0, 41, -38, -38, 0, 1, 0, 0, 29, 0, 32, -83, -125, -103, -110, 61, 103, 11, 27, -28, -69, -119, 0, 16, 80, -63, -17, 63, -39, 108, -43, -59, 65, -71, 82, 28, 30, -98, -108, 117, -102, -83, 13, 0, 45, 0, 2, 1, 1, 0, 43, 0, 11, 10, 122, 122, 127, 23, 3, 3, 3, 2, 3, 1, 0, 10, 0, 10, 0, 8, -38, -38, 0, 29, 0, 23, 0, 24, 74, 74, 0, 1, 0, 0, 21, 0, 18, 0, 0, 0, 0, 0};

    @Test
    public void testGit() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new HttpRequestDecoder(), new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                super.channelRead(ctx, msg);
            }
        });
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(originRight));
    }

    @Test
    public void testEnDe() throws Exception {
        SSStreamCipher aes128CfbCipher = new SSStreamCipher("123456","aes-128-cfb");
        byte[] orign = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        System.out.println("origin : ");
        print(orign);
        byte[] secret1 = aes128CfbCipher.encodeSSBytes(orign);
        System.out.println("secret : ");
        print(secret1);
        byte[] secret2 = aes128CfbCipher.encodeSSBytes(orign);
        System.out.println("secret2 : ");
        print(secret2);

//        byte[] afterDecBytes = aes128CfbCipher.decodeSSBytes(secret1);
        System.out.println("after : ");
//        print(afterDecBytes);
//        byte[] afterDecBytes2 = aes128CfbCipher.decodeSSBytes(secret2);
        System.out.println("after2 : ");
//        print(afterDecBytes2);
    }

    public void print(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i] + ",");
        }
        System.out.println();
    }
}
