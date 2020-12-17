package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendDamagePacket extends ClientPacketExecutor {

    public SendDamagePacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        getContext().getControl().setDamagePercentage(p.readByte());
        getContext().getControl().setBilgePercentage(p.readByte());
    }

    @Override
    public int getSize() {
        return -1;
    }
}
