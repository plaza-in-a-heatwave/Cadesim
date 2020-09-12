package com.benberi.cadesim.game.entity.vessel.move;


import com.benberi.cadesim.game.entity.vessel.VesselMovementAnimation;

public class MoveAnimationTurn {

    private MoveType tokenUsed = MoveType.NONE;
    private VesselMovementAnimation animation = VesselMovementAnimation.NO_ANIMATION; // phase 1
    private VesselMovementAnimation subAnimation = VesselMovementAnimation.NO_ANIMATION; // phase 2
    private int leftShoots;
    private int rightShoots;
    private boolean sunk;

    public VesselMovementAnimation getAnimation() {
        return animation;
    }

    public void setTokenUsed(MoveType type) {
        this.tokenUsed = type;
    }

    public MoveType getTokenUsed() {
         return this.tokenUsed;
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

    public void setSunk(boolean flag) {
        this.sunk = flag;
    }

    public boolean isSunk() {
        return this.sunk;
    }

}
