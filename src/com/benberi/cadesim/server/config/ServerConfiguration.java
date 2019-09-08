package com.benberi.cadesim.server.config;

public class ServerConfiguration {
	/**
	 * Store/retrieve/overwrite server gameplay defaults.
	 */
	
    /**
     * Server defaults. Overridden by CLI during initialisation.
     */
    private static int playerLimit = 5;
    private static int port = 4666;
    private static int turnDuration  = 300;        // "deciseconds"
    private static int roundDuration = 18000;      // "deciseconds"
    private static int respawnDelay  = 2;          // turns
    private static int mapRotationPeriod = 5;      // turns
    private static String mapName = "default.map";

    public static int getPlayerLimit() {
        return ServerConfiguration.playerLimit;
    }

    public static void setPlayerLimit(int playerLimit) {
        ServerConfiguration.playerLimit = playerLimit;
    }

    public static int getPort() {
        return ServerConfiguration.port;
    }

    public static void setPort(int port) {
        ServerConfiguration.port = port;
    }
    
    public static int getTurnDuration() {
    	return ServerConfiguration.turnDuration;
    }
    
    public static void setTurnDuration(int turnDurationInDeciseconds) {
    	ServerConfiguration.turnDuration = turnDurationInDeciseconds;
    }
    
    public static int getRoundDuration() {
		return ServerConfiguration.roundDuration;
	}

	public static void setRoundDuration(int roundDurationInDeciseconds) {
		ServerConfiguration.roundDuration = roundDurationInDeciseconds;
	}
    
    public static int getRespawnDelay() {
    	return ServerConfiguration.respawnDelay;
    }
    
    public static void setRespawnDelay(int respawnDelay) {
    	ServerConfiguration.respawnDelay = respawnDelay;
    }

    /**
	 * gets a printable config report.
	 * Note that the turnDuration/roundDuration are returned
	 * in **seconds** rather than their internal representation as
	 * deciseconds.
	 */
    public static String getConfig() {
        return "[" + 
        		"Player limit:" + getPlayerLimit() + ", " +
        		"Map Name:" + getMapName() + ", " +
        		"Port:" + getPort() + ", " +
        		"turn duration:" + getTurnDuration() / 10 + "s, " +
        		"round duration:" + getRoundDuration() / 10 + "s, " +
        		"sink delay:" + getRespawnDelay() + " turns, " +
        		"map rotation period:" + getMapRotationPeriod() + " turns " +
        		"]";
    }
    
    public static String getMapName() {
        return ServerConfiguration.mapName;
    }

    public static void setMapName(String mapName) {
        ServerConfiguration.mapName = mapName;
    }

	public static int getMapRotationPeriod() {
		return ServerConfiguration.mapRotationPeriod;
	}

	public static void setMapRotationPeriod(int mapRotationPeriod) {
		ServerConfiguration.mapRotationPeriod = mapRotationPeriod;
	}
}
