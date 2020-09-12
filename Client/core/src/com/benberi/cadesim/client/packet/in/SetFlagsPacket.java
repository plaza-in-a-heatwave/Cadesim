package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Flag;

public class SetFlagsPacket extends ClientPacketExecutor {

    public SetFlagsPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        getContext().getBattleScene().getMap().getFlags().clear();

        int defenderPoints = p.readInt();
        int attackerPoints = p.readInt();

        int size = p.readByte();

        for (int i = 0; i < size; i++) {
            int flagType = p.readByte();
            int controllerTeam = p.readByte();
            int isAtWar = p.readByte();
            int x = p.readInt();
            int y = p.readInt();

            Flag flag = new Flag(getContext(), x, y);
            flag.setSize(flagType);
            flag.setControllerTeam(Team.forId(controllerTeam));
            flag.setAtWar(isAtWar == 1);
            flag.updateTextureRegion();
            getContext().getBattleScene().getMap().getFlags().add(flag);
        }
        getContext().getBattleScene().getInformation().setPoints(defenderPoints, attackerPoints);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
