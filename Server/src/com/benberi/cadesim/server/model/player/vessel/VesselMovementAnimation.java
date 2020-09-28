package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.util.Position;

import static com.benberi.cadesim.server.model.cade.map.BlockadeMap.*;

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

    public boolean isWhirlpool() {
        return id >= 16;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        switch (this) {
            case NO_ANIMATION:
                return 0;
            case TURN_LEFT:
            case TURN_RIGHT:
            case MOVE_FORWARD:
            case MOVE_LEFT:
            case MOVE_RIGHT:
                return 500;
            default:
                return 0;
        }
    }

    public static VesselMovementAnimation getBumpForPhase(int phase) {
        switch (phase) {
            case 0:
                return BUMP_PHASE_1;
            case 1:
                return BUMP_PHASE_2;
        }

        return null;
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

    public static VesselMovementAnimation getBumpAnimation(Player bumper, Player bumped) {
        switch (bumper.getFace()) {
            case NORTH:
                switch (bumped.getFace()) {
                    case NORTH:
                        return VesselMovementAnimation.MOVE_FORWARD;
                    case SOUTH:
                        return VesselMovementAnimation.MOVE_BACKWARD;
                    case WEST:
                        return VesselMovementAnimation.MOVE_RIGHT;
                    case EAST:
                        return VesselMovementAnimation.MOVE_LEFT;
                }
                break;
            case SOUTH:
                switch (bumped.getFace()) {
                    case NORTH:
                        return VesselMovementAnimation.MOVE_BACKWARD;
                    case SOUTH:
                        return VesselMovementAnimation.MOVE_FORWARD;
                    case WEST:
                        return VesselMovementAnimation.MOVE_LEFT;
                    case EAST:
                        return VesselMovementAnimation.MOVE_RIGHT;
                }
                break;
            case WEST:
                switch (bumped.getFace()) {
                    case NORTH:
                        return VesselMovementAnimation.MOVE_LEFT;
                    case SOUTH:
                        return VesselMovementAnimation.MOVE_RIGHT;
                    case WEST:
                        return VesselMovementAnimation.MOVE_FORWARD;
                    case EAST:
                        return VesselMovementAnimation.MOVE_BACKWARD;
                }
                break;
            case EAST:
                switch (bumped.getFace()) {
                    case NORTH:
                        return VesselMovementAnimation.MOVE_RIGHT;
                    case SOUTH:
                        return VesselMovementAnimation.MOVE_LEFT;
                    case WEST:
                        return VesselMovementAnimation.MOVE_BACKWARD;
                    case EAST:
                        return VesselMovementAnimation.MOVE_FORWARD;
                }
                break;
        }

        return VesselMovementAnimation.NO_ANIMATION;
    }

    public Position getPositionForAnimation(Player player) {
        switch (this) {
            case MOVE_FORWARD:
                switch (player.getFace()) {
                    case NORTH:
                        return player.copy().addY(1);
                    case SOUTH:
                        return player.copy().addY(-1);
                    case WEST:
                        return player.copy().addX(-1);
                    case EAST:
                        return player.copy().addX(1);
                    default:
                        break;
                }
            case MOVE_BACKWARD:
                switch (player.getFace()) {
                    case NORTH:
                        return player.copy().addY(-1);
                    case SOUTH:
                        return player.copy().addY(1);
                    case WEST:
                        return player.copy().addX(1);
                    case EAST:
                        return player.copy().addX(-1);
                    default:
                        break;
                }
            case MOVE_LEFT:
                switch (player.getFace()) {
                    case NORTH:
                        return player.copy().addX(-1);
                    case SOUTH:
                        return player.copy().addX(1);
                    case WEST:
                        return player.copy().addY(-1);
                    case EAST:
                        return player.copy().addY(1);
                    default:
                        break;
                }
            case MOVE_RIGHT:
                switch (player.getFace()) {
                    case NORTH:
                        return player.copy().addX(1);
                    case SOUTH:
                        return player.copy().addX(-1);
                    case WEST:
                        return player.copy().addY(1);
                    case EAST:
                        return player.copy().addY(-1);
                    default:
                        break;
                }

            default:
                break;
        }

        return player;
    }

    public static VesselMovementAnimation getBumpAnimationForAction(int tile) {
    	//TODO fix correct directions depending on next position and face
        switch (tile) {
            case WIND_NORTH:
                return BUMP_NORTH;
            case WIND_SOUTH:
                return BUMP_SOUTH;
            case WIND_WEST:
                return BUMP_WEST;
            case WIND_EAST:
                return BUMP_EAST;
            case BlockadeMap.WP_NE:
                return BUMP_SOUTH;
            case BlockadeMap.WP_NW:
                return BUMP_EAST;
            case BlockadeMap.WP_SW:
                return BUMP_NORTH;
            case BlockadeMap.WP_SE:
                return BUMP_WEST;
        }
        return BUMP_NORTH;
    }

    public static VesselMovementAnimation getSubAnimation(int tile) {
        switch (tile) {
            case WIND_NORTH:
                return MOVE_NORTH;
            case WIND_SOUTH:
                return MOVE_SOUTH;
            case WIND_WEST:
                return MOVE_WEST;
            case WIND_EAST:
                return MOVE_EAST;
            case BlockadeMap.WP_NE:
                return WP_NE;
            case BlockadeMap.WP_NW:
                return WP_NW;
            case BlockadeMap.WP_SW:
                return WP_SW;
            case BlockadeMap.WP_SE:
                return WP_SE;
        }
        return MOVE_NORTH;
    }
}
