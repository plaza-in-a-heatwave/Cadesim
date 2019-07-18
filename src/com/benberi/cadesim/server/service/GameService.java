package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.ServerContext;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {

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
                ServerContext.log("Game has ended");
                gameEnded = true;

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

                ServerContext.log("Starting new segment!");
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
