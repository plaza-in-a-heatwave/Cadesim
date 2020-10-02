package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.model.player.Player;

public class PlayerCollisionReference {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The phase collision happened at
     */
    private int phase;

    public PlayerCollisionReference(Player p, int phase) {
        this.phase = phase;
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPhase() {
        return phase;
    }
}
