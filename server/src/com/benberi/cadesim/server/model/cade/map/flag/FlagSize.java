package com.benberi.cadesim.server.model.cade.map.flag;

import static com.benberi.cadesim.server.model.cade.map.BlockadeMap.*;

public enum FlagSize {

    FLAG_ONE(1),
    FLAG_TWO(2),
    FLAG_THREE(3);

    private int id;

    FlagSize(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static FlagSize forTile(int tile) {
        switch (tile) {
            case FLAG_1:
                return FLAG_ONE;
            case FLAG_2:
                return FLAG_TWO;
            case FLAG_3:
                return FLAG_THREE;
        }

        return FLAG_ONE;
    }
}
