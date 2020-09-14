package com.benberi.cadesim.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.player.PlayerManager;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {

    /**
     * Updater: Thread to check for updates automatically
     */
    Thread updateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                BufferedReader sc = new BufferedReader(new FileReader("getdown.txt"));
                sc.readLine();
                //get server url from getDown.txt
                String cadesimUrl = sc.readLine();
                String[] url = cadesimUrl.split("=");
                //read version from getDown.txt
                String cadeSimVersion = sc.readLine();
                String[] version = cadeSimVersion.split("=");
                String txtVersion = version[1].replaceAll("\\s+","");
                URL cadesimServer = new URL(url[1] + "version.txt");
                //read version from server
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(cadesimServer.openStream()));
                String serverVersion = reader.readLine().replaceAll("\\s+","");
                isUpdateAvailable = !serverVersion.equals(txtVersion);
                System.out.println("Finished checking server version.");
                sc.close();
                checkingForUpdate = false;
            }
             catch (IOException e) {
                e.printStackTrace();
            }
        }

    });

    /**
     * Updater: shared variables used by the updater thread
     */
    public boolean checkingForUpdate;
    public boolean isUpdateAvailable = false;

    /**
     * Updater: runs update thread and blocks until result.
     */
    public boolean isUpdateAvailable() {
        checkingForUpdate = true;
        updateThread.start();
        while (checkingForUpdate) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ServerContext.log("server was checking for update, but the thread was interrupted. (" + e.getMessage() + ")");
            }
        }

        // updated by thread
        return isUpdateAvailable;
    }

    /**
     * Updater: perform automatic update
     * @throws InterruptedException
     * @throws IOException
     */
    public void doAutomaticUpdateAndExit() throws InterruptedException, IOException {
        // check for updates and restart the server if we need to
        java.io.File f = new java.io.File(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
        int sleep_ms = 2000;
        int sleepTotal = 0;
        boolean fileLockSuccess = true;
        while (!f.mkdir()) {
            ServerContext.log("UPDATER: Waiting to update... (" + sleepTotal + ")");
            Thread.sleep(sleep_ms);
            sleepTotal += sleep_ms;

            // exit condition so we don't endlessly loop
            if (sleepTotal >= Constants.AUTO_UPDATE_MAX_LOCK_WAIT_MS) {
                ServerContext.log("UPDATER: Waited too long for file lock, maybe another server crashed. Giving up and restarting instead. (" + sleepTotal + ")");
                fileLockSuccess = false;
                break;
            }
        }

        if (fileLockSuccess && isUpdateAvailable()) {
            ServerContext.log("UPDATER: Created lock directory (" + f.getName() + ")");

            // TODO #69 fill in lock directory details

            try {
                ServerContext.log("Performing update, deleting files...");
                //delete required files in order to update client
                File digest1 = new File("digest.txt");
                File digest2 = new File("digest2.txt");
                File version = new File("version.txt");
                digest1.delete();
                digest2.delete();
                version.delete();
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", "getdown.jar");
                Process p = pb.start(); //assign to process for something in future
                System.exit(Constants.EXIT_SUCCESS_SCHEDULED_UPDATE);
            }catch(Exception e){System.out.println(e);}
        }
        else {

            // get the name of the jar file
            String jarFileName = "";
            CodeSource codeSource = GameServerBootstrap.class.getProtectionDomain().getCodeSource();
            try {
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                jarFileName = jarFile.getCanonicalPath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // only restart if it's a jar (e.g. prevent IDE)
            if (jarFileName.endsWith(".jar")) {
                ArrayList<String> arglist = new ArrayList<String>();

                // start with java -jar jarfile.jar
                arglist.add("java");
                arglist.add("-jar");
                arglist.add(jarFileName);

                // add the cadesim server args
                String args[] = ServerConfiguration.getArgs();
                for (int i=0; i<args.length; i++) {
                    arglist.add(args[i]);
                }

                // restart the server by creating a new process.
                ProcessBuilder pb = new ProcessBuilder(arglist);
                Process p = pb.start();
            }
        }

        System.exit(Constants.EXIT_SUCCESS_SCHEDULED_UPDATE);
    }

    /**
     * The server context
     */
    private ServerContext context;
    private PlayerManager playerManager; // this called so often, should cache

    /**
     * Keep track of how many games we've played
     * And when we last rotated the map
     */
    private int gamesCompleted = 0;
    private int lastMapRotation = 0;

    public GameService(ServerContext context) {
        this.context = context;
        this.playerManager = context.getPlayerManager();
    }

    /**
     * helper method to randomly rotate the map
     */
    private void randomRotateMap() {
        // rotate the current one
    	ServerConfiguration.setMapName(
            ServerConfiguration.getNextMapName()
    	);

        // generate the next one
        ServerConfiguration.pregenerateNextMapName();
        ServerContext.log("pre-generated the next map name in rotation: " + ServerConfiguration.getNextMapName());

        // renew it
    	context.renewMap();
    }

    @Override
    public void run() {
        try {
            context.getPackets().queuePackets();
            playerManager.tick();
            playerManager.queueOutgoing();
            context.getTimeMachine().tick();

            if(playerManager.isGameEnded()) {
            	// print out the scores for the game
            	playerManager.serverBroadcastMessage(
            			"Round ended, final scores were:\n" +
            			"    Defender:" + playerManager.getPointsDefender() + "\n" + 
            			"    Attacker:" + playerManager.getPointsAttacker()
            	);

            	ServerContext.log("Ending game #" + Integer.toString(gamesCompleted) + ".");
            	gamesCompleted++;

                // handle switching maps.
                String oldMap = ServerConfiguration.getMapName();
                if (playerManager.shouldSwitchMap())
                {
                	randomRotateMap();
                    lastMapRotation = gamesCompleted;
                	ServerContext.log(
                		"Players voted to switch map; rotated map to: " +
                		ServerConfiguration.getMapName()
                	);
                }
                else if (playerManager.shouldRestartMap())
                {
                    ServerContext.log(
                        "Players voted to restart map; keeping map: " +
                        ServerConfiguration.getMapName()
                    );
                }
                else if (!ServerConfiguration.getRunContinuousMode())
                {
                    // it would be cruel to exit early if players voted for a restart/nextmap
                    ServerContext.log("Not in run-continuous mode, so quitting early.");
                    System.exit(Constants.EXIT_SUCCESS);
                }
                else if (playerManager.isUpdateScheduledAfterGame()) {
                    doAutomaticUpdateAndExit();
                }
                else if (
                        (ServerConfiguration.getMapRotationPeriod() > 0) && // -1 == don't rotate, 0 invalid
                        ((gamesCompleted - lastMapRotation) >= ServerConfiguration.getMapRotationPeriod())
                ) {
                    lastMapRotation = gamesCompleted;

                    randomRotateMap();

                	ServerContext.log(
                		"Rotated map after " +
                		Integer.toString(ServerConfiguration.getMapRotationPeriod()) +
                		" games, automatically chose random map: " +
                		ServerConfiguration.getMapName()
                	);
                }
                
                // message if map changed
                String newMap = ServerConfiguration.getMapName();
                if (!newMap.contentEquals(oldMap))
                {
                	playerManager.serverBroadcastMessage("Changed map to " + newMap);
                }

                // complete the game refresh
                playerManager.renewGame();
                context.getTimeMachine().renewRound(); // bugfix - order matters

                playerManager.serverBroadcastMessage("Started new round: #" + (gamesCompleted + 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ServerContext.log(e.getMessage());
        }
    }
}
