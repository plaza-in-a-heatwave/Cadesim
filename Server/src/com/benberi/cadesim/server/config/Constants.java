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
	public static final String VERSION = "1.9.92";
    public static final int PROTOCOL_VERSION = 12; // MUST match client

    /**
     * Log paths to use
     */
    public static final String logDirectory = "logs";
    public static final String logName      = "cadesim.log";
    
    /**
     * map paths to use
     */
    public static final String mapDirectory = "maps";
    public static final String DEFAULT_MAPNAME = "default.txt";
    
    /**
     * return types ( < 128 good; >= 128 bad)
     */
    public static final int EXIT_ERROR_CANT_UPDATE              = 141;
    public static final int EXIT_ERROR_UNKNOWN                  = 140;
    public static final int EXIT_ERROR_BAD_CONFIG               = 133;
    public static final int EXIT_ERROR_CANT_FIND_MAPS           = 130;
    public static final int EXIT_ERROR_CANT_CREATE_LOGS         = 129;
    public static final int EXIT_ERROR_CANT_BIND_LOCAL          = 128;
    public static final int EXIT_SUCCESS_SCHEDULED_UPDATE       = 1;
    public static final int EXIT_SUCCESS                        = 0;
    
    /**
     * The delay of the main game service loop in milliseconds
     */
    public static final int SERVICE_LOOP_DELAY = 100; // ms

    public static final int DEFAULT_VESSEL_TYPE = 0;

    /**
     * Rates of bilge increase (pct)
     */
    // observed 1.06-1.12% per second natural bilge growth, max damage // max bilge growth
    // observed 0.0555%    per second natural bilge growth, 0   damage // min bilge growth
    public static final double MIN_BILGE_INCREASE_PERCENT_PER_SEC = 0.0555;
    public static final double MAX_BILGE_INCREASE_PERCENT_PER_SEC = 1.08;

    /** bilge affect on sailors - multiply normal token generation rate with this coefficient.
     * Observed move generation rate falls to 0.17578125 of its original value.
     * (1 - (bilge_fraction * BILGE_SAILOR_PENALTY)) * MOVE_GENERATION_RATE == new move generation rate.
     */
    public static final double BILGE_SAILOR_PENALTY = 0.17578125;

    /**
     * This is used to timeout players that did not notify the server about their animation finish for the given
     * timeout value
     */
    public static final int TURN_FINISH_TIMEOUT = 600; // deciseconds - should be > turntime + extratime

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
	 * split large messages at this value (advisory only)
	 */
	public static final int SPLIT_CHAT_MESSAGES_THRESHOLD = 240;

	/**
	 * how often to inform players about scheduled updates
	 */
	public static final long BROADCAST_SCHEDULED_UPDATE_INTERVAL_MILLIS = 300000;

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

	/**
	 * minimum values for breaks
	 */
    public static final int MIN_BREAK_DURATION = 10;
    public static final int MIN_BREAK_INTERVAL = 60;

    /**
     * auto update variables
     */
    public static final String AUTO_UPDATING_LOCK_DIRECTORY_NAME = "AUTOUPDATING.LOCK";
    public static final String AUTO_UPDATING_ID_FILE_NAME = "id.tmp";
    public static final int[] STAGGER_AUTOUPDATE_RANGE_MINUTES = {0, 20}; // 20 min window
    public static final int AUTO_UPDATE_MAX_LOCK_WAIT_MS = 600 * 1000; // 10 min
}
