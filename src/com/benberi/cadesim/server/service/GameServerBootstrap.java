package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.CadeServer;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

import com.benberi.cadesim.server.util.RandomUtils;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Obsidio game server bootstrap
 *
 * @author Ben Beri <benberi545@gmail.com>
 *                  <https://github.com/benberi>
 */
public class GameServerBootstrap {

    /**
     * The service executor
     */
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2); // 2 threads, 1 for netty, 1 for game logic

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The start time of the server
     */
    private long start;
    public GameServerBootstrap() {
        context = new ServerContext();
    }

    /**
     * Start the server
     * @throws InterruptedException
     */
    private void startServer() throws InterruptedException {
        start = System.currentTimeMillis();

        ServerContext.log("Using config: " + ServerConfiguration.getConfig());
        ServerContext.log("Starting up the host server....");
        CadeServer server = new CadeServer(context, this); // to notify back its done
        executorService.execute(server);
    }

    /**
     * Start the services
     */
    public void startServices() {

        ServerContext.log("Starting up the game service....");
        GameService service = new GameService(context);
        executorService.scheduleAtFixedRate(service, 0, Constants.SERVICE_LOOP_DELAY, TimeUnit.MILLISECONDS);

        long time = System.currentTimeMillis() - start;

        ServerContext.log("Game Server loaded successfully in " + (int)time + " ms.");
    }
    
    private static void help(Options options) {
        // This prints out some help for the cli
    	// And then exits
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Obsidio-Server", options);
        System.exit(0);
    }

    /**
     * Main method
     * @param args The arguments  for the simulator server
     * @throws InterruptedException 
     * @throws NumberFormatException 
     */
    public static void main(String[] args) throws NumberFormatException, InterruptedException{
        Options options = new Options();
        
        ServerContext.log("Welcome to " + Constants.name + " (version " + Constants.VERSION + ")" + ".");

        options.addOption("h", "help", false, "Show help");
        options.addOption("a", "amount", true, "Set max players allowed (default: " + ServerConfiguration.getPlayerLimit() + ")");
        options.addOption("p", "port", true, "Local port to bind (default: " + ServerConfiguration.getPort() + ")");
        options.addOption("t", "turn duration", true, "turn duration seconds (default: " + ServerConfiguration.getTurnDuration() + ")");
        options.addOption("r", "round duration", true, "round duration seconds (default: " + ServerConfiguration.getRoundDuration() + ")");
        options.addOption("d", "respawn delay", true, "respawn delay (in turns) after sinking (default: " + ServerConfiguration.getRespawnDelay() + ")");
        options.addOption("m", "map", true, "Set map name or leave blank for random (default: " + ServerConfiguration.getMapName() + ")");
        options.addOption("o", "map rotation", true, "randomly rotate map every n turns, or -1 for never. Do not set to 0. (default: " + ServerConfiguration.getMapRotationPeriod() + ")");
        
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            // assign parameters from cli. all optional
            if (cmd.hasOption("h")) {
                help(options);
            }
            if (cmd.hasOption("a")) {
            	ServerConfiguration.setPlayerLimit(Integer.parseInt(cmd.getOptionValue("a")));
            }
            if (cmd.hasOption("p")) {
            	ServerConfiguration.setPort(Integer.parseInt(cmd.getOptionValue("p")));
            }
            if (cmd.hasOption("t"))
            {
            	ServerConfiguration.setTurnDuration(10 * Integer.parseInt(cmd.getOptionValue("t")));
            }
            if (cmd.hasOption("r"))
            {
            	ServerConfiguration.setRoundDuration(10 * Integer.parseInt(cmd.getOptionValue("r")));
            }
            if (cmd.hasOption("d"))
            {
            	ServerConfiguration.setRespawnDelay(Integer.parseInt(cmd.getOptionValue("d")));
            }
            if (cmd.hasOption("o"))
            {
            	ServerConfiguration.setMapRotationPeriod(Integer.parseInt(cmd.getOptionValue("o")));
            	if (ServerConfiguration.getMapRotationPeriod() == 0) {
            		help(options);
            	}
            }
            if (!cmd.hasOption("m")) { // Chooses random map if no map chosen
                try {
                	ServerConfiguration.setMapName(
                		RandomUtils.getRandomMapName(
                			Constants.mapDirectory
                		)
                	);
                    ServerContext.log("No map specified, automatically chose random map: " + ServerConfiguration.getMapName());
                } catch(NullPointerException e) {
                	ServerContext.log("Failed to find random map folder. create a folder called \"maps\" in the same directory.");
                    help(options);
                }
            }
            else {
            	ServerConfiguration.setMapName(cmd.getOptionValue("m"));
                ServerContext.log("Using user specified map:" + ServerConfiguration.getMapName());
            }

            GameServerBootstrap bootstrap = new GameServerBootstrap();
            bootstrap.startServer();

        } catch (ParseException e) {
            ServerContext.log("Failed to parse comand line properties" + e.toString());
            help(options);
        }
    }
}
