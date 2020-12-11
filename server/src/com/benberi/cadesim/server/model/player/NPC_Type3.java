package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.move.MoveType;

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
    	
    }
    
    @Override
    public void performLogic() {
    	getMoves().setMove(0, MoveType.FORWARD);
    }
    
	@Override
    public NPC_Type getType() {
		return type;
    }
}
