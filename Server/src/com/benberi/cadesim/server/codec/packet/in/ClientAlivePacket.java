package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * the client indicates to the server that it is alive periodically.
 * 
 * Should be greater than Constants.PLAYER_LAG_TIMEOUT
 */
public class ClientAlivePacket extends ServerPacketExecutor {

    public ClientAlivePacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        p.readByte(); // read dummy byte
        pl.setLastAliveMilliseconds(System.currentTimeMillis());
    }
}