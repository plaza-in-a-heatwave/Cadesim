package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.util.RandomUtils;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {

    public static boolean gameEnded = false;

    /**
     * The server context
     */
    private ServerContext context;
    
    /**
     * Keep track of how many games we've played
     */
    private int gamesCompleted = 0;

    public GameService(ServerContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            context.getPackets().queuePackets();
            context.getTimeMachine().tick();
            context.getPlayerManager().tick();
            context.getPlayerManager().queueOutgoing();

            if(context.getTimeMachine().getGameTime() == 0 && !gameEnded) {
            	gameEnded = true;
            	gamesCompleted++;
            	ServerContext.log("Ending game #" + Integer.toString(gamesCompleted) + ".");
                
                // if no players... hibernate time machines
                if (context.getPlayerManager().getPlayers().size() == 0) {
                    ServerContext.log("No players connected, hibernating to save CPU");
                    while (context.getPlayerManager().getPlayers().size() == 0) {
                        try {
                            Thread.sleep(2000);
                        } catch(InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                ServerContext.log("Starting new game #" + Integer.toString(gamesCompleted) + ".");
                // rotate a new mapname if parameter > 0
                int t = ServerConfiguration.getMapRotationPeriod();
                if (t > 0) {
	                if ((gamesCompleted % t) == 0) {
	                	ServerConfiguration.setMapName(
	                		RandomUtils.getRandomMapName(Constants.mapDirectory)
	                	);
	                	context.renewMap();
	                	
	                	ServerContext.log("Rotated map after " + Integer.toString(t) + " games, automatically chose random map: " + ServerConfiguration.getMapName());
	                }
                }
                
                // complete the game refresh
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
