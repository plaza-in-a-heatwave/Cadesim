package com.benberi.cadesim.game.entity.vessel.move;

/**
 * Represents a move type
 */
public enum MoveType {
    FORWARD(2),
    LEFT(1),
    RIGHT(3),
    NONE(0);

    private int id;
    private boolean temp;

    MoveType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isTemp() {
        return this.temp;
    }

    public MoveType setTemp(boolean temp) {
        this.temp = temp;
        return this;
    }

    public int getIncrementXForRotation(int rotationIndex) {
        switch(this) {
            case FORWARD:
                switch(rotationIndex) {
                    case 2:
                        return 1;
                    case 6:
                        return 0;
                    case 10:
                        return -1;
                    case 14:
                        return 0;
                }
            case LEFT:
                switch(rotationIndex) {
                    case 2:
                    case 6:
                        return 1;
                    case 10:
                    case 14:
                        return -1;
                }
            case RIGHT:
                switch(rotationIndex) {
                    case 14:
                    case 2:
                        return 1;
                    case 10:
                    case 6:
                        return -1;
                }
        }
        return 0;
    }

    public int getIncrementYForRotation(int rotationIndex) {
        switch(this) {
            case FORWARD:
                switch(rotationIndex) {
                    case 2:
                        return 0;
                    case 6:
                        return -1;
                    case 10:
                        return 0;
                    case 14:
                        return 1;
                }
            case LEFT:
                switch(rotationIndex) {
                    case 14:
                    case 2:
                        return 1;
                    case 10:
                    case 6:
                        return -1;
                }
            case RIGHT:
                switch(rotationIndex) {
                    case 10:
                    case 14:
                        return 1;
                    case 6:
                    case 2:
                        return -1;
                }
        }
        return 0;
    }

    public int getRotationTargetIndex(int rotationIndex) {
        int rot = 0;
        switch (this) {
            case LEFT:
                rot = rotationIndex - 4;
                if (rot < 0) {
                    rot = 14;
                }
                break;
            case RIGHT:
                rot = rotationIndex + 4;
                if (rot > 15) {
                    rot = 2;
                }
                break;
        }
        return rot;
    }

    public MoveType getPrevious() {
        switch (this) {
            default:
            case LEFT:
                return NONE;
            case FORWARD:
                return LEFT;
            case RIGHT:
                return FORWARD;
        }
    }

    public MoveType getNext() {
        switch (this) {
            default:
            case LEFT:
                return FORWARD;
            case FORWARD:
                return RIGHT;
            case RIGHT:
                return NONE;
        }
    }

    public static MoveType forId(int id) {
        for(MoveType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return null;
    }
}
