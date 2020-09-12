package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class CannonSlotPlacedPacket extends ClientPacketExecutor {

    public CannonSlotPlacedPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int slot = p.readByte();
        int side = p.readByte();
        int amount = p.readByte();

        getContext().getControlScene().getBnavComponent().setCannons(side, slot, amount);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
