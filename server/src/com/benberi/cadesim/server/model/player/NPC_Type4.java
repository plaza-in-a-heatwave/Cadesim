package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.move.MoveType;

import io.netty.channel.Channel;

/*
 * AI Logic - chaser type (goes after player)
 * Priority - shooting, flag points
 */
public class NPC_Type4 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type4(ServerContext ctx, Channel c) {
    	super(ctx,c);
    	context = ctx;
        super.setBot(true);
        super.set(-1, -1); // not spawned
        super.setType(NPC_Type.TYPE4);
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
