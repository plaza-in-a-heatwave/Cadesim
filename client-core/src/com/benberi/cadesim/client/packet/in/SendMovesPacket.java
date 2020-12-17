package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendMovesPacket extends ClientPacketExecutor {

    public SendMovesPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        byte[] moves = p.readBytes(4);
        byte[] left = p.readBytes(4);
        byte[] right = p.readBytes(4);

        getContext().getControl().setMovePlaces(moves, left, right);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
