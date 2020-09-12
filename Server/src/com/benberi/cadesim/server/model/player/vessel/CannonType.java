package com.benberi.cadesim.server.model.player.vessel;

public enum CannonType {
    SMALL  (0.666667),
    MEDIUM (1.000000),
    LARGE  (1.333334);

    /**
     * The damage the cannon deals
     *
     * all damage in terms of medium shots.
     * round down for damage stats, and up for cannonball damages to make
     * sure that ships don't avoid sinking through rounding errors.
     */
    private double damage;

    CannonType(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }
}
