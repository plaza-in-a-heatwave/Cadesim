package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.ServerConfiguration;

public enum Team {
    NEUTRAL(2),
    DEFENDER(1),
    ATTACKER(0);

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
                return DEFENDER;
            case 0:
                return ATTACKER;
        }
    }
    
    @Override
    public String toString()
    {
    	return teamIDToString(getID());
    }
    
    public static String teamIDToString(int teamID) {
    	switch (teamID) {
	        case 2:
	            return "NEUTRAL";
	        case 1:
	        default:
	            return ServerConfiguration.getDefenderName();
	        case 0:
	            return ServerConfiguration.getAttackerName();
	    }
    }
}
