package com.benberi.cadesim.game.scene.impl.control.hand;

import com.benberi.cadesim.game.entity.vessel.move.MoveType;

/**
 * Created by User on 24/08/2017.
 */
public interface HandMove {
    boolean[] getLeft();
    boolean[] getRight();
    void resetLeft();
    void resetRight();
    void addLeft();
    void addRight();

    MoveType getMove();
    void setMove(MoveType move);

    void setMoveTemporary(boolean temp);
    boolean isMoveTemp();
}
