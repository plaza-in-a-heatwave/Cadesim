package com.benberi.cadesim.game.entity.vessel.move;

public enum  MovePhase {
    // The move that happens with your move token you placed
    MOVE_TOKEN,

    // The move that happens when you go on a wind and such
    ACTION_MOVE,

    // The shoots phase
    SHOOT;

    public static MovePhase getNext(MovePhase currentPhase) {
        if (currentPhase == null) {
            return MOVE_TOKEN;
        }
        switch (currentPhase) {
            case MOVE_TOKEN:
                return ACTION_MOVE;
            case ACTION_MOVE:
                return SHOOT;
        }

        return null;
    }
}
