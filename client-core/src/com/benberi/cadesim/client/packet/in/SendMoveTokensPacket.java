package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendMoveTokensPacket extends ClientPacketExecutor {

    public SendMoveTokensPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int left = p.readByte();
        int forward = p.readByte();
        int right = p.readByte();
        int leftNew = p.readByte();
        int forwardNew = p.readByte();
        int rightNew = p.readByte();
        int cannons = p.readByte();

        getContext().getControl().setMoves(left, forward, right);
        getContext().getControl().updateMoveHistoryWithNewMoves(leftNew, forwardNew, rightNew);
        getContext().getControl().setLoadedCannonballs(cannons);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
