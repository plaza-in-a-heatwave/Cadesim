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
		0.08983 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // moves                per tick
		2.5     / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // % guns filled        per tick
		0.01000 / (float)(1000 / Constants.SERVICE_LOOP_DELAY)  // full bilge num moves per tick
    ),
	ELITE(
		0.17     / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 5.1 % per turn ish (full clear in 19.61 turns)
		0.83333 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " // 25% per turn ish (full clear in 4 turns )
		65,                                                     // " //  actual figure from Napi Peak cade was 60. moved to 65 as players preferred.
		50,                                                     // " //  guessed
		0.11666 / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " //  3.5/turn, observed on a cit run
		3.0     / (float)(1000 / Constants.SERVICE_LOOP_DELAY), // " //  90% per 30s ish (i.e. wb 4 gunners, each fills 3 and a bit guns)
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
