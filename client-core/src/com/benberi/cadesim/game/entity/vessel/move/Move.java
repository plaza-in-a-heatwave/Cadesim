package com.benberi.cadesim.game.entity.vessel.move;

public class Move {

    /**
     * The move type
     */
    private MoveType type;

    /**
     * Left cannon ball shoots
     */
    private boolean[] left = new boolean[2];

    /**
     * Right cannon ball shots
     */
    private boolean[] right = new boolean[2];

    public Move() {
        this.type = MoveType.NONE;
    }

    public void setMoveType(MoveType type) {
        this.type = type;
    }

    public void addLeftCannon() {
        if (!left[0]) {
            left[0] = true;
        }
        else {
            left[1] = true;
        }
    }

    public void addRightannon() {
        if (!right[0]) {
            right[0] = true;
        }
        else {
            right[1] = true;
        }
    }

    public MoveType getType() {
        return type;
    }

    public boolean hasShoots() {
        return left[0] || right[0];
    }

    public boolean[] getLeft() {
        return left;
    }

    public boolean[] getRight() {
        return right;
    }
}
