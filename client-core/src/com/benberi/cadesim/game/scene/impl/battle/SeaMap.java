package com.benberi.cadesim.game.scene.impl.battle;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.SafeZone;

/**
 * Represents a battle arena map
 */
public class SeaMap {

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;

    /**
     * Map tiles
     */
    private GameTile[][] tiles = new GameTile[MAP_WIDTH][MAP_HEIGHT];

    public SeaMap(GameContext context, int[][] map) {
        new SafeZone(context);
    }

    public GameTile[][] getTiles() {
        return this.tiles;
    }
}
