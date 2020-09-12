package com.benberi.cadesim.client.packet;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;

public abstract class ClientPacketExecutor {

    /**
     * The server context
     */
    private GameContext context;

    protected ClientPacketExecutor(GameContext ctx) {
        this.context = ctx;
    }

    protected GameContext getContext() {
        return this.context;
    }

    public abstract void execute(Packet p);

    public abstract int getSize();
}
