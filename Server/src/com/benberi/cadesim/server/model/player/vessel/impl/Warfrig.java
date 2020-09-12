package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Warfrig extends Vessel {

	private String name = "warfrig";
	private int    id   = -1;
	
    public Warfrig(Player p) {
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
        return 8;
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

    @Override
    public double getMaxDamage() {
        return 33.333;
    }

    @Override
    public double getRamDamage() {
        return 2;
    }
    
    @Override
    public double getRockDamage() {
        return 1.667;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}
