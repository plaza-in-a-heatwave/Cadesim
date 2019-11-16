package com.benberi.cadesim.server.codec;

public class OutGoingPackets {

    /**
     * Used to response to a login request from the client
     */
    public static final int LOGIN_RESPONSE = 0;

    /**
     * Sends the blockade map sea battle
     */
    public static final int SEND_MAP = 1;

    /**
     * Adds a ship to the sea battle
     */
    public static final int ADD_SHIP = 2;

    /**
     * Sends game time and turn time
     */
    public static final int TIME_PACKET = 3;

    /**
     * Updates ship damage and bilge
     */
    public static final int SHIP_DAMAGE_BILGE = 4;

    /**
     * Sends the move tokens (The ones that are generating, seals)
     */
    public static final int SEND_MOVE_TOKENS = 5;

    /**
     * @deprecated
     * Move place verification. This is no longer used, replaced with {@link #SEND_MOVES}
     */
    public static final int PLACE_MOVE = 6;

    /**
     * Sends all vessel animations on execution
     */
    public static final int TURN_VESSEL_ANIMS = 7;

    /**
     * Sends all vessels
     */
    public static final int SEND_ALL_VESSELS = 8;

    /**
     * Updates the move bar for client
     */
    public static final int MOVE_BAR_UPDATE = 9;

    /**
     * @deprecated
     * Cannon place verification. This is no longer used, replaced with {@link #SEND_MOVES}
     */
    public static final int CANNON_PLACE = 10;

    /**
     * Sets the target token generation
     */
    public static final int SET_SEAL_TARGET = 11;

    /**
     * Respawns a player
     */
    public static final int PLAYER_SUNK = 12;

    /**
     * Sends all player positions
     */
    public static final int SEND_POSITIONS = 13;

    /**
     * Removes a ship from the sea battle
     */
    public static final int REMOVE_SHIP = 14;

    /**
     * Sends all selected move , cannon tokens of your player
     */
    public static final int SEND_MOVES = 15;

    /**
     * Sets/Updates the flags on the blockade
     */
    public static final int SET_FLAGS = 16;

    public static final int SET_PLAYER_FLAGS = 17;
    
    public static final int SET_TEAM_NAMES = 18;
    
    /**
     * delivers messages to clients
     */
    public static final int RECEIVE_MESSAGE = 19;
    
    /**
     * ends the game and sends stats across
     */
    public static final int END_GAME = 20;
}
