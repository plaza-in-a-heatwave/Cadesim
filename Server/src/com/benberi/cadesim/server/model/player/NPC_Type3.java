package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;

/*
 * AI Logic - moving type but engage with player in range
 * Priority - shooting
 */
public class NPC_Type3 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;

    public NPC_Type3(ServerContext ctx) {
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
