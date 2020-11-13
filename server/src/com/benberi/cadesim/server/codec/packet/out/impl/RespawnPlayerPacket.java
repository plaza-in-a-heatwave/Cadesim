package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class RespawnPlayerPacket extends OutgoingPacket {

    private String name;
    private int x;
    private int y;
    private VesselFace face;

    public RespawnPlayerPacket() {
        super(OutGoingPackets.PLAYER_SUNK);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setFace(VesselFace face) {
        this.face = face;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByteString(name);
        writeByte(x);
        writeByte(y);
        writeByte(face.getDirectionId());
        setLength(getBuffer().readableBytes());
    }
}
