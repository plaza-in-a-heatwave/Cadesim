package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;

/*
 * AI Logic - moving type only (like green ships in flotilla with no cbs)
 * Priority - flag points
 */
public class NPC_Type1 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type1(ServerContext ctx) {
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
