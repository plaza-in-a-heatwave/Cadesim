package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;

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

    // bot options
    private int[] startPosition;
    private VesselFace startFace;
    private float startDamage;

    // login request - non bots
    public PlayerLoginRequest(
            Player player,
            String name,
            int ship,
            int team,
            int version
    ) {
        this.player = player;
        this.name = name;
        this.ship = ship;
        this.team = team;
        this.version = version;
    }

    public PlayerLoginRequest(
            Player player,
            String name,
            int ship,
            int team,
            int version,
            int[] startPosition,
            VesselFace startFace,
            float startDamage
    ) {
        this.player = player;
        this.name = name;
        this.ship = ship;
        this.team = team;
        this.version = version;
        this.startPosition = startPosition;
        this.startFace = startFace;
        this.startDamage = startDamage;
        
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

    public int[] getStartPosition() {
        return startPosition;
    }

    public VesselFace getStartFace() {
        return startFace;
    }

    public float getStartDamage() {
        return startDamage;
    }
}
