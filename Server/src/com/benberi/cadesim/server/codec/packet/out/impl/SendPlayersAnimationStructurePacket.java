package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.player.move.MoveAnimationTurn;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

import java.util.List;

public class SendPlayersAnimationStructurePacket extends OutgoingPacket {

    private List<Player> players;

    public SendPlayersAnimationStructurePacket() {
        super(OutGoingPackets.TURN_VESSEL_ANIMS);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);

        // How many players
        writeByte(players.size());

        for (Player p : players) {

            // The structure
            MoveAnimationStructure structure = p.getAnimationStructure();

            // Write the name of the player as a key
            writeByteString(p.getName());

            for (int slot = 0; slot < 4; slot++) {

                // The turn
                MoveAnimationTurn turn = structure.getTurn(slot);

                // Write the data
                writeByte(turn.getMoveToken().getId());
                writeByte(turn.getAnimation().getId());
                writeByte(turn.getSubAnimation().getId());
                writeByte(turn.getLeftShoots());
                writeByte(turn.getRightShoots());
                writeByte(turn.isSunk() ? 1 : 0);
            }
        }

        setLength(getBuffer().readableBytes());
    }
}
