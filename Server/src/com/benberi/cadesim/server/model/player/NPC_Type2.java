package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;

/*
 * AI Logic - flag grabbing type (collect available flag points)
 * Priority - flag points, shooting
 */
public class NPC_Type2 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type2(ServerContext ctx) {
        this.context = ctx;
        setBot(true);
        set(-1, -1); // not spawned
    }
    
    @Override
    public void calculateRoute() {
    	
    }
    
    @Override
    public void performLogic() {
    	
    }
}
