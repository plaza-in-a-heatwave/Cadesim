package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendTargetSealPosition extends OutgoingPacket {

    private int position;

    public SendTargetSealPosition() {
        super(OutGoingPackets.SET_SEAL_TARGET);
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(position);
        setLength(1);
    }
}
