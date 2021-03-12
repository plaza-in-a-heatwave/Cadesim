package com.benberi.cadesim.util;


import com.badlogic.gdx.graphics.Color;

public enum Team {

    DEFENDER(1, new Color(0.29803921568f, 0.77647058823f, 0.22352941176f, 1f), new Color(146 / 255f, 236 / 255f, 30 / 255f, 1), "Defender"),
    ATTACKER(0, new Color(0.81568627451f, 0.18039215686f, 0.20392156862f, 1f), new Color(100 / 255f, 182 / 255f, 232 / 255f, 1), "Attacker");

    private int team;
    private Color color;
    private Color altColor;
    private String teamAsString;

    Team(int id, Color color, Color altColor, String teamAsString) {
        this.team = id;
        this.color = color;
        this.altColor = altColor;
        this.teamAsString = teamAsString;
    }

    public Color getColor() {
        return this.color;
    }
    
    public Color getAltColor() {
        return this.altColor;
    }
    
    public String getTeamAsString() {
        return this.teamAsString;
    }

    public int getID() {
        return this.team;
    }
    
    public static Team forString(String team) {
    	if (team == "") {
    		return null;
    	}
    	switch (team) {
	        case "Defender":
	        default:
	            return DEFENDER;
	        case "Attacker":
	            return ATTACKER;
    	}
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