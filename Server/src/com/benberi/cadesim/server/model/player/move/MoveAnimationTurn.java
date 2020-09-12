package com.benberi.cadesim.server.model.player.move;

import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;

public class MoveAnimationTurn {

    private VesselMovementAnimation animation = VesselMovementAnimation.NO_ANIMATION; // phase 1
    private VesselMovementAnimation subAnimation = VesselMovementAnimation.NO_ANIMATION; // phase 2
    private int leftShoots;
    private int rightShoots;
    private boolean sunk;
    private MoveType moveToken;

    public VesselMovementAnimation getAnimation() {
        return animation;
    }

    public void setMoveToken(MoveType moveToken) {
        this.moveToken = moveToken;
    }

    public void setAnimation(VesselMovementAnimation animation) {
        this.animation = animation;
    }

    public VesselMovementAnimation getSubAnimation() {
        return subAnimation;
    }

    public void setSubAnimation(VesselMovementAnimation subAnimation) {
        this.subAnimation = subAnimation;
    }

    public int getLeftShoots() {
        return leftShoots;
    }

    public void setLeftShoots(int leftShoots) {
        this.leftShoots = leftShoots;
    }

    public int getRightShoots() {
        return rightShoots;
    }

    public void setRightShoots(int rightShoots) {
        this.rightShoots = rightShoots;
    }

    public boolean isSunk() {
        return sunk;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }

    public MoveType getMoveToken() {
        return moveToken;
    }
}
