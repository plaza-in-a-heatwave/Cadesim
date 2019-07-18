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
