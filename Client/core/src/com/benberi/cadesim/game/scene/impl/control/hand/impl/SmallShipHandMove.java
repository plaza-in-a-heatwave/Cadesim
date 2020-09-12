package com.benberi.cadesim.game.scene.impl.control.hand.impl;

import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.scene.impl.control.hand.HandMove;

public class SmallShipHandMove implements HandMove {

    private boolean[] left = new boolean[1];
    private boolean[] right = new boolean[1];
    private MoveType move = MoveType.NONE;

    private boolean moveTemp;

    @Override
	public void resetLeft() {
        left = new boolean[1];
    }

    @Override
	public void resetRight() {
        right = new boolean[1];
    }

    @Override
	public void addLeft() {
        if (!left[0]) {
            left[0] = true;
        }
    }

    @Override
	public void addRight() {
        if (!right[0]) {
            right[0] = true;
        }
    }

    @Override
	public void setMove(MoveType move) {
        this.move = move;
    }

    @Override
    public void setMoveTemporary(boolean temp) {
        this.moveTemp = temp;
    }

    @Override
    public boolean isMoveTemp() {
        return moveTemp;
    }

    @Override
	public MoveType getMove() {
        return this.move;
    }

    @Override
	public boolean[] getLeft() {
        return this.left;
    }

    @Override
	public boolean[] getRight() {
        return this.right;
    }
}
