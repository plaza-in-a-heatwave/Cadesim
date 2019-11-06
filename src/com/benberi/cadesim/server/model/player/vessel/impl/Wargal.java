package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Wargal extends Vessel {

	private String name = "wargal";
	private int    id   = -1;
	
    public Wargal(Player p) {
        super(p);
        
        // hacky way to avoid using bidirectional map
        id = getIdFromName(name);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public int getInfluenceDiameter() {
        return 6;
    }

    @Override
    public int getMaxCannons() {
        return 24;
    }

    @Override
    public boolean isDualCannon() {
        return true;
    }

    @Override
    public boolean has3Moves() {
        return true;
    }

    // forums.puzzlepirates.com/community/mvnforum/viewthread?thread=208282
    @Override
    public double getMaxDamage() {
        return 10.333;
    }

    @Override
    public double getRamDamage() {
        return 1.125; // wild guess based on report of ~ 1.5LCB equiv
    }
    
    @Override
    public double getRockDamage() {
        return 0.8514667;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}
