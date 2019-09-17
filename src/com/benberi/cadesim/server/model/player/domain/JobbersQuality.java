package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.config.Constants;

/**
 * the rates of various ship constants in Seconds.
 * compensation is applied for the service loop rate, which is
 * typically greater than 1 tick per second.
 */
public enum JobbersQuality {
	BASIC(
		0.06666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // fixRate           %  per tick
		0.26666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // bilgeFixRate      %  per tick // TODO is this too fast?
		40,                                                     // minBilgeForDamage
		0.06000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // moves                per tick
		0.25000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // cannons              per tick
		0.01000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY)  // full bilge num moves per tick
    ),
	ELITE(
		0.13333 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 4 % per turn ish (full clear in 25 turns)
		0.46666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 14% per turn ish (full clear in 7 turns )
		50,                                                     // " // TODO this value seems a little low.
		0.08983 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " //  2.7 ish
		0.35000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 10.5 ish
		0.02000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY)  // " //  0.6 ish
	);

    /**
     *  The fix amount per tick
     */
    private double fixRate;

    /**
     * The fix bilge amount per tick
     */
    private double bilgeFixRate;

    /**
     * Minimum damage for bilge to increase
     */
    private double minBilgeForDamamge;

    /**
     * Move generation per tick
     */
    private double movesRate;

    /**
     * Cannon generation per tick
     */
    private double cannonsRate;

    /**
     * Move generation on full bilge per tick
     */
    private double fullBilgeMoveRate;

    JobbersQuality(double fixRate, double bilgeFixRate, double minBilgeForDamage,  double movesRate, double cannonsRate, double fullBilgeMoveRate) {
        this.fixRate = fixRate;
        this.bilgeFixRate = bilgeFixRate;
        this.minBilgeForDamamge = minBilgeForDamage;
        this.movesRate = movesRate;
        this.cannonsRate = cannonsRate;
        this.fullBilgeMoveRate = fullBilgeMoveRate;
    }

    public double getFixRatePerTick() {
        return fixRate;
    }

    public double getCannonsPerTick() {
        return this.cannonsRate;
    }

    public double getBilgeFixPerTick() {
        return bilgeFixRate;
    }

    public double getMinDamageForBilge() {
        return minBilgeForDamamge;
    }

    public double getFullBilgeMovesPerTick() {
        return fullBilgeMoveRate;
    }

    public double getMovesPerTick() {
        return movesRate;
    }
}
