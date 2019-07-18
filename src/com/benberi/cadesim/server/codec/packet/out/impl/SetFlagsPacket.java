package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;

import java.util.List;

/**
 * Sets the flags in the map
 */
public class SetFlagsPacket extends OutgoingPacket {

    private List<Flag> flags;
    private int pointsRed;
    private int pointsGreen;

    public SetFlagsPacket() {
        super(OutGoingPackets.SET_FLAGS);
    }


    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);

        writeInt(pointsGreen);
        writeInt(pointsRed);

        writeByte(flags.size());
        for (Flag flag : flags) {
            writeByte(flag.getSize().getID());
            writeByte(flag.getController() != null ? flag.getController().getID() : -1);
            writeByte(flag.isAtWar() ? 1 : 0);
            writeInt(flag.getX());
            writeInt(flag.getY());
        }
        setLength(getBuffer().readableBytes());
    }

    public void setPointsGreen(int pointsGreen) {
        this.pointsGreen = pointsGreen;
    }

    public void setPointsRed(int pointsRed) {
        this.pointsRed = pointsRed;
    }
}
