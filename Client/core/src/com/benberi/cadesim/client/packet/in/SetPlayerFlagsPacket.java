package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.FlagSymbol;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Flag;

public class SetPlayerFlagsPacket extends ClientPacketExecutor {

    public SetPlayerFlagsPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        getContext().getEntities().clearFlagSymbols();

        int playerSize = p.readByte();

        for (int i = 0; i < playerSize; i++) {
            String vesselName = p.readByteString();
            Vessel vessel = getContext().getEntities().getVesselByName(vesselName);
            if (vessel != null) {
                int flagsSize = p.readByte();
                for (int j = 0; j < flagsSize; j++) {
                    int x = p.readByte();
                    int y = p.readByte();

                    Flag flag = getContext().getBattleScene().getMap().getFlags().get(x, y);
                    if (flag != null && flag.getControllerTeam() != null) {
                        FlagSymbol fs = new FlagSymbol(getContext(),flag.getSize(), flag.isAtWar(), flag.getControllerTeam());
                        if (!flag.isAtWar() && (vesselName.equals(getContext().myVessel) || vessel.getTeam().getID() == getContext().myTeam.getID())) {
                            fs.setLocal(true);
                            flag.setLocal(true);
                            flag.updateTextureRegion();
                        }

                        fs.createTexture();
                        vessel.getFlags().add(fs);
                    }
                }
            }
        }

        getContext().getEntities().updatePlayerScoreAffects();
    }

    @Override
    public int getSize() {
        return -1;
    }
}
