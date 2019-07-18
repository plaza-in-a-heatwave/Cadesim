package com.benberi.cadesim.server.model.player.vessel.impl;


import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.CannonType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;

public class WarFrigate extends Vessel {

    public WarFrigate(Player p) {
        super(p);
    }

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public int getSize() {
        return 3;
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
    public boolean isManuaver() {
        return true;
    }

    @Override
    public double getMaxDamage() {
        return 33.32;
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
