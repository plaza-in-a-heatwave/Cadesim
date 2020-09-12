package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class PlaceMovePacket extends OutgoingPacket {

    private int slot;
    private int move;

    public PlaceMovePacket() {
        super(1);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setMove(int move) {
        this.move = move;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(slot);
        writeByte(move);
        setLength(getBuffer().readableBytes());
    }
}
