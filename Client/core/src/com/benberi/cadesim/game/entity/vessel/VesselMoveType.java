package com.benberi.cadesim.game.entity.vessel;

public enum VesselMoveType {
    FOUR_MOVES(40),
    THREE_MOVES(30);

    private int width;

    VesselMoveType(int width) {
        this.width = width;
    }

    public int getBarWidth() {
        return this.width;
    }
}
