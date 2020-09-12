package com.benberi.cadesim.game.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.OrientationLocation;
import com.benberi.cadesim.util.PackedObjectOrientation;

public abstract class Entity extends Sprite {

    /**
     * The sprite of the entity
     */
    private Sprite sprite;

    /**
     * The game context instance
     */
    private GameContext context;

    /**
     * The orientation pack
     */
    private PackedObjectOrientation orientationPack;

    /**
     * The orientaiton location
     */
    private OrientationLocation orientationLocation;


    public Entity(GameContext context) {
        this.context = context;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public OrientationLocation getOrientationLocation() {
        return this.orientationLocation;
    }

    protected GameContext getContext() {
        return this.context;
    }

    /**
     * Sets the packed orientation manager
     * @param o The orientation pack
     */
    protected void setOrientationPack(PackedObjectOrientation o) {
        this.orientationPack = o;
    }

    /**
     * @return The packed orientation
     */
    protected PackedObjectOrientation getOrientationPack() {
        return this.orientationPack;
    }

    protected void setOrientationLocation(int rotation) {
        this.orientationLocation = this.orientationPack.getOrienation(rotation);
    }

    /**
     * Create the entity called by libgdx
     */
    public abstract void create();
}
