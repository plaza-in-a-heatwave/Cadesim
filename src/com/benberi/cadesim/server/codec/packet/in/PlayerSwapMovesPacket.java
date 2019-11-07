package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

public class PlayerSwapMovesPacket extends ServerPacketExecutor {

    public PlayerSwapMovesPacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        int slot1 = p.readByte();
        int slot2 = p.readByte();

        System.out.println("swapping moves:" + slot1 + ", " + slot2); // TODO DEBUG REMOVE THIS
        pl.swapMove(slot1, slot2);
    }
}
