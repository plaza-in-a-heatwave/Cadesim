package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SetTeamNamesPacket extends ClientPacketExecutor {

    public SetTeamNamesPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        String attacker = p.readByteString();
        String defender = p.readByteString();
        
        getContext().getBattleScreen().getInformation().setTeamNames(attacker, defender);
        
    }

    @Override
    public int getSize() {
        return 8;
    }
}
