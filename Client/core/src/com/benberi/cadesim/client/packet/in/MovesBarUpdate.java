package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class MovesBarUpdate extends ClientPacketExecutor {

    public MovesBarUpdate(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {

        String name = p.readByteString();
        int moves = p.readByte();

        Vessel vessel = getContext().getEntities().getVesselByName(name);
        if (vessel != null) {
            vessel.setNumberOfMoves(moves);
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
