package com.benberi.cadesim.client.codec;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public class ClientChannelHandler implements ChannelInboundHandler {

    private GameContext context;

    public ClientChannelHandler(GameContext context) {
        this.context = context;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Packet) {
            try {
                context.getPacketHandler().queuePacket((Packet) o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {

    }
}
