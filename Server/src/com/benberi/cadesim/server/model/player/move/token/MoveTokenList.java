package com.benberi.cadesim.server.model.player.move.token;

import com.benberi.cadesim.server.model.player.move.MoveType;

import java.util.TreeSet;

public class MoveTokenList extends TreeSet<MoveToken> {

    private MoveType type;

    public MoveTokenList(MoveType moveType) {
        super(new MoveTokenComparator());
        this.type = moveType;
    }

    public MoveType getType() {
        return type;
    }
}
