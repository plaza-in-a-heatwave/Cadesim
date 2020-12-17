package com.benberi.cadesim.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author Mats Svensson
 */
public class LoadingBar extends Actor {

    Animation<?> animation;
    TextureRegion reg;
    float stateTime;

    public LoadingBar(Animation<?> animation) {
        this.animation = animation;
        reg = (TextureRegion) animation.getKeyFrame(0);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        reg = (TextureRegion) animation.getKeyFrame(stateTime);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(reg, getX(), getY());
    }
}
