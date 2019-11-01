package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Xebec extends Vessel {

	private String name = "xebec";
	private int    id   = -1;
	
    public Xebec(Player p) {
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

    @Override
    public double getMaxDamage() {
        return 23.333;
    }

    @Override
    public double getRamDamage() {
        return 1.667;
    }
    
    @Override
    public double getRockDamage() {
        return 1.167;
    }
    @Override
    public CannonType getCannonType() {
        return CannonType.MEDIUM;
    }
}
