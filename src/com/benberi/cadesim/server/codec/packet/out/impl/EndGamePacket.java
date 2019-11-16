package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class EndGamePacket extends OutgoingPacket {
    private byte[] statsBytes;

    public EndGamePacket() {
        super(OutGoingPackets.END_GAME);
    }

    public void setStats(byte[] serializedStats) {
        this.statsBytes = serializedStats;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        this.writeBytes(statsBytes);
        setLength(getBuffer().readableBytes());
    }
}
