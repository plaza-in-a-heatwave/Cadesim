package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

import java.util.List;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class SendPlayersPacket extends OutgoingPacket {

    private List<Player> players;

    public SendPlayersPacket() {
        super(OutGoingPackets.SEND_ALL_VESSELS);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);

        writeByte(players.size());
        for(Player p : players) {
            if (!p.isRegistered()) {
                continue;
            }
            writeByteString(p.getName());
            writeByte(p.getX());
            writeByte(p.getY());
            writeByte(p.getFace().getDirectionId());
            writeByte(p.getVessel().getID());
            writeByte(p.getTeam().getID());
        }

        setLength(getBuffer().readableBytes());
    }
}
