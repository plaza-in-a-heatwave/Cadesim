package com.benberi.cadesim.server.model.player.ai;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.ai.util.NPC_Type;
import io.netty.channel.Channel;

/*
 * AI Logic - moving type but engage with player in range
 * Priority - shooting
 */
public class NPC_Type3 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type3(ServerContext ctx, Channel c) {
    	super(ctx,c);
    	context = ctx;
        super.setBot(true);
        super.set(-1, -1); // not spawned
        super.setType(NPC_Type.TYPE3);
    }
    
    @Override
    public void calculateRoute() {
    	if(getPath() != null && getPath().size() > 0) {
    		for(int slot = 0; slot < Math.min(getVessel().has3Moves() ? 3 : 4, getPath().size()); slot++) { //moves to enter
    			getMoves().setMove(slot, getPath().get(slot).move);
    		}	
    	}
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
