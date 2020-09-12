package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

public class AutoSealGenerationTogglePacket extends OutgoingPacket {

    private int toggle;

    public AutoSealGenerationTogglePacket() {
        super(4);
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle ? 1 : 0;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(toggle);
        setLength(1);
    }
}
