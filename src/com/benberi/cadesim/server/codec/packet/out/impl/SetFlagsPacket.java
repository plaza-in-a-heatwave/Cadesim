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
    private int pointsAttacker;
    private int pointsDefender;

    public SetFlagsPacket() {
        super(OutGoingPackets.SET_FLAGS);
    }


    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);

        writeInt(pointsDefender);
        writeInt(pointsAttacker);

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

    public void setPointsDefender(int pointsDefender) {
        this.pointsDefender = pointsDefender;
    }

    public void setPointsAttacker(int pointsAttacker) {
        this.pointsAttacker = pointsAttacker;
    }
}
