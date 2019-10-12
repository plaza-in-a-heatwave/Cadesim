package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.config.Constants;

/**
 * the rates of various ship constants in Seconds.
 * compensation is applied for the service loop rate, which is
 * typically greater than 1 tick per second.
 */
public enum JobbersQuality {
	BASIC(
		0.06666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // fixRate           %  per tick // TODO observed 0.072% in Napi Peak cade
		0.41666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // bilgeFixRate      %  per tick
		50,                                                     // minBilgeForDamage
		40,                                                     // bilgeMaxReductionThreshold
		0.06000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // moves                per tick
		1.33333 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // % guns filled        per tick
		0.01000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY)  // full bilge num moves per tick
    ),
	ELITE(
		0.13333 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 4 % per turn ish (full clear in 25 turns)
		0.83333 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 25% per turn ish (full clear in 4 turns )
		60,                                                     // " //  actual figure from Napi Peak cade
		50,                                                     // " //  guessed
		0.08983 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " //  2.7 ish
		2.5     / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " //  75% per 30s ish (i.e. wb 4 gunners, each fills 3 guns)
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
     * threshold for maximum bilge reduction
     */
    private double bilgeMaxReductionThreshold;

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

    JobbersQuality(
    		double fixRate,
    		double bilgeFixRate,
    		double minBilgeForDamage,
    		double bilgeMaxReductionThreshold,
    		double movesRate,
    		double cannonsRate,
    		double fullBilgeMoveRate
    ) {
        this.fixRate = fixRate;
        this.bilgeFixRate = bilgeFixRate;
        this.minBilgeForDamamge = minBilgeForDamage;
        this.bilgeMaxReductionThreshold = bilgeMaxReductionThreshold;
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

	public double getBilgeMaxReductionThreshold() {
		return bilgeMaxReductionThreshold;
	}
}
