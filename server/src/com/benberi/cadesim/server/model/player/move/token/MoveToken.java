package com.benberi.cadesim.server.model.player.move.token;

import com.benberi.cadesim.server.model.player.move.MoveType;

public class MoveToken {

    /**
     * The move type token
     */
    private MoveType move;

    /**
     * Amount of turns its been alive for
     */
    private int turns;

    public MoveToken(MoveType type) {
        this.move = type;
    }

    public MoveType getMove() {
        return move;
    }

    public int getTurns() {
        return turns;
    }

    public void tickTurn() {
        this.turns++;
    }
}
