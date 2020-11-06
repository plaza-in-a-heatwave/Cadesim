package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendMapPacket extends ClientPacketExecutor {

    public SendMapPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int[][] map = new int[20][36];
        //set GUI settings each time settings are changed
        if(getContext().getBattleSceneMenu() != null) {
            getContext().getBattleSceneMenu().clearDisengageBehavior();
            getContext().getBattleSceneMenu().clearQuality();
    	    getContext().getBattleSceneMenu().getTurnSlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().getRoundSlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().getSinkPenaltySlider().setValue((float)p.readInt());
    	    getContext().getBattleSceneMenu().setDisengageButton(p.readByteString(), true);
    	    getContext().getBattleSceneMenu().setQualityButton(p.readByteString(), true);
    	    getContext().getBattleSceneMenu().setCustomMapButton(p.readInt());
        }else {// if menu is null then just process reading byte info
    	    p.readInt();
    	    p.readInt();
    	    p.readInt();
    	    p.readByteString();
    	    p.readByteString();
    	    p.readInt();
        }
	    
        getContext().currentMapName  = p.readByteString().replace(".txt", ""); 
        while(p.getBuffer().readableBytes() >= 3) {
            int tile = p.readByte();
            int x = p.readByte();
            int y = p.readByte();
            map[x][y] = tile;
        }
        
        getContext().setIslandId(p.readByte());
        getContext().getBattleScene().createMap(map);
        getContext().setConnected(true);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
