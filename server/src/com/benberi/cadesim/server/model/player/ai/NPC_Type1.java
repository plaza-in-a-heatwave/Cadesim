package com.benberi.cadesim.server.model.player.ai;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.ai.util.NPC_Type;
import io.netty.channel.Channel;

/*
 * AI Logic - moving type only (like green ships in flotilla with no cbs)
 * Priority - flag points
 */
public class NPC_Type1 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type1(ServerContext ctx, Channel c) {
    	super(ctx,c);
    	context = ctx;
        super.setBot(true);
        super.set(-1, -1); // not spawned
        super.setType(NPC_Type.TYPE1);
    }
    
    @Override
    public void calculateRoute() {
//    	if(getPath() != null) {
//    		if(getVessel().has3Moves()) {
//
//        		for(int slot = 0; slot < 4; slot++) { //moves to enter
//        			if(slot == 0) {
//        				continue;
//        			}
//        			if(slot < getPath().size()) {
//        				getMoves().setMove(slot, getPath().get(slot-1).move);
//        			}
//        		}	
//    		}else {
//        		for(int slot = 0; slot < 4; slot++) { //moves to enter
//        			if(slot < getPath().size()) {
//        				getMoves().setMove(slot, getPath().get(slot).move);
//        			}
//        		}	
//    		}
//    	}
    }
    
    @Override
    public void performLogic() {
    	calculateRoute();
    }
    
	@Override
    public NPC_Type getType() {
		return type;
    }
}
