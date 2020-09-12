package com.benberi.cadesim.game.entity.vessel;


import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.util.Face;

public enum VesselMovementAnimation {
    NO_ANIMATION(-1),
    TURN_LEFT(0),
    TURN_RIGHT(1),
    MOVE_FORWARD(2),

    MOVE_BACKWARD(3),
    MOVE_LEFT(4),
    MOVE_RIGHT(5),
    BUMP_PHASE_1(6),
    BUMP_PHASE_2(7),

    MOVE_NORTH(8),
    MOVE_SOUTH(9),
    MOVE_WEST(10),
    MOVE_EAST(11),

    BUMP_NORTH(12),
    BUMP_SOUTH(13),
    BUMP_WEST(14),
    BUMP_EAST(15),
    WP_SE(16),
    WP_SW(17),
    WP_NW(18),
    WP_NE(19);

    private int id;

    VesselMovementAnimation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VesselMovementAnimation forId(int id) {
        for (VesselMovementAnimation v : values()) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public boolean isWhirlpoolMove() {
        return this.getId() >= 16;
    }

    public int getIncrementXForRotation(int rotationIndex) {
        switch (this) {
            case WP_SE:
            case WP_NE:
                return -1;
            case WP_SW:
            case WP_NW:
                return 1;
        }
        switch(this) {
            case MOVE_SOUTH:
            case MOVE_NORTH:
                return 0;
            case MOVE_WEST:
                return -1;
            case MOVE_EAST:
                return 1;
            case MOVE_FORWARD:
                switch(rotationIndex) {
                    case Face.EAST:
                        return 1;
                    case Face.SOUTH:
                        return 0;
                    case Face.WEST:
                        return -1;
                    case Face.NORTH:
                        return 0;
                }
            case MOVE_BACKWARD:
                switch(rotationIndex) {
                    case Face.EAST:
                        return -1;
                    case Face.SOUTH:
                        return 0;
                    case Face.WEST:
                        return 1;
                    case Face.NORTH:
                        return 0;
                }
            case MOVE_LEFT:
                switch (rotationIndex) {
                    case Face.EAST:
                        return 0;
                    case Face.SOUTH:
                        return 1;
                    case Face.WEST:
                        return 0;
                    case Face.NORTH:
                        return -1;
                }
            case MOVE_RIGHT:
                switch (rotationIndex) {
                    case Face.EAST:
                        return 0;
                    case Face.SOUTH:
                        return -1;
                    case Face.WEST:
                        return 0;
                    case Face.NORTH:
                        return 1;
                }
            case TURN_LEFT:
                switch(rotationIndex) {
                    case Face.EAST:
                    case Face.SOUTH:
                        return 1;
                    case Face.WEST:
                    case Face.NORTH:
                        return -1;
                }

            case TURN_RIGHT:
                switch(rotationIndex) {
                    case Face.NORTH:
                    case Face.EAST:
                        return 1;
                    case Face.WEST:
                    case Face.SOUTH:
                        return -1;
                }
        }
        return 0;
    }

    public int getIncrementYForRotation(int rotationIndex) {
        switch (this) {
            case WP_SW:
            case WP_SE:
                return 1;
            case WP_NE:
            case WP_NW:
                return -1;
        }
        switch(this) {
            case MOVE_SOUTH:
                return -1;
            case MOVE_NORTH:
                return 1;
            case MOVE_WEST:
            case MOVE_EAST:
                return 0;
            case MOVE_FORWARD:
                switch(rotationIndex) {
                    case Face.EAST:
                        return 0;
                    case Face.SOUTH:
                        return -1;
                    case Face.WEST:
                        return 0;
                    case Face.NORTH:
                        return 1;
                }
            case MOVE_BACKWARD:
                switch(rotationIndex) {
                    case Face.EAST:
                        return 0;
                    case Face.SOUTH:
                        return 1;
                    case Face.WEST:
                        return 0;
                    case Face.NORTH:
                        return -1;
                }
            case MOVE_LEFT:
                switch (rotationIndex) {
                    case Face.EAST:
                        return 1;
                    case Face.SOUTH:
                        return 0;
                    case Face.WEST:
                        return -1;
                    case Face.NORTH:
                        return 0;
                }
            case MOVE_RIGHT:
                switch (rotationIndex) {
                    case Face.EAST:
                        return -1;
                    case Face.SOUTH:
                        return 0;
                    case Face.WEST:
                        return 1;
                    case Face.NORTH:
                        return 0;
                }
            case TURN_LEFT:
                switch(rotationIndex) {
                    case Face.NORTH:
                    case Face.EAST:
                        return 1;
                    case Face.WEST:
                    case Face.SOUTH:
                        return -1;
                }
            case TURN_RIGHT:
                switch(rotationIndex) {
                    case Face.WEST:
                    case Face.NORTH:
                        return 1;
                    case Face.SOUTH:
                    case Face.EAST:
                        return -1;
                }
        }
        return 0;
    }

    public boolean isOneDimensionMove() {
        return this == MOVE_FORWARD || this == MOVE_BACKWARD || this == MOVE_LEFT || this == MOVE_RIGHT || this.getId() >= 8 && this.getId() <= 11;
    }

    public Vector2 getBumpTargetPosition(int rotationIndex) {
        switch (this) {
            case BUMP_NORTH:
                return new Vector2(0, 0.2f);
            case BUMP_SOUTH:
                return new Vector2(0, -0.2f);
            case BUMP_EAST:
                return new Vector2(0.2f, 0);
            case BUMP_WEST:
                return new Vector2(-0.2f, 0);
        }

        switch (this) {
            case BUMP_PHASE_1:
                switch (rotationIndex) {
                    case Face.NORTH:
                        return new Vector2(0, 0.2f);
                    case Face.SOUTH:
                        return new Vector2(0, -0.2f);
                    case Face.EAST:
                        return new Vector2(0.2f, 0);
                    case Face.WEST:
                        return new Vector2(-0.2f, 0);
                }
            case BUMP_PHASE_2:
                return new Vector2(MOVE_FORWARD.getIncrementXForRotation(rotationIndex), MOVE_FORWARD.getIncrementYForRotation(rotationIndex));
        }

        return null;
    }

    public int getRotationTargetIndex(int rotationIndex) {
        int rot = 0;
        if (this.getId() >= 16) {
            rot = rotationIndex + 4;
            if (rot  > 15) {
                rot = 2;
            }
            return rot;
        }
        switch (this) {
            case TURN_LEFT:
                rot = rotationIndex - 4;
                if (rot < 0) {
                    rot = 14;
                }
                break;
            case TURN_RIGHT:
                rot = rotationIndex + 4;
                if (rot > 15) {
                    rot = 2;
                }
                break;
        }
        return rot;
    }

    public static VesselMovementAnimation getIdForMoveType(MoveType type) {
        switch (type) {
            case LEFT:
                return TURN_LEFT;
            case RIGHT:
                return TURN_RIGHT;
            case NONE:
                return NO_ANIMATION;
            default:
            case FORWARD:
                return MOVE_FORWARD;
        }
    }

    public static boolean isBump(VesselMovementAnimation animation) {
        return animation == BUMP_PHASE_1 || animation == BUMP_PHASE_2 || animation.getId() >= 12 && animation.getId() <= 15;
    }

    public Vector2 getInbetweenWhirlpool(Vector2 start) {
        Vector2 inbetween = start.cpy();
        switch (this) {
            case WP_SW:
                return inbetween.add(0, 1);
            case WP_SE:
                return inbetween.add(-1, 0);
            case WP_NE:
                return inbetween.add(0, -1);
            case WP_NW:
                return inbetween.add(1, 0);
        }

        return inbetween;
    }
}
