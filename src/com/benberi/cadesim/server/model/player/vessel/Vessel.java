package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.impl.*;
import com.benberi.cadesim.server.model.cade.Team;

import java.util.HashMap;

/**
 * Abstraction of vessel
 */
public abstract class Vessel {
	/**
     * Creates a vessel by given vessel type
     * @param type  The vessel type
     * @return The created vessel, or null if can't do it
     */
    public static Vessel createVesselByType(Player p, int type) {
        switch (type) {
            case 0:
            	return new Smsloop(p);
            case 1:
            	return new Lgsloop(p);
            case 2:
            	return new Dhow(p);
            case 3:
            	return new Fanchuan(p);
            case 4:
            	return new Longship(p);
            case 5:
            	return new Junk(p);
            case 6:
            	return new Baghlah(p);
            case 7:
            	return new Merchbrig(p);
            case 8:
            	return new Warbrig(p);
            case 9:
            	return new Xebec(p);
            case 10:
            	return new Merchgal(p);
            case 11:
            	return new Warfrig(p);
            case 12:
            	return new Wargal(p);
            case 13:
            	return new Grandfrig(p);
            case 14:
            	return new Blackship(p);
            default:
            	return null;
        }
    }

	/**
	 * map integer ids to human readable ship ids
	 */
	public static final HashMap<Integer, String> VESSEL_IDS = new HashMap<Integer, String>() {{
		put(0, "smsloop");
		put(1, "lgsloop");
		put(2, "dhow");
		put(3, "fanchuan");
		put(4, "longship");
		put(5, "junk");
		put(6, "baghlah");
		put(7, "merchbrig");
		put(8, "warbrig");
		put(9, "xebec");
		put(10, "merchgal");
		put(11, "warfrig");
		put(12, "wargal");
		put(13, "grandfrig");
		put(14, "blackship");
	}};
	
	/**
	 * @param name the name to search
	 * @return id, or -1 if not found
	 */
	protected static int getIdFromName(String name) {
        for (int i : VESSEL_IDS.keySet())
        {
        	if (VESSEL_IDS.get(i).equals(name))
        	{
        		return i;
        	}
        }
        return -1;
	}	
	
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

    public double getDamagePercentage() {
        return (damage * 100.0) / getMaxDamage();
    }

    /** returns a percentage 0<=n<=100. **/
    public double getBilgePercentage() {
        return bilge;
    }

    /** returns a fraction 0<=n<=1. **/
    public double getBilgeFraction() {
        return bilge / 100.0;
    }
    
    /**
     * helper method to convert percent maxed into damage
     */
    private double getDamageFromPercentage(double percentage)
    {
    	return (percentage * getMaxDamage()) / 100.0;
    }

    public void decreaseDamage(double rate) {
    	// apply rate decrease to the percentage, not the value
        damage = getDamageFromPercentage(getDamagePercentage() - rate);

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
    	if(damage >= getMaxDamage()) {
    		return true;
    	}
    	return false;
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
    public abstract boolean has3Moves();

    /**
     * @return  The maximum damage. (med CB)
     */
    public abstract double getMaxDamage();

    /**
     * @return The damage dealt when ramming a ship. (med CB)
     */
    public abstract double getRamDamage();
    
    /**
     * @return The damage received when ramming a rock. (med CB)
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
}
