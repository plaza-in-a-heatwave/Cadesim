package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class RemovePlayerShip extends ClientPacketExecutor {

    public RemovePlayerShip(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {

        String name = p.readByteString();
        Vessel vessel = getContext().getEntities().getVesselByName(name);
        if (vessel != null) {
            getContext().getEntities().remove(vessel);
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
