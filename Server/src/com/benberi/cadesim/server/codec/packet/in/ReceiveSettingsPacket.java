package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * request that the ship is respawned ocean-side.
 */
public class ReceiveSettingsPacket extends ServerPacketExecutor {
	ServerContext context;
	
    public ReceiveSettingsPacket(ServerContext ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public void execute(Player pl, Packet p) {
        int turnDuration = p.readInt();
        int roundDuration = p.readInt();
        int sinkPenalty = p.readInt();
        String disengageBehavior = p.readByteString();
        String jobberQuality = p.readByteString();
        String mapName = p.readByteString();
    	
        ServerConfiguration.setProposedTurnDuration(turnDuration);
        ServerConfiguration.setProposedRoundDuration(roundDuration);
        ServerConfiguration.setProposedRespawnDelay(sinkPenalty);
        ServerConfiguration.setProposedDisengageBehavior(disengageBehavior);
        ServerConfiguration.setProposedJobbersQuality(jobberQuality);
        ServerConfiguration.setProposedMapName(mapName);
    }
}
