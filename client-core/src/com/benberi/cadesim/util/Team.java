package com.benberi.cadesim.util;


import com.badlogic.gdx.graphics.Color;

public enum Team {

    DEFENDER(1, new Color(0.29803921568f, 0.77647058823f, 0.22352941176f, 1f)),
    ATTACKER(0, new Color(0.81568627451f, 0.18039215686f, 0.20392156862f, 1f));

    private int team;
    private Color color;

    Team(int id, Color color) {
        this.team = id;
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public int getID() {
        return this.team;
    }

    public static Team forId(int team) {
        if (team == -1) {
            return null;
        }
        switch (team) {
            case 1:
            default:
                return DEFENDER;
            case 0:
                return ATTACKER;
        }
    }
}