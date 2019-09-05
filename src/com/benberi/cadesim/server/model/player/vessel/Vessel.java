package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.impl.WarBrig;
import com.benberi.cadesim.server.model.player.vessel.impl.WarFrigate;
import com.benberi.cadesim.server.model.player.vessel.impl.Xebec;
import com.benberi.cadesim.server.model.player.vessel.impl.Junk;
import com.benberi.cadesim.server.model.player.vessel.impl.WarGalleon;
import com.benberi.cadesim.server.model.cade.Team;

/**
 * Abstraction of vessel
 */
public abstract class Vessel {

    private Player player;

    /**
     * The damage of the vessel
     */
    private double damage;

    /**
     * The bilge of the vessel
     */
    private double bilge;

    public Vessel(Player p) {
        this.player = p;
    }

    /**
     * Appends damage
     *
     * @param damage        The damage amount to append
     * @param damagingTeam    The team that dealt damage to the vessel
     */
    public void appendDamage(double damage, Team damagingTeam) {
        if (player.isInSafe()) {
            return;
        }
        if (player.getTeam() == damagingTeam) {
            damage = 0.5 * damage;
        }
        this.damage += damage;
        if (this.damage > getMaxDamage()) {
            this.damage = getMaxDamage();
        }
    }

    /**
     * Appends bilge
     * @param bilge The bilge to append
     */
    public void appendBilge(double bilge) {
        this.bilge += bilge;
        if (this.bilge > 100) {
            this.bilge = 100;
        }
    }

    /**
     * Gets the bilge
     *
     * @return  The bilge
     */
    public double getBilge() {
        return this.bilge;
    }

    /**
     * Gets the damage
     *
     * @return  The damage
     */
    public double getDamage() {
        return this.damage;
    }

    public int getDamagePercentage() {
        double percentage = damage / getMaxDamage() * 100;
        return (int) percentage;
    }

    public int getBilgePercentage() {
        return (int) (bilge / 100 * 100);
    }

    public void decreaseDamage(double rate) {
        damage -= rate;
        if (damage < 0) {
            damage = 0;
        }
    }

    public void decreaseBilge(double rate) {
        bilge -= rate;
        if (bilge < 0) {
            bilge = 0;
        }
    }

    public boolean isDamageMaxed() {
        return damage >= getMaxDamage();
    }

    public void resetDamageAndBilge() {
        damage = 0;
        bilge = 0;
    }

    /**
     * @return The maximum amount of filled cannons allowed
     */
    public abstract int getMaxCannons();

    /**
     * @return If the vessel is dual-cannon shoot per turn
     */
    public abstract boolean isDualCannon();

    /**
     * @return If the vessel is 3-move only
     */
    public abstract boolean isManuaver();

    /**
     * @return  The maximum damage
     */
    public abstract double getMaxDamage();

    /**
     * @return The damage dealt when ramming a ship.
     */
    public abstract double getRamDamage();
    
    /**
     * @return The damage received when ramming a rock.
     */
    public abstract double getRockDamage();

    /**
     * @return The cannon type
     */
    public abstract CannonType getCannonType();

    /**
     * The ID of the vessel type
     */
    public abstract int getID();

    /**
     * Gets the size of the ship
     * @return  The size
     */
    public abstract int getSize();

    /**
     * Gets the influence flag diameter
     * @return  The diameter
     */
    public abstract int getInfluenceDiameter();

    /**
     * Creates a vessel by given vessel type
     * @param type  The vessel type
     * @return The created vessel
     */
    public static Vessel createVesselByType(Player p, int type) {
        switch (type) {
            default:
            case 2:
                return new WarBrig(p);
            case 3:
                return new WarFrigate(p);
            case 4:
                return new Xebec(p);
            case 5:
                return new Junk(p);
            case 6:
                return new WarGalleon(p);
        }
    }
    
    /**
     * Convert a vessel ID to a string
     */
    public static String vesselIDToString(int vesselID) {
    	switch (vesselID) {
	        default:
	        case 2:
	            return "War Brig";
	        case 3:
	            return "War Frigate";
	        case 4:
	            return "Xebec";
	        case 5:
	            return "Junk";
	        case 6:
	            return "War Galleon";
	    }
    }
    
    public String getName()
    {
    	return vesselIDToString(getID());
    }

    /**
     * Checks if the vessel type exists
     * @param type  The vessel type
     * @return
     */
    public static boolean vesselExists(int type) {
        switch (type) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
        }
        return false;
    }
}
