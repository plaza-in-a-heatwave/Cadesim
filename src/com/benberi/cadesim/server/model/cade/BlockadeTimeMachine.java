package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.ServerContext;

public class BlockadeTimeMachine {

    /**
     * The timer of the blockade
     */
    private int roundTime = ServerConfiguration.getRoundDuration();

    /**
     * The current turn time
     */
    private int turnTime = ServerConfiguration.getTurnDuration();

    /**
     * The server context
     */
    private ServerContext context;

    public BlockadeTimeMachine(ServerContext context) {
        this.context = context;
    }

    private boolean lock;

	private boolean isLastTurn = false;

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        if (!isLock())
        {
            roundTime--; // Tick blockade time
            
            // if in final turn, use turnTime instead of roundTime
            // gives players a last whole turn
            if ((!isLastTurn) && (roundTime < turnTime) && (turnTime > 0))
            {
            	isLastTurn  = true;
            	roundTime = turnTime;
            }
        }
        	

        if (turnTime <= -Constants.TURN_EXTRA_TIME) {
            if (!isLock()) {
                try {
                    context.getPlayerManager().handleTurns();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        turnTime--; // Tick turn time
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    /**
     * Gets the blockade time
     * @return {@link #roundTime}
     */
    public int getRoundTime() {
        return roundTime / 10;
    }

    private void endRound() { roundTime = 0;}

    /**
     * Gets the current turn time
     * @return {@link #turnTime}
     */
    public int getTurnTime() {
        return turnTime / 10;
    }

    private void endTurn() { turnTime = 0; }
    
    /**
     * helper method to combine endRound and endTurn
     * while persisting some of the temporary time config settings
     */
    public void stop() {
    	endRound();
    	endTurn();
    }

    /**
     * Checks if the time is locked
     * @return {@link #lock}
     */
    public boolean isLock() {
        return lock;
    }

    /**
     * Renews the turn time
     */
    public void renewTurn() {
        turnTime = context.getPlayerManager().getTurnDuration();
    }

    public void renewRound() {
    	roundTime = context.getPlayerManager().getRoundDuration();
        isLastTurn = false;
    }
}
