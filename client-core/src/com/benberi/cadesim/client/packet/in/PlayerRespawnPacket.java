package com.benberi.cadesim.client.packet.in;

import java.util.HashSet;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class PlayerRespawnPacket extends ClientPacketExecutor {

    public PlayerRespawnPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        String name = p.readByteString();
        int x = p.readByte();
        int y = p.readByte();
        int face = p.readByte();

        Vessel v = getContext().getEntities().getVesselByName(name);
        if (v != null) {
            v.setPosition(x, y, true);      // queue if sinking
            v.setRotationIndex(face, true); // queue if sinking
            if (v.getName().equals(getContext().myVessel)) {
                getContext().getControl().dispose();
                getContext().getControl().reset();
                getContext().getBattleScreen().initializePlayerCamera(v);
            }
        }
        HashSet<Object> seen=new HashSet<>();
        getContext().getEntities().vessels.removeIf(e->!seen.add(e.getName()));
    }

    @Override
    public int getSize() {
        return -1;
    }
}
