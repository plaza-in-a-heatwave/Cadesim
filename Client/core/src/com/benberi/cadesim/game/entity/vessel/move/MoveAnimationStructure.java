package com.benberi.cadesim.game.entity.vessel.move;

public class MoveAnimationStructure {

    /**
     * The turns
     */
    private MoveAnimationTurn[] turns = new MoveAnimationTurn[4];

    public MoveAnimationStructure() {
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

    public void reset() {
        for (int i = 0; i < turns.length; i++) {
            turns[i] = new MoveAnimationTurn();
        }
    }

    @Override
    public String toString() {
        return "[0: " + turns[0].getAnimation().getId() + ", " + turns[0].getSubAnimation().getId() + ", " + turns[0].getLeftShoots() + ", " + turns[0].getRightShoots() + "]" +
                "[1: " + turns[1].getAnimation().getId() + ", " + turns[1].getSubAnimation().getId() + ", " + turns[1].getLeftShoots() + ", " + turns[1].getRightShoots() + "]" +
                "[2: " + turns[2].getAnimation().getId() + ", " + turns[2].getSubAnimation().getId() + ", " + turns[2].getLeftShoots() + ", " + turns[2].getRightShoots() + "]" +
                "[3: " + turns[3].getAnimation().getId() + ", " + turns[3].getSubAnimation().getId() + ", " + turns[3].getLeftShoots() + ", " + turns[3].getRightShoots() + "]";
    }
}
