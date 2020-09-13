package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class SwapMovesPacket extends OutgoingPacket {

    private int slot1;
    private int slot2;

    public SwapMovesPacket() {
        super(9);
    }

    public void set(int slot1, int slot2) {
        this.slot1 = slot1;
        this.slot2 = slot2;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(slot1);
        writeByte(slot2);
        setLength(getBuffer().readableBytes());
    }
}
