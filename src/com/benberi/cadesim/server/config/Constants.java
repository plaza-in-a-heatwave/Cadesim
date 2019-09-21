package com.benberi.cadesim.server.config;

public class Constants {	
	/**
	 * Name of server
	 */
	public static final String name = "Cadesim server";
	
	/**
	 * server broadcast/private sender ids
	 * these must match the ones specified in the client for colors to
	 * show correctly.
	 * Banned prefix should contain substring of the ids.
	 */
	public static final String bannedSubstring  = "cadesim"; // players not allowed to use this in their name
	public static final String serverBroadcast  = "<cadesim_broadcast>";
	public static final String serverPrivate    = "<cadesim_private>";
	

	/**
	 * Version of server
	 */
	public static final String VERSION = "1.9.7";

    /**
     * The protocol version to allow connections from
     */
    public static final int PROTOCOL_VERSION = 9;

    /**
     * Log paths to use
     */
    public static final String logDirectory = "logs";
    public static final String logName      = "cadesim.log";
    
    /**
     * map paths to use
     */
    public static final String mapDirectory = "maps";
    
    /**
     * return types
     */
    public static final int EXIT_ERROR_UNKNOWN          = 13;
    public static final int EXIT_ERROR_BAD_CONFIG       =  6;
    public static final int EXIT_ERROR_CANT_FIND_MAPS   =  3;
    public static final int EXIT_ERROR_CANT_CREATE_LOGS =  2;
    public static final int EXIT_ERROR_CANT_BIND_LOCAL  =  1;
    public static final int EXIT_SUCCESS                =  0;
    
    /**
     * The delay of the main game service loop in milliseconds
     */
    public static final int SERVICE_LOOP_DELAY = 100; // ms

    public static final int DEFAULT_VESSEL_TYPE = 0;

    /**
     * Bilge increasing rate after X damage
     */
    public static final double BILGE_INCREASE_RATE_PER_TICK = 0.1;

    /**
     * This is used to timeout players that did not notify the server about their animation finish for the given
     * timeout value
     */
    public static final int TURN_FINISH_TIMEOUT = 200; // deciseconds

    public static final int TURN_EXTRA_TIME = 13; // deciseconds
    public static final int OUTGOING_PACKETS_PLAYER_PER_TICK = 100;
    public static final int INCOMING_PACKETS_PLAYER_PER_TICK = 100;

    /**
     * largest possible player name
     */
	public static final int MAX_NAME_SIZE = 19;

	/**
	 * largest possible auth code
	 */
	public static final int MAX_CODE_SIZE = 30;

	/**
	 * largest possible server name
	 */
	public static final int MAX_SERVER_NAME_SIZE = 19;

	/**
	 * n seconds to register
	 */
	public static final long REGISTER_TIME_MILLIS = 2000;

	/**
	 * server does misc tasks every n seconds
	 */
	public static final int SERVER_ADMIN_INTERVAL_MILLIS = 2000;

	/**
	 * how big can team names (e.g. attacker, defender) be?
	 */
	public static final int MAX_TEAMNAME_SIZE = 12;
	
	/**
	 * minimum times
	 */
	public static final int MIN_TURN_DURATION = 10;
	public static final int MIN_ROUND_DURATION = 60;
}
