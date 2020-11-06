package com.benberi.cadesim;

public class Constants {

	/**
	 * Name of client
	 */
    public static final String name = "CadeSim";

    /**
     * Name the server identifies itself with
     * Only the server will send chats with this name
     */
    public static final String serverBroadcast = "<cadesim_broadcast>";
    public static final String serverPrivate   = "<cadesim_private>";

    /**
     * Version of client
     */
    public static  String VERSION = "1.9.93";
    public static final int PROTOCOL_VERSION = 1994; // MUST match server

	public static final int MAX_NAME_SIZE = 19;   // name of player

	public static final int MAX_CODE_SIZE = 30;   // server code

    /**
     * The default port the simulator is using - being initialized based on 
     * last room selected on startup.
     */
    public static int PROTOCOL_PORT = 0;
    public static String SERVER_CODE = "";

    /**
     * Announce client lives every few ms
     */
    public static int CLIENT_SEND_ALIVE_MS = 2000;

    /**
     * Enable some developer features, for instance the black ship, and a client lag test mode.
     *
     * Individual options can also be enabled/disabled as required.
     */
    public static final boolean ENABLE_DEVELOPER_FEATURES = false;
    public static final boolean ENABLE_CHOOSE_BLACKSHIP = ENABLE_DEVELOPER_FEATURES; // see dropdown in lobby
    public static final boolean ENABLE_LAG_TEST_MODE    = ENABLE_DEVELOPER_FEATURES; // toggle with /lagtestmode. disabled when in lobby.

    public static final int MAX_CLIENT_LOCK_MILLIS = 20000; // last resort: force unlock once we reach 20s if not already unlocked.
    public static boolean AUTO_UPDATE = true;
}
