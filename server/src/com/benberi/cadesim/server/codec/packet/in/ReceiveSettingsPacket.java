package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
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
        int customMapBool = p.readInt();
        ServerConfiguration.setProposedTurnDuration(turnDuration);
        ServerConfiguration.setProposedRoundDuration(roundDuration);
        ServerConfiguration.setProposedRespawnDelay(sinkPenalty);
        ServerConfiguration.setProposedDisengageBehavior(disengageBehavior);
        ServerConfiguration.setProposedJobbersQuality(jobberQuality);
        if(customMapBool == 0) {
        	ServerConfiguration.setCustomMap(false);
        	String mapName = p.readByteString();
            ServerConfiguration.setProposedMapName(mapName);
        }else if(customMapBool == 1) {
        	String customMapName = p.readByteString();
        	ServerContext.setCustomMapName(customMapName);
        	ServerConfiguration.setCustomMap(true);
            int[][] tileMaps = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];
            try {
                for (int i = tileMaps.length - 1; i >= 0; i--) {
                    for (int j = tileMaps[i].length - 1; j >= 0; j--) {
                        tileMaps[i][j] = p.readInt();
                     }
                }
        		ServerContext.setMapArray(tileMaps);
            }catch(ArrayIndexOutOfBoundsException e) {
            	ServerConfiguration.setCustomMap(false);
            }
        }
        context.getPlayerManager().handleMessage(pl, "/propose gameSettings");
    }
}
