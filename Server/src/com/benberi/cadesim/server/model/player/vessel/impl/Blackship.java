package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Blackship extends Vessel {	
	private String name = "blackship";
	private int    id   = -1;
	
    public Blackship(Player p) {
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
        return 3;
    }

    @Override
    public int getInfluenceDiameter() {
        return 10;
    }

    @Override
    public int getMaxCannons() {
        return 64;
    }

    @Override
    public boolean isDualCannon() {
        return true;
    }

    @Override
    public boolean has3Moves() {
        return false;
    }

    @Override
    public double getMaxDamage() {
        return 999999999.0; // 336384000 is max @ 4x GF pew pew per sec for 1 year
    }

    @Override
    public double getRamDamage() {
        return 2.667;
    }
    
    @Override
    public double getRockDamage() {
        return 2.0;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}
