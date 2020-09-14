package com.benberi.cadesim.game.scene.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.util.RandomUtils;

/**
 * A sea cell where ships can freely move on
 */
public class Cell extends GameTile {


    /**
     * Initializes the tile
     */
    public Cell(GameContext context) {
        super(context);
        setTexture(
        		context.getManager().get(context.getAssetObject().cell));
        setPackedObjectOrientation("cell");
        setOrientation(RandomUtils.randInt(0, 3));
    }
}
