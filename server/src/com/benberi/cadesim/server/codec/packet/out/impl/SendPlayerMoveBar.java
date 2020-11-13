package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class SendPlayerMoveBar extends OutgoingPacket {

    private String playerName;
    private TurnMoveHandler moves;

    public SendPlayerMoveBar() {
        super(OutGoingPackets.MOVE_BAR_UPDATE);
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setMoves(TurnMoveHandler moves) {
        this.moves = moves;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        int numberOfMoves = 0;
        for (int slot = 0; slot < 4; slot++) {
            MoveType move = moves.getMove(slot);
            int leftShoots = moves.getLeftCannons(slot);
            int rightShoots = moves.getRightCannons(slot);

            if (move != MoveType.NONE || leftShoots > 0 || rightShoots > 0) {
                numberOfMoves++;
            }
        }

        writeByteString(playerName);
        writeByte(numberOfMoves);
        setLength(getBuffer().readableBytes());
    }
}
