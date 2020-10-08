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
    public BigRock(GameContext context, int x, int y, boolean rotationDisabled) {
        super(context);
        set(x, y);
        setTexture(
        		context.getManager().get(context.getAssetObject().bigrock));
        setPackedObjectOrientation("big_rock");
        if(rotationDisabled) {
        	setOrientation(1);
        }else {
            setOrientation(RandomUtils.randInt(0, 3));
        }
    }
    /**
     * Initializes the tile
     */
    public BigRock(GameContext context, boolean disabled) {
        super(context);
        if(!disabled) {
            setTexture(
            		context.getManager().get(context.getAssetObject().bigrock));
        }else {
            setTexture(
            		context.getManager().get(context.getAssetObject().bigrock_disabled));
        }
        setPackedObjectOrientation("big_rock");
        setOrientation(1);
    }

}
