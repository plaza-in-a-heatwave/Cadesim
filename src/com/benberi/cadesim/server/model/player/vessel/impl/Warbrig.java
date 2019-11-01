package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Warbrig extends Vessel {

	private String name = "warbrig";
	private int    id   = -1;
	
    public Warbrig(Player p) {
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
        return 6;
    }

    @Override
    public int getMaxCannons() {
        return 16;
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
        return 16.66;
    }

    @Override
    public double getRamDamage() {
        return 1.333;
    }
    
    @Override
    public double getRockDamage() {
        return 0.833;
    }
    @Override
    public CannonType getCannonType() {
        return CannonType.MEDIUM;
    }
}
