package com.benberi.cadesim.server.codec.packet;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;

public abstract class ServerPacketExecutor {

    /**
     * The server context
     */
    private ServerContext context;

    protected ServerPacketExecutor(ServerContext ctx) {
        this.context = ctx;
    }

    protected ServerContext getContext() {
        return this.context;
    }

    public abstract void execute(Player pl, Packet p);
}
