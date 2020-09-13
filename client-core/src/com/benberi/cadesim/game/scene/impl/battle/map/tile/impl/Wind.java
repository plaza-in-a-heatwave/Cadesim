package com.benberi.cadesim.game.scene.impl.battle.map.tile.impl;

import com.badlogic.gdx.graphics.Texture;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;

/**
 * A sea cell where ships can freely move on
 */
public class Wind extends GameTile {

    /**
     * Initializes the tile
     */
    public Wind(GameContext context, int direction) {
        super(context);
        setTexture(
        		context.getManager().get(context.getAssetObject().wind));
        setPackedObjectOrientation("cell");

        switch (direction) {
            case BlockadeMap.WIND_NORTH:
                setOrientation(3);
                break;
            case BlockadeMap.WIND_SOUTH:
                setOrientation(1);
                break;
            case BlockadeMap.WIND_WEST:
                setOrientation(2);
                break;
            case BlockadeMap.WIND_EAST:
                setOrientation(0);
                break;
        }
    }
}
