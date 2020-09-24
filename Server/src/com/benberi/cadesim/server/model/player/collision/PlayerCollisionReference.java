package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.util.Position;

public class PlayerCollisionReference {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The phase collision happened at
     */
    private int phase;
    private Position position;

    public PlayerCollisionReference(Player p, int phase, Position position) {
        this.phase = phase;
        this.position = position;
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPhase() {
        return phase;
    }
    
    public Position getPosition() {
        return position;
    }
}
