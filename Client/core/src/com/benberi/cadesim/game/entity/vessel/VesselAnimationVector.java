package com.benberi.cadesim.game.entity.vessel;

import com.badlogic.gdx.math.Vector2;

public class VesselAnimationVector {
    /**
     * The current step on curve
     */
    private double currentStep;

    /**
     * Animation start position
     */
    private Vector2 start;

    /**
     * Animation inbetween position (only for left/right movements)
     */
    private Vector2 inbetween;

    /**
     * Animation ending position
     */
    private Vector2 end;

    private int animationTicks;

    private int tickIndex;

    /**
     * The current animation location
     */
    private Vector2 currentAnimationLocation;

    public VesselAnimationVector(Vector2 start, Vector2 inbetween, Vector2 end, Vector2 currentAnimationLocation, Vector2 linear) {
        this.start = start;
        this.inbetween = inbetween;
        this.end = end;
        this.currentAnimationLocation = currentAnimationLocation;
    }

    public Vector2 getCurrentAnimationLocation() {
        return this.currentAnimationLocation;
    }

    public double getCurrentStep() {
        return this.currentStep;
    }

    public void addStep(double step) {
        this.currentStep += step;
    }

    public void resetAnimationTicks() {
        this.animationTicks = 0;
    }

    public int getAnimationTicks() {
        return this.animationTicks;
    }

    public void tickAnimationTicks(float amm) {
        animationTicks += amm;
    }

    public int getTickIndex() {
        return this.tickIndex;
    }

    public void setTickIndex(int idx) {
        this.tickIndex = idx;
    }

    /**
     * Gets the starting point of the move
     * @return {@link #start}
     */
    public Vector2 getStartPoint() {
        return this.start;
    }

    /**
     * Gets the ending target point of the move
     * @return {@link #end}
     */
    public Vector2 getEndPoint() {
        return this.end;
    }

    /**
     * Gets the in-between point of the move
     * @return {@link #inbetween}
     */
    public Vector2 getInbetweenPoint() {
        return this.inbetween;
    }
}
