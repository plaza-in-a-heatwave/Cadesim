package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.ServerContext;
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

public class BlockadeTimeMachine {

    /**
     * The timer of the blockade
     */
    private int gameTime = Constants.BLOCKADE_TIME;

    /**
     * The current turn time
     */
    private int turnTime = Constants.TURN_TIME;

    /**
     * The server context
     */
    private ServerContext context;

    public BlockadeTimeMachine(ServerContext context) {
        this.context = context;
    }

    private boolean lock;

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        if (gameTime == 0) {
            // TODO end?
        }

        if (!isLock() && gameTime > 0)
            gameTime--; // Tick blockade time

        if (turnTime <= -Constants.TURN_EXTRA_TIME) {
            if (!lock) {
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
     * @return Checks if there is a turn delay
     */
    public boolean hasLock() {
        return lock;
    }

    /**
     * Gets the blockade time
     * @return {@link #gameTime}
     */
    public int getGameTime() {
        return gameTime / 10;
    }

    public void endGame() { gameTime = 0;}

    /**
     * Gets the current turn time
     * @return {@link #turnTime}
     */
    public int getTurnTime() {
        return turnTime / 10;
    }

    public void endTurn() { turnTime = 0; }

    /**
     * Checks if the time is locked
     * @return {@link #lock}
     */
    public boolean isLock() {
        return lock;
    }

    /**
     * Renewals the turn time
     */
    public void renewTurn() {
        turnTime = Constants.TURN_TIME;
        setLock(false);
    }

    public void renewGame() {
        gameTime = Constants.BLOCKADE_TIME;
    }
}
