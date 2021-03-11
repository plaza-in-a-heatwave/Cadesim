package com.benberi.cadesim.util;

import java.util.ArrayList;
import com.benberi.cadesim.GameContext;

public class TeamSelection {
    
    private int currentTeam = 0;
    
    private ArrayList<String> teamList = new ArrayList<String>();
    
    public TeamSelection(GameContext context) {

        teamList.add("Attacker");
        teamList.add("Defender");

    }
    
    public int getCurrentTeamAsInt() {
    	return currentTeam;
    }
    
    public void setCurrentTeamAsInt(int value) {
    	currentTeam = value;
    }
    
    public String getCurrentTeam() {
    	return (String) teamList.get(currentTeam);
    }
    
    public String getNextTeam() {
    	currentTeam++;
    	if(currentTeam > teamList.size() - 1) {
    		currentTeam = 0;
    	}
    	return (String) teamList.get(currentTeam);
    }

    
    public String getPreviousTeam() {
    	currentTeam--;
    	if(currentTeam < 0) {
    		currentTeam = teamList.size() - 1;
    	}
    	return (String) teamList.get(currentTeam);
    }

}
