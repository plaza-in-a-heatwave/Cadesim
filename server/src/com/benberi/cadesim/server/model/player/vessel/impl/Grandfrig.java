package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Grandfrig extends Vessel {	
	private String name = "grandfrig";
	private int    id   = -1;
	
    public Grandfrig(Player p) {
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
        return 40;
    }

    @Override
    public double getRamDamage() {
        return 2.667;
    }
    
    @Override
    public double getRockDamage() {
        return 2;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}
