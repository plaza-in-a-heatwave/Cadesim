package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class SendPositionsPacket extends ClientPacketExecutor {

    public SendPositionsPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int size = p.readByte();
        for (int i = 0; i < size; i++) {
            String name = p.readByteString();
            int x = p.readByte();
            int y = p.readByte();
            int face = p.readByte();

            Vessel vessel = getContext().getEntities().getVesselByName(name);
            if (vessel != null) {
            	if(vessel.getName().matches(getContext().myVessel)) {
            		getContext().myVesselY = y;
            	}
                vessel.setPosition(x, y, true);      // queue if sinking
                vessel.setRotationIndex(face, true); // queue if sinking
            }
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
