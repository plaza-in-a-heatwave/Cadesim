package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.domain.PlayerLoginRequest;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

public class PlayerLoginPacket extends ServerPacketExecutor {

    public PlayerLoginPacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        int version = p.readByte();
        int ship = p.readByte();
        int team = p.readByte();
        String name = p.readByteString();
        String code = p.readByteString();
        
        // auth check - otherwise login packet never gets processed
        if ((code.length() <= Constants.MAX_CODE_SIZE) && code.equals(ServerConfiguration.getAuthCode()))
        {
        	getContext().getPlayerManager().queuePlayerLogin(new PlayerLoginRequest(pl, name, ship, team, version));
        	ServerContext.log("[auth] " + pl.getChannel().remoteAddress() + " provided correct code");
        }
        else
        {
        	ServerContext.log("[auth] WARNING " + pl.getChannel().remoteAddress() + " didnt provide correct code");
        }
    }
}
