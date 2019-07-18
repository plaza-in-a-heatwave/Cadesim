package com.benberi.cadesim.server;

import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.codec.packet.ServerPacketManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

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

    private static File log;

    /**
     * The configuration of the server
     */
    private ServerConfiguration configuration;

    private ServerPacketManager packets;

    public ServerContext(ServerConfiguration config) {
        this.configuration = config;
        this.players = new PlayerManager(this);
        this.timeMachine = new BlockadeTimeMachine(this);
        this.map = new BlockadeMap(this);
        this.packets = new ServerPacketManager(this);
    }

    static {
        Date date = new Date();
        new File("logs/").mkdirs();
        log = new File("logs/" + date.toString().replace(" ", "_").replace(":", "") + ".txt");
        try {
            log.createNewFile();
        } catch (IOException e) {
            System.out.println("failed to create log file: " + log.getName() + " , check log directory permissions");
            System.exit(1);
        }
    }

    public static void log(String message) {
        Date date = new Date();
        try {
            message = "[" + date.toString().replace(" ", "_").replace(":", "") + "]: " + message + "\n";
            Files.write(ServerContext.log.toPath(), message.getBytes(), StandardOpenOption.APPEND);
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
     * Gets the server configuration
     * @return {@link #configuration}
     */
    public ServerConfiguration getConfiguration() {
         return configuration;
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

    /**
     * Gets the packet manager
     * @return {@link #packets}
     */
    public ServerPacketManager getPackets() {
        return packets;
    }
}
