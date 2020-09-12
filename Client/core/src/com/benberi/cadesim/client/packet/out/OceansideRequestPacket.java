package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * Send a request to move oceanside
 */
public class OceansideRequestPacket extends OutgoingPacket {

    public OceansideRequestPacket() {
        super(7);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(1);
        setLength(1);
    }
}
