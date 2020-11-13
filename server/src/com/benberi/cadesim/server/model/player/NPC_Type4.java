package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;

/*
 * AI Logic - chaser type (goes after player)
 * Priority - shooting, flag points
 */
public class NPC_Type4 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type4(ServerContext ctx) {
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
