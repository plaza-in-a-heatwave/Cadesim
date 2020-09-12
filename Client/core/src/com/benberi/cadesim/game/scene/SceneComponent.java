package com.benberi.cadesim.game.scene;

import com.benberi.cadesim.GameContext;

public abstract class SceneComponent<T extends GameScene> {

    /**
     * The game context
     */
    private GameContext context;

    /**
     * The owner scene
     */
    private T scene;

    protected SceneComponent(GameContext context, T owner) {
        this.context = context;
        this.scene = owner;
    }

    protected T getScene() {
        return this.scene;
    }

    protected GameContext getContext() {
        return this.context;
    }

    public abstract void create();
    public abstract void update();
    public abstract void render();
    public abstract void dispose();

    public abstract boolean handleClick(float x, float y, int button);
    public abstract boolean handleDrag(float screenX, float screenY, float x, float y);
    public abstract boolean handleRelease(float x, float y, int button);
    public abstract boolean handleMouseMove(float x, float y);
}
