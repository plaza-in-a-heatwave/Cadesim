package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * request that the ship is respawned ocean-side.
 */
public class OceansideRequestPacket extends ServerPacketExecutor {

    public OceansideRequestPacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        if (!pl.isOutOfSafe()) {
        	pl.respawnToOceanSide();
        }
    }
}
