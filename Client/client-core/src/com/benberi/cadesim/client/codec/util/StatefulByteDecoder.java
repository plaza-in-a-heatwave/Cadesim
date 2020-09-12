package com.benberi.cadesim.client.codec.util;

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
        decode(ctx, buffer, state, out);
    }


    public abstract void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, T state, List<Object> out);
}
