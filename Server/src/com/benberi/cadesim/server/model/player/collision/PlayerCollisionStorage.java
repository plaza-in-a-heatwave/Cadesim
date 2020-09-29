package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;

public class PlayerCollisionStorage {

    /**
     * The player instance
     */
    private Player player;

    private PlayerCollisionReference[] collisions = new PlayerCollisionReference[4];

    private boolean actionMoveCollided;

    public PlayerCollisionStorage(Player player) {
        this.player = player;
    }

    private VesselMovementAnimation bumpAnimation = VesselMovementAnimation.NO_ANIMATION;

    private boolean isBumped;

    private boolean positionChanged;

    private int actionTile = -1;
    private int nextActionTile = -1;

    private boolean recursionStarter;

    public boolean isActionMoveCollided() {
        return actionMoveCollided;
    }

    public void setActionMoveCollided(boolean actionMoveCollided) {
        this.actionMoveCollided = actionMoveCollided;
    }

    /**
     * Stores a collision
     * @param turn  The turn to store at
     * @param phase The phase the collision happened at
     * @param position The position the collision happened at
     */
    public void setCollided(int turn, int phase) {
        this.collisions[turn] = new PlayerCollisionReference(player, phase);
    }

    public boolean isCollided(int turn, int phase) {
        if (this.collisions[turn] != null)
            return this.collisions[turn].getPhase() == phase;
        return false;
    }

    public boolean isOnAction() {
        return actionTile != -1;
    }

    public boolean isRecursionStarter() {
        return recursionStarter;
    }

    public void setRecursionStarter(boolean recursionStarter) {
        this.recursionStarter = recursionStarter;
    }

    public void setOnAction(int tile) {
        this.actionTile = tile;
    }
    
    public void setNextAction(int tile) {
        this.nextActionTile = tile;
    }

    public int getActionTile() {
        return  actionTile;
    }
    
    public int getNextActionTile() {
        return  nextActionTile;
    }
    /**
     * Check if the player collided at given turn
     * @param turn  The turn to check
     * @return <code>true</code> If collision happened, else false
     */
    public boolean isCollided(int turn) {
        return getCollisionReference(turn) != null;
    }

    /**
     * Gets the collision reference by turn
     * @param turn  The turn
     * @return The collision reference if found, or null if not
     */
    public PlayerCollisionReference getCollisionReference(int turn) {
        return this.collisions[turn];
    }

    /**
     * Clears the storage
     */
    public void clear() {
        this.collisions = new PlayerCollisionReference[4];
        this.bumpAnimation = VesselMovementAnimation.NO_ANIMATION;
        this.actionMoveCollided = false;
        positionChanged = false;
    }

    public VesselMovementAnimation getBumpAnimation() {
        return bumpAnimation;
    }

    public void setBumpAnimation(VesselMovementAnimation bumpAnimation) {
        this.bumpAnimation = bumpAnimation;
        if (bumpAnimation != VesselMovementAnimation.NO_ANIMATION) {
            setBumped(true);
        }
        else {
            setBumped(false);
        }
    }

    public boolean isBumped() {
       // return this.bumpAnimation != VesselMovementAnimation.NO_ANIMATION;
        return isBumped;
    }

    public boolean isPositionChanged() {
        return positionChanged;
    }

    public void setPositionChanged(boolean positionChanged) {
        this.positionChanged = positionChanged;
    }

    public void setBumped(boolean bool) {
        this.isBumped = bool;
    }
}
