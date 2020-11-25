package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * 
 */
public class ReceiveTeamPacket extends ServerPacketExecutor {
	ServerContext context;
	
    public ReceiveTeamPacket(ServerContext ctx) {
        super(ctx);
        context = ctx;
    }

	@Override
    public void execute(Player pl, Packet p) {
    	context.getPlayerManager().setTeam(pl, p.readByte());
    }
}
