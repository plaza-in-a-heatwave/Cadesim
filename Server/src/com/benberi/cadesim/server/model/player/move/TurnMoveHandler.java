package com.benberi.cadesim.server.model.player.move;

import com.benberi.cadesim.server.model.player.Player;

public class TurnMoveHandler {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The moves
     */
    private MoveType[] moves = new MoveType[4];

    /**
     * Left cannons
     */
    private int[] leftCannons = new int[4];

    /**
     * Right cannons
     */
    private int[] rightCannons = new int[4];

    /**
     * The manuaver slot for 3-moves ships
     */
    private int manuaverSlot = 3;

    public TurnMoveHandler(Player p)
    {
        this.player = p;
        resetTurn();
    }

    public MoveType getMove(int slot) {
        return moves[slot];
    }

    public int getLeftCannons(int slot) {
        return leftCannons[slot];
    }

    public int getRightCannons(int slot) {
        return rightCannons[slot];
    }

    public void setMove(int slot, MoveType move) {
        moves[slot] = move;
    }

    public void setLeftCannons(int slot, int amount) {
        this.leftCannons[slot] = amount;
    }

    public void setRightCannons(int slot, int amount) {
        this.rightCannons[slot] = amount;
    }

    public MoveType[] getMoves() {
        return moves;
    }

    public int[] getLeftCannons() {
        return leftCannons;
    }

    public int[] getRightCannons() {
        return rightCannons;
    }

    /**
     * Resets the turn
     */
    public void resetTurn() {
        for(int i = 0; i < moves.length; i++) {
            moves[i] = MoveType.NONE;
            leftCannons[i] = 0;
            rightCannons[i] = 0;
        }
        this.manuaverSlot = 3;
    }

    /**
     * Sets the manuaver slot
     *
     * @param manuaverSlot  The new maneuver slot
     */
    @SuppressWarnings("deprecation")
	public void setManuaverSlot(int manuaverSlot) {
        int prevSlot = this.manuaverSlot;
        this.manuaverSlot = manuaverSlot;
        MoveType move = getMove(manuaverSlot);
        if (move != MoveType.NONE) {
            setMove(prevSlot, move);
            setMove(manuaverSlot, MoveType.NONE);
            player.getPackets().sendMovePlaceVerification(manuaverSlot, MoveType.NONE.getId());
            player.getPackets().sendMovePlaceVerification(prevSlot, move.getId());
        }
    }

    /**
     * Gets the maneuver slot
     *
     * @return {@link #manuaverSlot}
     */
    public int getManuaverSlot() {
        return manuaverSlot;
    }

    public int countAllShoots() {
        int count = 0;
        for (int slot = 0; slot < leftCannons.length; slot++) {
            count += leftCannons[slot];
        }
        for (int slot = 0; slot < rightCannons.length; slot++) {
            count += rightCannons[slot];
        }

        return count;
    }
}
