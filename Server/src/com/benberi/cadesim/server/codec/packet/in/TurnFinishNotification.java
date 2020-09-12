package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;

public class TurnFinishNotification extends ServerPacketExecutor {

    public TurnFinishNotification(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        pl.setTurnFinished(true);
    }
}
