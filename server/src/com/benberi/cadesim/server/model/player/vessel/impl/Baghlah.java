package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Baghlah extends Vessel {	
	private String name = "baghlah";
	private int    id   = -1;
	
    public Baghlah(Player p) {
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
        return 1;
    }

    @Override
    public int getInfluenceDiameter() {
        return 4;
    }

    @Override
    public int getMaxCannons() {
        return 12;
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
        return 13.333;
    }

    @Override
    public double getRamDamage() {
        return 0.667;
    }
    
    @Override
    public double getRockDamage() {
        return 0.667;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.MEDIUM;
    }
}
