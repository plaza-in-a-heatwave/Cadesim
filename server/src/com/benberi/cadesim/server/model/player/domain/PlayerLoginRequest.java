package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.model.player.Player;

public class PlayerLoginRequest {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The player name requested
     */
    private String name;

    /**
     * The ship ID selected
     */
    private int ship;

    /**
     * The ship team
     */
    private int team;

    /**
     * The version
     */
    private int version;
    

    public PlayerLoginRequest(Player player, String name, int ship, int team, int version) {
        this.player = player;
        this.name = name;
        this.ship = ship;
        this.team = team;
        this.version = version;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public int getShip() {
        return ship;
    }

    public int getTeam() {
        return this.team;
    }

    public int getVersion() {
        return version;
    }
    
}
