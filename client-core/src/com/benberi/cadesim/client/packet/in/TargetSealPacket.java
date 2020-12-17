package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;

public class TargetSealPacket extends ClientPacketExecutor {

    public TargetSealPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int pos = p.readByte();
        getContext().getControl().setMoveSealTarget(MoveType.forId(pos));
    }

    @Override
    public int getSize() {
        return -1;
    }
}
