package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.ServerContext;

public class BlockadeTimeMachine {

    /**
     * Round time and turn time
     */
    private int roundTime = ServerConfiguration.getRoundDuration();
    private int turnTime = ServerConfiguration.getTurnDuration();

    /*
     * Break duration and break interval
     */
    private int breakTime      = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
    private int timeUntilBreak = ServerConfiguration.getBreak()[1] * 10; // to deciseconds

    /*
     * Are we currently breaking
     */
    private boolean inBreak = false;
    private boolean breakPending = false;

    /**
     * Has time machine ticked yet
     */
    private boolean firstRunThisGame = true;

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

    private boolean lock;

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
     * The main tick of blockade time machine
     */
    public void tick() {
        firstRunThisGame = false;
        if (!isLock())
        {
            // if breaks enabled, count down towards the break too
            if (timeUntilBreak > 0)
            {
                // Bugfix #36 - don't start the break before the turn ends
                if (turnTime > timeUntilBreak) {
                    timeUntilBreak = turnTime;
                } else {
                    timeUntilBreak -= 1;
                }
            }
            else if (timeUntilBreak == 0)
            {
                if (breakTime > 0)
                {
                    // at the start of the break period:
                    //     schedule it
                    //     notify people
                    if (breakTime == (ServerConfiguration.getBreak()[0] * 10))
                    {
                        if (!inBreak)
                        {
                            breakPending = true;
                        }
                    }

                    if (inBreak)
                    {
                        breakTime -= 1;
                    }
                }
                else if (breakTime == 0)
                {
                    // break's over!
                    breakTime    = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
                    timeUntilBreak = ServerConfiguration.getBreak()[1] * 10; // to deciseconds
                    inBreak = false;
                    context.getPlayerManager().serverBroadcastMessage("Break's over!");
                }
            }

            if (!inBreak)
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
        }
        else // if isLock()
        {
            // if locked, we can schedule a break.
            if (breakPending)
            {
                breakPending = false;
                inBreak = true;
                context.getPlayerManager().serverBroadcastMessage("Arr, time to rest a moment. (" + getBreakTime() / 10 + "s)");
            }

            // Bugfix #36; decrement round time, timeuntilbreak, breaktime even when locked
            if (!inBreak) {
                roundTime--;
                timeUntilBreak--;
            } else {
                if (breakTime > 0) {
                    breakTime--;
                }
            }

        }

        if (
                (!testModeActive) && (turnTime <= -Constants.TURN_EXTRA_TIME) ||
                (testModeActive)  && (turnTime <= 0) // make tests run faster
        ) {
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

        if (!inBreak)
        {
            turnTime--; // Tick turn time
        }
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

        // reset breaks
        breakTime        = ServerConfiguration.getBreak()[0] * 10; // to deciseconds
        timeUntilBreak   = ServerConfiguration.getBreak()[1] * 10; // to deciseconds
        inBreak          = false;
        breakPending     = false;
        firstRunThisGame = true;
    }
}
