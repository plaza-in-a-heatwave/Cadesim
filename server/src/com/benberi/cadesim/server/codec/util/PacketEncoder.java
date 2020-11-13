package com.benberi.cadesim.server.codec.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buffer) throws Exception {

        int opcode = packet.getOpcode();
        int lengthType = packet.getPacketLengthType();
        int length = packet.getLength();

        PacketLength type = PacketLength.get(lengthType);

        buffer.writeByte(opcode);
        buffer.writeByte(lengthType);

        assert type != null;
        switch(type) {
            case BYTE:
                buffer.writeByte(length);
                break;
            case SHORT:
                buffer.writeShort(length);
                break;
            case MEDIUM:
                buffer.writeMedium(length);
                break;
        }

        buffer.writeBytes(packet.getBuffer());

        packet.getBuffer().release();
    }
}
