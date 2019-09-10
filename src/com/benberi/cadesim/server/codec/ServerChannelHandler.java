package com.benberi.cadesim.server.codec;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.packet.IncomingPacket;
import com.benberi.cadesim.server.model.player.Player;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

/**
 * Handlers the channel of the server.
 * New clients, packets, etc
 */
public class ServerChannelHandler implements ChannelInboundHandler {

    /**
     * The server context
     */
    private ServerContext context;

    public ServerChannelHandler(ServerContext ctx) {
        this.context = ctx;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
            context.getPlayerManager().registerPlayer(ctx.channel());
        } catch (Exception e) {
            ServerContext.log("Channel register error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        context.getPlayerManager().deRegisterPlayer(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        try {
            if (o instanceof Packet) {
                Packet packet = (Packet) o;
                Channel c = ctx.channel();

                Player player = context.getPlayerManager().getPlayerByChannel(c);
                if (player != null) {
                    IncomingPacket p = new IncomingPacket(c, packet);
                    player.getPackets().queueIncoming(p);
                }
            }
        } catch(Exception e) {
            ServerContext.log("Channel read error: " + e.getMessage());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        ServerContext.log("Channel exception caught: " + throwable.getMessage());
    }
}
