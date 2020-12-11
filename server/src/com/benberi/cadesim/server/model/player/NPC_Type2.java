package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.move.MoveType;

import io.netty.channel.Channel;

/*
 * AI Logic - flag grabbing type (collect available flag points)
 * Priority - flag points, shooting
 */
public class NPC_Type2 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type2(ServerContext ctx, Channel c) {
    	super(ctx,c);
    	context = ctx;
        super.setBot(true);
        super.set(-1, -1); // not spawned
        super.setType(NPC_Type.TYPE2);
    }
    
    @Override
    public void performLogic() {
    	this.getMoves().setMove(0, MoveType.FORWARD);
    }
    
	@Override
    public NPC_Type getType() {
		return type;
    }
}
