package com.benberi.cadesim.server.model.cade;

public enum Team {
    NEUTRAL(2),
    GREEN(1),
    RED(0);

    private int team;

    Team(int id) {
        this.team = id;
    }

    public int getID() {
        return this.team;
    }

    public static Team forId(int team) {
        switch (team) {
            case 2:
                return NEUTRAL;
            case 1:
            default:
                return GREEN;
            case 0:
                return RED;
        }
    }
}
