package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.util.RandomUtils;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {
    /**
     * The server context
     */
    private ServerContext context;
    private PlayerManager playerManager; // this called so often, should cache
    
    /**
     * Keep track of how many games we've played
     */
    private int gamesCompleted = 0;

    public GameService(ServerContext context) {
        this.context = context;
        this.playerManager = context.getPlayerManager();
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
            playerManager.tick();
            playerManager.queueOutgoing();
            context.getTimeMachine().tick();

            if(playerManager.isGameEnded()) {
                if (ServerConfiguration.getRunContinuousMode())
                {
                	// print out the scores for the game
                	playerManager.serverBroadcastMessage(
                			"Round ended, final scores were:\n" +
                			"    Defender:" + playerManager.getPointsDefender() + "\n" + 
                			"    Attacker:" + playerManager.getPointsAttacker()
                	);
    
                	ServerContext.log("Ending game #" + Integer.toString(gamesCompleted) + ".");
                	gamesCompleted++;
                    
                    // switch map if players have demanded it, or if we're rotating maps
                    String oldMap = ServerConfiguration.getMapName();
                    if (playerManager.shouldSwitchMap())
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
                    
                    // message if map changed
                    String newMap = ServerConfiguration.getMapName();
                    if (!newMap.contentEquals(oldMap))
                    {
                    	playerManager.serverBroadcastMessage("Changed map to " + newMap);
                    }
                    
                    // complete the game refresh
                    playerManager.renewGame();
                    context.getTimeMachine().renewRound(); // bugfix - order matters
    
                    playerManager.serverBroadcastMessage("Started new round: #" + (gamesCompleted + 1));
                }
                else // dont run continuous
                {
                    // get client to display endgame stats
                    System.out.println("sending game ended");
                    playerManager.sendEndGamePacket();
                    System.out.print("sent game ended");

                    // exit cleanly
                    ServerContext.log("Exited successfully after 1 game played as --run-continuous not enabled");
                    System.exit(Constants.EXIT_SUCCESS_GAMEOVER);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ServerContext.log(e.getMessage());
        }
    }
}
