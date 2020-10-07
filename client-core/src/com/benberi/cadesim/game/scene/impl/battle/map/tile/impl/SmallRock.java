package com.benberi.cadesim.game.scene.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;
import com.benberi.cadesim.util.RandomUtils;

/**
 * A sea cell where ships can freely move on
 */
public class SmallRock extends GameObject {


    /**
     * Initializes the tile
     */
    public SmallRock(GameContext context, int x, int y) {
        super(context);
        set(x, y);
        setTexture(
        		context.getManager().get(context.getAssetObject().smallrock));
        setPackedObjectOrientation("small_rock");
        setOrientation(RandomUtils.randInt(0, 3));
    }

    /**
     * Initializes the tile
     */
    public SmallRock(GameContext context, boolean disabled) {
        super(context);
        if(!disabled) {
            setTexture(
            		context.getManager().get(context.getAssetObject().smallrock));
        }else {
            setTexture(
            		context.getManager().get(context.getAssetObject().smallrock_disabled));
        }
        setPackedObjectOrientation("small_rock");
        setOrientation(1);
    }

}
