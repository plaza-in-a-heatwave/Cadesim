package com.benberi.cadesim.game.screen.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.screen.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.GameTile;

/**
 * A sea cell where ships can freely move on
 */
public class Whirlpool extends GameTile {

    public static final int SOUTH_EAST = 0;
    public static final int SOUTH_WEST = 1;
    public static final int NORTH_WEST = 2;
    public static final int NORTH_EAST = 3;

    /**
     * Initializes the tile
     */
    public Whirlpool(GameContext context, int direction, boolean disabled) {
        super(context);
        if(!disabled) {
	        setTexture(
	        		context.getManager().get(context.getAssetObject().whirlpool));
        }else {
        	setTexture(
	        		context.getManager().get(context.getAssetObject().whirlpool_disabled));
        }
        setPackedObjectOrientation("whirl");

        switch (direction) {
            case BlockadeMap.WP_NE:
                setOrientation(NORTH_EAST);
                break;
            case BlockadeMap.WP_NW:
                setOrientation(NORTH_WEST);
                break;
            case BlockadeMap.WP_SE:
                setOrientation(SOUTH_EAST);
                break;
            case BlockadeMap.WP_SW:
                setOrientation(SOUTH_WEST);
                break;
        }
    }
}
