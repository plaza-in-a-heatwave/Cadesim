package com.benberi.cadesim.server.packet;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;

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

    public abstract void execute(Packet p);
}
