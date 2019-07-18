package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class Junk extends Vessel {

    public Junk(Player p) {
        super(p);
    }

    @Override
    public int getID() {
        return 5;
    }

    @Override
    public int getSize() {
        return 2;
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
        return false;
    }

    @Override
    public boolean isManuaver() {
        return true;
    }

    @Override
    public double getMaxDamage() {
        return 16.66;
    }

    @Override
    public double getRamDamage() {
        return 1;
    }
    
    @Override
    public double getRockDamage() {
        return 0.833;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}
