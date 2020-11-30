package com.benberi.cadesim.server.codec.packet.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

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

    @SuppressWarnings("unchecked")
	@Override
    public void execute(Player pl, Packet p) {
        int length = p.readInt();
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(p.readBytes(length)));
            try {
            	ServerConfiguration.getGameSettings().clear();
            	ServerConfiguration.getGameSettings().addAll((ArrayList<Object>) ois.readObject());
            	ServerConfiguration.setCustomMap(ServerConfiguration.getCustomMapSetting());
            	ServerContext.setCustomMapName(ServerConfiguration.getMapNameSetting());
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
                ois.close();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
        context.getPlayerManager().handleMessage(pl, "/propose gameSettings");
    }
}
