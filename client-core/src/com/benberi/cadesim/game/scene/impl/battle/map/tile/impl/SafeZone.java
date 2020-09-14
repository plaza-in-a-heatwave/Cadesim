package com.benberi.cadesim.game.scene.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.RandomUtils;

public class SafeZone extends Cell {

    /**
     * Initializes the tile
     *
     */
    public SafeZone(GameContext context) {
        super(context);
        setTexture(
        		context.getManager().get(context.getAssetObject().safe));
        setPackedObjectOrientation("cell_safe");
        setOrientation(RandomUtils.randInt(0, 3));
    }
}
