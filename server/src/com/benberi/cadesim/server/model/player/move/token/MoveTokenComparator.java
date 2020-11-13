package com.benberi.cadesim.server.model.player.move.token;
import java.util.Comparator;

public class MoveTokenComparator implements Comparator<MoveToken> {
    @Override
    public int compare(MoveToken t1, MoveToken t2) {
        if (t1.getTurns() == t2.getTurns()) {
            return -1;
        }
        return t2.getTurns() - t1.getTurns();
    }
}
