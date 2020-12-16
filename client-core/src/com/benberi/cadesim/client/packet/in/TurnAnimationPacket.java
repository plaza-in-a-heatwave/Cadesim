package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.VesselFace;
import com.benberi.cadesim.game.entity.vessel.VesselMovementAnimation;
import com.benberi.cadesim.game.entity.vessel.move.MoveAnimationTurn;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;

public class TurnAnimationPacket extends ClientPacketExecutor {

	public TurnAnimationPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
    	int numberOfShips = p.readByte();

        for (int i = 0; i < numberOfShips; i++) {
            String name = p.readByteString();

            Vessel vessel = getContext().getEntities().getVesselByName(name);
            if (vessel != null) {
                for (int slot = 0; slot < 4; slot++) {
                    MoveAnimationTurn turn = vessel.getStructure().getTurn(slot);
                    turn.setTokenUsed(MoveType.forId(p.readByte()));
                    turn.setAnimation(VesselMovementAnimation.forId(p.readByte()));
                    turn.setSubAnimation(VesselMovementAnimation.forId(p.readByte()));
                    turn.setLeftShoots(p.readByte());
                    turn.setRightShoots(p.readByte());
                    turn.setSunk(p.readByte() == 1);
                    int face = p.readByte();
                    if(face != 0) {
                    	turn.setSpinCollision(true);
                    	turn.setFace(VesselFace.forId(face));
                    }else {
                    	turn.setSpinCollision(false);
                    }
                }
            }
            else {
                p.getBuffer().readerIndex(p.getBuffer().readerIndex() + 16);
            }

        }

        getContext().getBattleScreen().setTurnExecute();
    }

    @Override
    public int getSize() {
        return -1;
    }
}
