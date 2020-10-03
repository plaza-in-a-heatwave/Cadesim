package com.benberi.cadesim.server.model.player.move;

import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;

public class MoveAnimationStructure {

    /**
     * The turns
     */
    private MoveAnimationTurn[] turns;

    public MoveAnimationStructure() {
        this.turns = new MoveAnimationTurn[4];
        reset();
    }

    /**
     * Gets the turn by index
     * @param index The turn index
     * @return The turn animation
     */
    public MoveAnimationTurn getTurn(int index) {
        return turns[index];
    }

    public int countFilledTurnSlots() {
        int count = 0;
        for (int slot = 0; slot < turns.length; slot++) {
            MoveAnimationTurn turn = turns[slot];
            if (turn.getAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                count++;
            }
        }

        return count;
    }

    public int countFilledShootSlots() {
        int count = 0;
        for (int slot = 0; slot < turns.length; slot++) {
            MoveAnimationTurn turn = turns[slot];
            if (turn.getLeftShoots() > 0 || turn.getRightShoots() > 0) {
                count++;
            }
        }

        return count;
    }

    /**
     * Reset the animation structure.
     * 
     * This happens once the animation packet has been sent.
     */
    public void reset() {
        for (int i = 0; i < turns.length; i++) {
            turns[i] = new MoveAnimationTurn();
        }
    }
}
