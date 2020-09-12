package com.benberi.cadesim.game.entity.vessel.move;

public class VesselMoveTurn {

    /**
     * The current move index
     */
    private int index;

    /**
     * The moves in the turn
     */
    private Move[] moves = new Move[4];

    public VesselMoveTurn() {
        for(int i = 0; i < moves.length; i++) {
            moves[i] = new Move();
        }

        moves[0].setMoveType(MoveType.FORWARD);
        moves[0].addLeftCannon();
    }

    /**
     * Processes to next move
     */
    public void nextMove() {
        this.disposeMove();
        index++;
    }

    /**
     * Disposes the current move by {@link #index}
     */
    public void disposeMove() {
        moves[0] = null;
    }

    /**
     * Gets the current move from the current index {@link #index}
     * @return The current move by index, or null if {@link #index} is out of bounds.
     */
    public Move getMove() {
        if (isDone()) {
            return null;
        }
        return this.moves[index];
    }

    /**
     * Checks if finished all moves
     * @return <code>TRUE</code> If done
     */
    public boolean isDone() {
        return index > moves.length - 1;
    }

}
