package com.benberi.cadesim.server.codec;

public class IncomingPackets {
	// n.b.
	// these numbers must match up to the ones
	// in the packet class definition files in the client
	// e.g. ManuaverSlotChanged.java must call super(2)
	// when constructing the object.
    public static final int LOGIN_PACKET = 0;
    public static final int PLACE_MOVE = 1;
    public static final int MANUAVER_SLOT_CHANGED = 2;
    public static final int CANNON_PLACE = 3;
    public static final int SEAL_TOGGLE = 4;
    public static final int SET_SEAL_TARGET = 5;
    public static final int TURN_FINISH_NOTIFICATION = 6;
    public static final int OCEANSIDE_REQUEST = 7;
    public static final int POST_MESSAGE = 8;
    public static final int SWAP_MOVE = 9;
    public static final int CLIENT_ALIVE = 10;
    public static final int GAME_SETTINGS = 11;
    public static final int SET_TEAM = 12;
}
