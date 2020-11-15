package com.benberi.cadesim.server;

import com.benberi.cadesim.server.model.player.PlayerManager;

import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.codec.packet.ServerPacketManager;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;

/**
 * The server context containing references to every massive part of the
 * server model, domain and configuration
 */
public class ServerContext {
    /**
     *  monitor uptime (after has started up). In ms.
     */
    private long time1 = 0;
    public long getUpTimeMillis() {
        return System.currentTimeMillis() - time1;
    }
    public void initialiseUpTimeMillis(long time1) {
        this.time1 = System.currentTimeMillis() + time1;
    }

    /**
     * The player manager
     */
    private PlayerManager players;

    /**
     * The time machine of blockade
     */
    private BlockadeTimeMachine timeMachine;

    /**
     * The blockade map
     */
    private BlockadeMap map;

    /*
     * Logging options
     */
    private static File logFile = null;
    private static File unifiedLogFile = null;
    public final static boolean LOG_MODE_INSTANCE = true;  // cheap enum for logMode lol
    public final static boolean LOG_MODE_UNIFIED  = false;
    private static boolean logMode = LOG_MODE_UNIFIED;

    private ServerPacketManager packets;

    private RegressionTests regressionTests;

    private byte pingCounter = 0;
    
    private static int[][] mapArray;
    private static String customMapName;

    public byte incrementPingCounter() {
        return ++pingCounter;

    }

    public byte getPingCounter() {
        return pingCounter;
    }

    public ServerContext() {
        this.players = new PlayerManager(this);
        this.timeMachine = new BlockadeTimeMachine(this);
        this.map = new BlockadeMap(this);
        this.packets = new ServerPacketManager(this);
        this.regressionTests = new RegressionTests(this, false); // verbose switch

        // instruct the logger to switch across to using per-instance logs
        // now that we have access to context info and can create meaningful logfile names
        ServerContext.setLogMode(ServerContext.LOG_MODE_INSTANCE);
    }

    public static boolean getLogMode() {
        return logMode;
    }
    public static void setLogMode(boolean logMode) {
        ServerContext.logMode = logMode;
    }

    /**
     * Log a message. Depending on logMode, will log to either a unified or an instance logfile.
     * @param message the message to log.
     */
    public static void log(String message) {
        // format message
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(
            new DateTimeFormatterBuilder().appendInstant(3).toFormatter()
        );
        message = "[" + timestamp + "]: " + message + "\n";

        // put to file
        if (getLogMode() == ServerContext.LOG_MODE_UNIFIED) {
            logUnified(message);
        }
        else
        {
            logInstance(message);
        }

        // also print to stdout so we can see what's going on...
        System.out.print(message);
    }

    /**
     * Print to a "unified log" file that is shared with all cadesim instances.
     * This is only to be used before the main logfile is created. Useful for catching error messages.
     * This approach taken so that main logfiles can be per-instance and also be named according to port etc.
     *
     * Use setLogMode() to change to instance logs.
     *
     * @param message the message to log.
     */
    private static void logUnified(String message) {
        if (unifiedLogFile == null) {
            new File(Constants.logDirectory).mkdirs();
            unifiedLogFile = new File(Constants.logDirectory + "/" + "unified_" + Constants.logName);
            try {
                unifiedLogFile.createNewFile();
            } catch (IOException e) {
                System.out.println("failed to create unifiedLogFile: " + unifiedLogFile.getName() + " , check log directory permissions");
                System.exit(Constants.EXIT_ERROR_CANT_CREATE_LOGS);
            }
        }
        try {
            Files.write(ServerContext.unifiedLogFile.toPath(), message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print to an "instance log" which is unique for each instance of cadesim.
     * This is to be used after server bootstraps.
     * Use setLogMode() to change to unified, but you probably won't want to do this.
     * @param message
     */
    private static void logInstance(String message) {
        if (logFile == null) {
            new File(Constants.logDirectory).mkdirs();

            // TODO names are based on ports. Could we hash each port to a unique room name?
            logFile = new File(Constants.logDirectory + "/" + "port_" + ServerConfiguration.getPort() + "_" + Constants.logName);
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("failed to create log file: " + logFile.getName() + " , check log directory permissions");
                System.exit(Constants.EXIT_ERROR_CANT_CREATE_LOGS);
            }
        }
        try {
            Files.write(ServerContext.logFile.toPath(), message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the player manager
     * @return {@link #players}
     */
    public PlayerManager getPlayerManager() {
        return players;
    }

    /**
     * Gets the blockade time machine
     * @return {@link #timeMachine}
     */
    public BlockadeTimeMachine getTimeMachine() {
        return timeMachine;
    }

    /**
     * Gets the map handler
     * @return  {@link #map}
     */
    public BlockadeMap getMap() {
        return map;
    }
    
    /** Sets the map handler
     * @return {@link #map}
     */
    public void renewMap() {
    	map = new BlockadeMap(this);
    }

    /**
     * Gets the packet manager
     * @return {@link #packets}
     */
    public ServerPacketManager getPackets() {
        return packets;
    }

    /**
     * Gets the regression tests
     * @return {@link #regressionTests}
     */
    public RegressionTests getRegressionTests() {
        return regressionTests;
    }
	public static int[][] getMapArray() {
		return ServerContext.mapArray;
	}
	public static void setMapArray(int[][] mapArray) {
		ServerContext.mapArray = mapArray;
	}
	
	public static String getCustomMapName() {
		return ServerContext.customMapName;
	}
	public static void setCustomMapName(String name) {
		ServerContext.customMapName = name;
	}
}
