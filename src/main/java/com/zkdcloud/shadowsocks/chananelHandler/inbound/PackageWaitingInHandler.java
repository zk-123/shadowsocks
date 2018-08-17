package com.zkdcloud.shadowsocks.chananelHandler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author zk
 * @since 2018/8/15
 */
public class PackageWaitingInHandler extends MessageToMessageDecoder<ByteBuf> {
    /**
     * byteBufList
     */
    private List<ByteBuf> byteBufList = new ArrayList<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byteBufList.add(msg.retain());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf nextByteBuf = ctx.alloc().heapBuffer();
        if(byteBufList != null && !byteBufList.isEmpty()){
            for (ByteBuf byteBuf : byteBufList) {
                nextByteBuf.writeBytes(byteBuf);
                ReferenceCountUtil.release(byteBuf);
            }
            byteBufList.clear();
            ctx.fireChannelRead(nextByteBuf);
        }
    }
}
