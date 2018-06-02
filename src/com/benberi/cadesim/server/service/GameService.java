package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.player.PlayerManager;

import java.util.logging.Logger;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {
	
	private Logger logger = Logger.getLogger("GameService");
	
	public static boolean gameEnded = false;

    /**
     * The server context
     */
    private ServerContext context;

    public GameService(ServerContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            context.getPackets().queuePackets();
            context.getTimeMachine().tick();
            context.getPlayerManager().tick();
            context.getPlayerManager().queueOutgoing();
            long end = System.currentTimeMillis() - start;
            
			if(context.getTimeMachine().getGameTime() == 0 && !gameEnded) {
            	logger.info("Game has ended!");
            	gameEnded = true;
            	logger.info("Starting new segment");
            	context.getTimeMachine().renewGame();
            	context.getPlayerManager().renewGame();
            	gameEnded = false;
            }
						
        } catch (Exception e) {
            e.printStackTrace();
            ServerContext.log(e.getMessage());
        }
    }
}
