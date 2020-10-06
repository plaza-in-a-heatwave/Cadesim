package com.benberi.cadesim.game.scene.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;
import com.benberi.cadesim.util.RandomUtils;

/**
 * A sea cell where ships can freely move on
 */
public class BigRock extends GameObject {


    /**
     * Initializes the tile
     */
    public BigRock(GameContext context, int x, int y) {
        super(context);
        set(x, y);
        setTexture(
        		context.getManager().get(context.getAssetObject().bigrock));
        setPackedObjectOrientation("big_rock");
        setOrientation(RandomUtils.randInt(0, 3));
    }
    /**
     * Initializes the tile
     */
    public BigRock(GameContext context) {
        super(context);
        setTexture(
        		context.getManager().get(context.getAssetObject().bigrock));
        setPackedObjectOrientation("big_rock");
        setOrientation(RandomUtils.randInt(0, 3));
    }

}
