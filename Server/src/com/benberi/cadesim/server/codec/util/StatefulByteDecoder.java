package com.benberi.cadesim.server.codec.util;

import com.benberi.cadesim.server.ServerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class StatefulByteDecoder<T extends Enum<T>> extends ByteToMessageDecoder {

    /**
     * The decode state
     */
    private T state;

    public void setState(T state) {
        this.state = state;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        try {
            decode(ctx, buffer, state, out);
        } catch (Exception e) {
            ServerContext.log("Channel decode error: " + e.getMessage());
        }
    }


    public abstract void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, T state, List<Object> out);
}
