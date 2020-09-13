package com.benberi.cadesim.game.entity.vessel;

import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;

public class VesselBumpVector {

    private Vector2 start;
    private Vector2 end;

    private float directionX;
    private float directionY;

    private float distance;

    private boolean playedMiddleAnimation;

    private MoveType move;

    public VesselBumpVector(Vector2 start, Vector2 end, MoveType move) {
        this.start = start;
        this.end = end;

        distance = start.dst(end);

        directionX = (end.x - start.x) / distance;
        directionY = (end.y - start.y) / distance;
        this.move = move;
    }

    public Vector2 getStart() {
        return start;
    }

    public boolean isPlayedMiddleAnimation() {
        return playedMiddleAnimation;
    }

    public void setPlayedMiddleAnimation(boolean playedMiddleAnimation) {
        this.playedMiddleAnimation = playedMiddleAnimation;
    }

    public Vector2 getEnd() {
        return end;
    }

    public float getDirectionX() {
        return directionX;
    }

    public float getDirectionY() {
        return directionY;
    }

    public float getDistance() {
        return distance;
    }

    public MoveType getMove() {
        return move;
    }
}
