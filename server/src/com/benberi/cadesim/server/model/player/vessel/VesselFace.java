package com.benberi.cadesim.server.model.player.vessel;

public enum VesselFace {

    NORTH(14),
    WEST(10),
    SOUTH(6),
    EAST(2);

	private static VesselFace[] values = values();
    private int directionId;

    VesselFace(int dir) {
        this.directionId = dir;
    }

    public int getDirectionId() {
        return this.directionId;
    }
    
    public VesselFace getPrev() {
    	return values[(ordinal() + 1) % values.length];
    }

    public VesselFace getNext() {
    	return values[(ordinal() - 1  + values.length) % values.length];
    }

    public static VesselFace forId(int id) {
        for(VesselFace face : values()) {
            if (face.getDirectionId() == id) {
                return face;
            }
        }

        return NORTH;
    }
}
