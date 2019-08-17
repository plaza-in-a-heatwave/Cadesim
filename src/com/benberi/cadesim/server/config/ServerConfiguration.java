package com.benberi.cadesim.server.config;

public class ServerConfiguration {

    /**
     * A player limit between 2 to 50
     */
    private int playerLimit;

    /**
     * Map type
     */
    private int mapType;

    /**
     * Server port
     */
    private int port = 4666;
    
    /**
     * Turn duration
     */
    private static int turnDuration;
    
    /**
     * Round duration
     */
    private static int roundDuration;
    
    /**
     * Respawn delay
     */
    private static int respawnDelay;
    
   

	private static String mapName = "default.txt";

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public static int getTurnDuration() {
    	return turnDuration;
    }
    
    public static void setTurnDuration(int turnDuration) {
    	// server works with internal representation in "deci-seconds" - 1/10th of a second
    	ServerConfiguration.turnDuration = 10 * turnDuration;
    }
    
    public static int getRoundDuration() {
		return roundDuration;
	}

	public static void setRoundDuration(int roundDuration) {
		// server works with internal representation in "deci-seconds" - 1/10th of a second
		ServerConfiguration.roundDuration = 10 * roundDuration;
	}
    
    public static int getRespawnDelay() {
    	return respawnDelay;
    }
    
    public void setRespawnDelay(int respawnDelay) {
    	ServerConfiguration.respawnDelay = respawnDelay;
    }

    @Override
    public String toString() {
        return "[Player limit: " + playerLimit + ", Map Name: " + mapName + " Port: " + port + "]";
    }
    
    public static String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
