package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class BlockingMoveSlotChanged extends OutgoingPacket {

    private int slot;

    public BlockingMoveSlotChanged() {
        super(2);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(slot);
        setLength(1);
    }
}
