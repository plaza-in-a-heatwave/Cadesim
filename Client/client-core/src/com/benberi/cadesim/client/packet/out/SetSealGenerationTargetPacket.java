package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class SetSealGenerationTargetPacket extends OutgoingPacket {

    private int targetMove;

    public SetSealGenerationTargetPacket() {
        super(5);
    }

    public void setTargetMove(int targetMove) {
        this.targetMove = targetMove;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(targetMove);
        setLength(1);
    }
}
