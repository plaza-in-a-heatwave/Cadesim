package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

public class PlayerPlaceMovePacket extends ServerPacketExecutor {

    public PlayerPlaceMovePacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        int slot = p.readByte();
        int move = p.readByte();

        pl.placeMove(slot, move);
    }
}
