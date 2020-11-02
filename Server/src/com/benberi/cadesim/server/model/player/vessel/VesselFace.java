package com.benberi.cadesim.server.model.player.vessel;

import java.util.HashMap;

public enum VesselFace {

    NORTH(14),
    SOUTH(6),
    WEST(10),
    EAST(2);

    private int directionId;

    VesselFace(int dir) {
        this.directionId = dir;
    }

    public int getDirectionId() {
        return this.directionId;
    }

    public VesselFace getNext() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
        }

        return NORTH;
    }

    /**
     * map human readable face ids to integer ids
     */
    public static final HashMap<String, VesselFace> FACE_STRINGS = new HashMap<String, VesselFace>() {
        private static final long serialVersionUID = 1L;

    {
        put("N", NORTH);
        put("S", SOUTH);
        put("W", WEST);
        put("E", EAST);
    }};

    public static VesselFace forId(int id) {
        for(VesselFace face : values()) {
            if (face.getDirectionId() == id) {
                return face;
            }
        }

        return NORTH;
    }
}
