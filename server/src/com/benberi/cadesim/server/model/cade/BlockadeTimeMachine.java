package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.ServerContext;

public class BlockadeTimeMachine {

    /**
     * Round time and turn time
     */
    private int roundTime = ServerConfiguration.getRoundSetting();
    private int turnTime = ServerConfiguration.getTurnSetting();
    

    /*
     * Break duration and break interval
     */
    private int breakTime      = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
    private int timeUntilBreak = ServerConfiguration.getBreak()[1] * 10; // to deciseconds

    /*
     * Are we currently breaking
     */
    private boolean inBreak = false;

    /**
     * For the time machine to signal to itself it's a good time to start a break
     */
    private boolean breakPending = false;

    /**
     * Has time machine ticked yet
     */
    private boolean firstRunThisGame = true;
    
    /**
     * Have we had a turn end yet
     */
    private boolean isFirstTurn = true;

    /**
     * Are we in test mode
     */
    private boolean testModeActive = ServerConfiguration.isTestMode();

    /**
     * The server context
     */
    private ServerContext context;

    public BlockadeTimeMachine(ServerContext context) {
        this.context = context;
    }

	private boolean isLastTurn = false;

	/**
	 * helper method to print whether it's a break or not
	 */
	public boolean isBreak() {
	    return inBreak;
	}

	/**
	 * helper method to get whether the time machine has ticked yet
	 */
	public boolean firstRunThisGame() {
	    return firstRunThisGame;
	}
	
	/**
	 * Helper method to get whether we're in the first turn of the new game
	 */
	public boolean getIsFirstTurn() {
	    return isFirstTurn;
	}
	
	public void setIsFirstTurn(boolean value) {
	    isFirstTurn = value;
	}

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        firstRunThisGame = false;

        if (!inBreak)
        {
            roundTime--; // Tick blockade time
            turnTime--;  // Tick turn time

            // bugfix for final turn: use turnTime instead of roundTime so players can
            // end on a full turn.
            if ((!isLastTurn) && (roundTime < turnTime) && (turnTime > 0))
            {
                isLastTurn  = true;
                roundTime = turnTime;
            }

            // handle break countdown/activation
            if (timeUntilBreak > 0) {
                // Bugfix #36 - don't start the break before the turn ends
                if (turnTime > timeUntilBreak) {
                    timeUntilBreak = turnTime;
                } else {
                    timeUntilBreak -= 1;
                }
            }
            else if (timeUntilBreak == 0) {
                // start break only if the end-of-turn has also happened
                if (breakPending) {
                    // start break
                    inBreak = true;
                    breakPending = false;
                    context.getPlayerManager().serverBroadcastMessage("Arr, time to rest a moment. (" + getBreakTime() / 10 + "s)");
                }
            } else {
                // not enabled
            }
            if(turnTime == 250 && !context.getPlayerManager().listBots().isEmpty()) {
                try {
                    context.getPlayerManager().AILogic();
                    for (Player p : context.getPlayerManager().listRegisteredPlayers()) {
                        context.getPlayerManager().sendMoveBar(p);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }        
            }

            // if turn ended
            if (
                    (!testModeActive) && (turnTime <= -Constants.TURN_EXTRA_TIME) ||
                    (testModeActive)  && (turnTime <= 0) // make tests run faster
            ) {
                renewTurn();
                try {
                    context.getPlayerManager().handleTurns();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // no breaks are allowed until the turn has ended. now we allow it.
                // this guarantees turns will always run first.
                if ((!inBreak) && (timeUntilBreak == 0)) {
                    breakPending = true;
                }
            }
        }
        else // if in break
        {
            if (breakTime > 0) {
                breakTime--;
            }
            else {
                // get out of break
                breakTime    = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
                timeUntilBreak = ServerConfiguration.getBreak()[1] * 10; // to deciseconds
                inBreak = false;
                context.getPlayerManager().serverBroadcastMessage("Break's over!");
            }
        }
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

    public int getTimeUntilBreak() {
        return timeUntilBreak;
    }

    public int getBreakTime() {
        return breakTime;
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
     * Renews the turn time
     */
    public void renewTurn() {
        turnTime = context.getPlayerManager().getTurnDuration();
        isFirstTurn = false;
    }

    public void renewRound() {
    	roundTime = context.getPlayerManager().getRoundDuration();
        isLastTurn = false;
        isFirstTurn = true;

        // reset breaks
        breakTime        = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
        timeUntilBreak   = ServerConfiguration.getBreak()[1] * 10; // to deciseconds
        inBreak          = false;
        firstRunThisGame = true;
    }
}
