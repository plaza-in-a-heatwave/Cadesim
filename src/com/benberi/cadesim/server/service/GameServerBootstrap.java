package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.CadeServer;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

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
     * Server's configuration
     */
    private ServerConfiguration config;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The start time of the server
     */
    private long start;
    public GameServerBootstrap(ServerConfiguration cfg) {
        this.config = cfg;
        context = new ServerContext(cfg);
    }

    /**
     * Start the server
     * @throws InterruptedException
     */
    private void startServer() throws InterruptedException {
        start = System.currentTimeMillis();

        ServerContext.log("Starting the Game Server, configuration: " + config.toString());
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

        ServerContext.log("Game Server loaded successfully in " + (int) (time / 1000) + " seconds.");
    }

    /**
     * Main method
     * @param args The arguments  for the simulator server
     * @throws InterruptedException 
     * @throws NumberFormatException 
     */
    public static void main(String[] args) throws NumberFormatException, InterruptedException{

        new Cli(args).parse();

    }

    public static void initiateServerStart(
    		int amount,
    		String mapName,
    		int port,
    		int turnDuration,
    		int roundTime,
    		int respawnDelay
    ) throws InterruptedException {

        ServerConfiguration config = new ServerConfiguration();
        config.setPlayerLimit(amount);
        config.setMapType(0);
        config.setPort(port);
        config.setMapName(mapName);
        ServerConfiguration.setTurnDuration(turnDuration);
        ServerConfiguration.setRoundDuration(roundTime);
        config.setRespawnDelay(respawnDelay);

        GameServerBootstrap bootstrap = new GameServerBootstrap(config);
        bootstrap.startServer();
    }
}
