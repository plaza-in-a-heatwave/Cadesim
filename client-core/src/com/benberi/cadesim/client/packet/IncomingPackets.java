package com.benberi.cadesim.client.packet;

public class IncomingPackets {
    // n.b.
    // these numbers must match up to the ones
    // in the packet class definition files in the server
    // e.g. LoginResponsePacket.java must call super(0)
    // when constructing the object.
    public static final int LOGIN_RESPONSE       = 0;
    public static final int SEND_MAP             = 1;
    public static final int ADD_PLAYER_SHIP      = 2;
    public static final int SET_TIME             = 3;
    public static final int SEND_DAMAGE          = 4;
    public static final int SEND_MOVE_TOKENS     = 5;
    public static final int MOVE_SLOT_PLACED     = 6;
    public static final int TURN_ANIMATION       = 7;
    public static final int SET_PLAYERS          = 8;
    public static final int MOVES_BAR_UPDATE     = 9;
    public static final int  CANNONS_SLOT_PLACED = 10;
    public static final int  TARGET_SEAL         = 11;
    public static final int  PLAYER_RESPAWN      = 12;
    public static final int  SEND_POSITIONS      = 13;
    public static final int  REMOVE_PLAYER_SHIP  = 14;
    public static final int  SEND_MOVES          = 15;
    public static final int  SET_FLAGS           = 16;
    public static final int  SET_PLAYER_FLAGS    = 17;
    public static final int  SET_TEAM_NAMES      = 18;
    public static final int  RECEIVE_MESSAGE     = 19;
    public static final int  LIST_ALL_MAPS       = 20;
    public static final int  GAME_SETTINGS       = 21;
}
