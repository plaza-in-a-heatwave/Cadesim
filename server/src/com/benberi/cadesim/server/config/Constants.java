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
	public static final String serverTeam    = "<cadesim_team>";
	public static final String serverPrivate    = "<cadesim_private>";

	/**
	 * Version of server
	 */
	public static final String VERSION = "1.9.93";
    public static final int PROTOCOL_VERSION = 1994; // MUST match client

    /**
     * Log paths to use
     */
    public static final String logDirectory = "logs";
    public static final String logName      = "cadesim.log";

    /**
     * map paths to use
     */
    public static final String mapDirectory    = "maps";
    public static final String DEFAULT_MAPNAME = "default.txt";
    public static final String TEST_MAPNAME    = "test.txt";

    /**
     * return types ( < 128 good; >= 128 bad)
     */
    public static final int EXIT_ERROR_CANT_UPDATE              = 141;
    public static final int EXIT_ERROR_UNKNOWN                  = 140;
    public static final int EXIT_ERROR_BAD_CONFIG               = 133;
    public static final int EXIT_ERROR_CANT_FIND_MAPS           = 130;
    public static final int EXIT_ERROR_CANT_CREATE_LOGS         = 129;
    public static final int EXIT_ERROR_CANT_BIND_LOCAL          = 128;
    public static final int EXIT_SUCCESS_SHUTDOWN_BY_STOPFILE   = 2;
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

    public static final int TURN_EXTRA_TIME = 13; // deciseconds
    public static final int OUTGOING_PACKETS_PLAYER_PER_TICK = 100;
    public static final int INCOMING_PACKETS_PLAYER_PER_TICK = 100;

    /**
     * server does misc tasks every n seconds
     */
    public static final int SERVER_ADMIN_INTERVAL_MILLIS = 2000;

    /**
     * How long a player can not respond before they are lagged out.
     */
    public static final int PLAYER_LAG_TIMEOUT_MS = 30000; // milliseconds

    public static final int PLAYER_LAG_TOLERABLE_MISSED_PACKETS = 2;

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
	 * how big can team names (e.g. attacker, defender) be?
	 */
	public static final int MAX_TEAMNAME_SIZE = 12;

	/**
	 * minimum times
	 */
	public static final int MIN_TURN_DURATION  = 5;
	public static final int MIN_ROUND_DURATION = 30;

	/**
	 * minimum values for breaks
	 */
    public static final int MIN_BREAK_DURATION = 10;
    public static final int MIN_BREAK_INTERVAL = 60;

    /**
     * auto update variables
     */
    public static final String AUTO_UPDATING_LOCK_DIRECTORY_NAME = "AUTOUPDATING.LOCK";
    public static final String AUTO_UPDATING_ID_FILE_NAME        = "id.tmp";
    public static final int AUTO_UPDATE_MAX_LOCK_WAIT_MS         = 600 * 1000; // 10 min
    public static final int AUTO_UPDATE_MAX_WAIT_GETDOWN_MS      = 300 * 1000; //  5 min

    /**
     * InstanceFileManager file instance names and stop file names
     */
    public static final String INSTANCE_FILENAME_PREFIX = ".CADESIM_INSTANCE_";
    public static final String STOP_FILENAME            = ".STOP";

    /**
     * checks for stopfile every n seconds
     */
    public static final int SERVER_STOPFILE_CHECK_MILLIS = 5000;

    /**
     * Enable some developer features, for instance a continuous reboot mode.
     *
     * Individual options can also be enabled/disabled as required.
     */
    public static final boolean ENABLE_DEVELOPER_FEATURES = false;

    public static final boolean ENABLE_CONTINUOUS_REBOOT  = ENABLE_DEVELOPER_FEATURES;
    public static final int     CONTINOUS_REBOOT_INTERVAL = 60000;  // 60,000 ms, 60 sec

    public static final boolean ENABLE_CHOOSE_BLACKSHIP = ENABLE_DEVELOPER_FEATURES; // allow blackship
}
