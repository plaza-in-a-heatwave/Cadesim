package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * Post a message from the client's chat window to the server.
 */
public class PostMessagePacket extends ServerPacketExecutor {
	private ServerContext context;
	
    public PostMessagePacket(ServerContext ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public void execute(Player pl, Packet p) {
        // pass to message handler
        context.getPlayerManager().handleMessage(pl, p.readIntString());
    }
}
