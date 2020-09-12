package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.player.Player;

import java.util.List;

/**
 * Sets the flags above player's ship, of what he has taken
 */
public class SetPlayerFlagsPacket extends OutgoingPacket {

    private List<Player> players;

    public SetPlayerFlagsPacket() {
        super(OutGoingPackets.SET_PLAYER_FLAGS);
    }


    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);
        writeByte(players.size());
        for (Player p : players) {
            writeByteString(p.getName());
            List<Flag> flags = p.getFlags();
            writeByte(flags.size());
            for(Flag f : flags) {
                writeByte(f.getX());
                writeByte(f.getY());
            }
        }
        setLength(getBuffer().readableBytes());
    }
}
