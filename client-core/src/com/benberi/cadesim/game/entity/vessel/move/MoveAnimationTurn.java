package com.benberi.cadesim.game.entity.vessel.move;


import com.benberi.cadesim.game.entity.vessel.VesselFace;
import com.benberi.cadesim.game.entity.vessel.VesselMovementAnimation;

public class MoveAnimationTurn {

    private MoveType tokenUsed = MoveType.NONE;
    private VesselFace face;
    private VesselMovementAnimation animation = VesselMovementAnimation.NO_ANIMATION; // phase 1
    private VesselMovementAnimation subAnimation = VesselMovementAnimation.NO_ANIMATION; // phase 2
    private int leftShoots;
    private int rightShoots;
    private boolean sunk;
    private boolean spinCollision = false;

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

	public VesselFace getFace() {
		return face;
	}

	public void setFace(VesselFace face) {
		this.face = face;
	}
	
    public void setSpinCollision(boolean flag) {
        this.spinCollision = flag;
    }
    
    public boolean getSpinCollision() {
        return this.spinCollision;
    }
}
