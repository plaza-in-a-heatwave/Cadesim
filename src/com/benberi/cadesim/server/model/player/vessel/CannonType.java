package com.benberi.cadesim.server.model.player.vessel;

public enum CannonType {
    SMALL  (0.666667),
    MEDIUM (1.000000), // all damages in terms of medium shots
    LARGE  (1.333333);

    /**
     * The damage the cannon deals
     */
    private double damage;

    CannonType(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }
}
