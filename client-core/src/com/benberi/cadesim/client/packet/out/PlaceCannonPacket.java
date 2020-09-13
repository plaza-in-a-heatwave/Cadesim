package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class PlaceCannonPacket extends OutgoingPacket {

    private int slot;
    private int side;

    public PlaceCannonPacket() {
        super(3);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setSide(int side) {
        this.side = side;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(slot);
        writeByte(side);
        setLength(getBuffer().readableBytes());
    }
}
