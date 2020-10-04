package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The login packet requests the server to
 * connect to the game, with the given display name.
 */
public class ClientAlivePacket extends OutgoingPacket {
    private byte counter;

    public ClientAlivePacket() {
        super(10);
    }

    public void setCounter(byte value) {
        counter = value;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(counter); // counter matching the most recent time packet we received
        setLength(getBuffer().readableBytes());
    }
}