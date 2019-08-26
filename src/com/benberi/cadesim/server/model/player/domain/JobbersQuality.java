package com.benberi.cadesim.server.model.player.domain;

public enum JobbersQuality {
	BASIC(
		0.00333, // fixRate              per sec
		0.02666, // bilgeFixRate         per sec
		40,      // minBilgeForDamage
		0.01133, // moves                per sec
		0.07666, // cannons              per sec
		0.00100  // full bilge move rate per sec
    ),
	ELITE(
		0.00666, // "
		0.04666, // "
		50,      // "
		0.01600, // "
		1.01333, // "
		0.00200  // "
	);

    /**
     *  The fix amount per sec
     */
    private double fixRate;

    /**
     * The fix bilge amount per sec
     */
    private double bilgeFixRate;

    /**
     * Minimum damage for bilge to increase
     */
    private double minBilgeForDamamge;

    /**
     * Move generation per sec
     */
    private double movesRate;

    /**
     * Cannon generation per sec
     */
    private double cannonsRate;

    private double fullBilgeMoveRate;

    JobbersQuality(double fixRate, double bilgeFixRate, double minBilgeForDamage,  double movesRate, double cannonsRate, double fullBilgeMoveRate) {
        this.fixRate = fixRate;
        this.bilgeFixRate = bilgeFixRate;
        this.minBilgeForDamamge = minBilgeForDamage;
        this.movesRate = movesRate;
        this.cannonsRate = cannonsRate;
        this.fullBilgeMoveRate = fullBilgeMoveRate;
    }

    public double getFixRate() {
        return fixRate;
    }

    public double getCannonsPerSec() {
        return this.cannonsRate;
    }

    public double getBilgeFixRate() {
        return bilgeFixRate;
    }

    public double getMinDamageForBilge() {
        return minBilgeForDamamge;
    }

    public double getFullBilgeMoveRate() {
        return fullBilgeMoveRate;
    }

    public double getMovesPerSec() {
        return movesRate;
    }
}
