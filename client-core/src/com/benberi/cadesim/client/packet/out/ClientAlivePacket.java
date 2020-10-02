package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The login packet requests the server to
 * connect to the game, with the given display name.
 */
public class ClientAlivePacket extends OutgoingPacket {
    public ClientAlivePacket() {
        super(10);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(0x00); // dummy byte to deliver
        setLength(getBuffer().readableBytes());
    }
}