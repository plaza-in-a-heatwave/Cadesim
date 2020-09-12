package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;

public class MoveSlotPlacedPacket extends ClientPacketExecutor {

    public MoveSlotPlacedPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int slot = p.readByte();
        MoveType move = MoveType.forId(p.readByte());

        getContext().getControlScene().getBnavComponent().placeMove(slot, move, false);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
