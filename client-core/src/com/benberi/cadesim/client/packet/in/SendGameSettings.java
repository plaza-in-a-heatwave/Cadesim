package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendGameSettings extends ClientPacketExecutor {

    public SendGameSettings(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        //set GUI settings each time settings are changed
        if(getContext().getBattleSceneMenu() != null) {
            getContext().getBattleSceneMenu().clearDisengageBehavior();
            getContext().getBattleSceneMenu().clearQuality();
    	    getContext().getBattleSceneMenu().getTurnSlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().getRoundSlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().getRespawnDelaySlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().setDisengageButton(p.readByteString(), true);
    	    getContext().getBattleSceneMenu().setQualityButton(p.readByteString(), true);
    	    getContext().getBattleSceneMenu().setCustomMapButton(p.readInt());
        }else {// if menu is null then just process reading byte info
    	    p.readByte();
    	    p.readByte();
    	    p.readByte();
    	    p.readByteString();
    	    p.readByteString();
    	    p.readByte();
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
