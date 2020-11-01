package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.player.Player;

import java.util.List;

/**
 * Sets the flags in the map
 */
public class SetFlagsPacket extends OutgoingPacket {

    private List<Flag> flags;
    private int pointsAttacker;
    private int pointsDefender;
    private List<Player> players;

    public SetFlagsPacket() {
        super(OutGoingPackets.SET_FLAGS);
    }


    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);

        // send basic flag information about all flags
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

        // send information about which players control which flags
        writeByte(players.size());
        for (Player p : players) {
            writeByteString(p.getName());
            List<Flag> flags = p.getFlags();
            if (flags != null) {
                writeByte(flags.size());
                for (Flag f : flags) {
                    writeByte(f.getX());
                    writeByte(f.getY());
                }
            }
            else {
                writeByte(0x00); // no flags
            }
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
