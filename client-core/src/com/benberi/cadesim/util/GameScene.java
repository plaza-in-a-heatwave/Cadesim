package com.benberi.cadesim.util;

/**
 * Represents a game scene view
 */
public interface GameScene {

    /**
     * Create components
     */
    void create();

    /**
     * Update logic
     */
    void update();

    /**
     * Render results
     */
    void render();

    /**
     * Dispose
     */
    void dispose();

    /**
     * Handles mouse drag input
     * @param diffX drag-x
     * @param diffY drag-y
     */
    boolean handleDrag(float screenX, float screenY, float diffX, float diffY);

    /**
     * Handles click on screen
     * @param x click-x
     * @param y click-y
     */
    boolean handleClick(float x, float y, int button);

    /**
     * Handles move on screen
     * @param x click-x
     * @param y click-y
     */
    boolean handleMouseMove(float x, float y);

    /**
     * Handles click release on screen
     * @param x click-x
     * @param y click-y
     */
    boolean handleClickRelease(float x, float y, int button);

}
