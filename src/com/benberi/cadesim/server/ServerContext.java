package com.benberi.cadesim.server;

import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.codec.packet.ServerPacketManager;
import com.benberi.cadesim.server.config.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The server context containing references to every massive part of the
 * server model, domain and configuration
 */
public class ServerContext {

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

    private static File logFile;

    private ServerPacketManager packets;

    public ServerContext() {
        this.players = new PlayerManager(this);
        this.timeMachine = new BlockadeTimeMachine(this);
        this.map = new BlockadeMap(this);
        this.packets = new ServerPacketManager(this);
    }

    static {
    	new File(Constants.logDirectory).mkdirs();
    	logFile = new File(Constants.logDirectory + "/" + Constants.logName);
        try {
            logFile.createNewFile();
            log("Using logfile: " + logFile.getPath());
        } catch (IOException e) {
            System.out.println("failed to create log file: " + logFile.getName() + " , check log directory permissions");
            System.exit(1);
        }
    }

    public static void log(String message) {
        try {
        	String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
            message = "[" + timestamp + "]: " + message + "\n";

            // put to log file
            Files.write(ServerContext.logFile.toPath(), message.getBytes(), StandardOpenOption.APPEND);

            // also print to stdout so we can see what's going on...
            System.out.print(message);
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
}
