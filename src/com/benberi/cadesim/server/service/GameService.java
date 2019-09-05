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
    
    /**
     * helper method to randomly rotate the map
     */
    private void randomRotateMap() {
    	ServerConfiguration.setMapName(
    		RandomUtils.getRandomMapName(Constants.mapDirectory)
    	);
    	context.renewMap();
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
            	ServerContext.log("Ending game #" + Integer.toString(gamesCompleted) + ".");
            	gamesCompleted++;
                
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
                    ServerContext.log("New player joined, waking up...");
                }

                ServerContext.log("Starting new game #" + Integer.toString(gamesCompleted) + ".");
                
                // switch map if players have demanded it, or if we're rotating maps
                if (context.getPlayerManager().shouldSwitchMap())
                {
                	randomRotateMap();
                	ServerContext.log(
                		"Players voted to switch map; rotated map to: " +
                		ServerConfiguration.getMapName()
                	);
                }
                else if (
                	(ServerConfiguration.getMapRotationPeriod() > 0) &&
                	((gamesCompleted % ServerConfiguration.getMapRotationPeriod()) == 0)
                ) {
                	randomRotateMap();
                	ServerContext.log(
                		"Rotated map after " +
                		Integer.toString(ServerConfiguration.getMapRotationPeriod()) +
                		" games, automatically chose random map: " +
                		ServerConfiguration.getMapName()
                	);
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
