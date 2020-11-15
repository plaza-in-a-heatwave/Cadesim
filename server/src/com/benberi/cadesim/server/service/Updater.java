package com.benberi.cadesim.server.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

/**
 * The Updater class contains logic for applying in-place updates.
 * - Checking
 * - Rebooting
 * - Cleaning up
 *
 * It is designed to work with deployments of single .jar files which are
 * spawned multiple times as different cadesim rooms.
 *
 * The updater performs the following steps:
 * - Check for a more recent version
 *     - if it is up to date, just restart current process
 * - Try to put down a directory as a "lock"
 *     - wait a reasonable amount of time until it succeeds, after which
 *       just restart current process
 * - If the directory lock succeeded:
 *     - copy update schema into current directory
 *     - append update schema with this server's args
 *     - start getdown and exit normally (getdown will restart it)
 *
 */
public class Updater {

    /**
     * Updater: cleanup any files left over from update at startup. Additionally, if
     * we locked the directory, unlock it.
     *
     * Usage: run just after startup.
     */
    public static void cleanupAfterRestart() {
        // add a few sec delay before doing anything to give any
        // previous instances a chance to exit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // pass
        }

        // check if the lockdir is "ours"
        boolean lastUpdateWasOurs = false;
        String idfilename = Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME + System.getProperty("file.separator")
                + Constants.AUTO_UPDATING_ID_FILE_NAME;
        String idfilecontent;
        try {
            idfilecontent = new String(Files.readAllBytes(Paths.get(idfilename)));
            if (idfilecontent.equals(String.join(" ", ServerConfiguration.getArgs()))) {
                lastUpdateWasOurs = true;
            }
        } catch (IOException e) {
            // Couldn't open idfile so assume wasn't ours (or isn't there)
        }

        // if last update was ours, clean up
        if (lastUpdateWasOurs) {

            // bugfix - prevent endless updates on startup as we've already updated
            ServerConfiguration.setcheckForUpdatesOnStartup(false);
 
            ArrayList<String> toDelete = new ArrayList<>();
            toDelete.add(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME + System.getProperty("file.separator")
                    + Constants.AUTO_UPDATING_ID_FILE_NAME);
            toDelete.add(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
            boolean deleteSucceeded = true;
            for (String s : toDelete) {
                File f = new File(s);
                if (!f.delete()) {
                    ServerContext.log("[updater] Error: couldn't delete " + f.getPath() + " on startup");
                    deleteSucceeded = false;
                }
            }

            if (!deleteSucceeded) {
                ServerContext.log("[updater] Error: delete of at least one update file failed. This may impact other local Cadesim servers.");
            }
        }
        else
        {
            // the lockfile isn't ours (or there is no lockfile), so doing nothing.
        }
    }

    /**
     * Updater: helper to clean up lock file & restart in case of error
     */
    private static void cleanupAndRestart() {
        File f = new File(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
        if (f.delete()) {
            ServerContext.log("[updater] deleted lockfile " + f.getPath());
        } else {
            ServerContext.log("[updater] couldn't delete lockfile " + f.getPath());
        }
        restartServer();
    }

    /**
     * Spawn a new server with the args that this one was called with. Exits after
     * spawning the server.
     *
     * @throws IOException
     */
    private static void restartServer() {
        // get the name of the jar file
        String jarFileName = "";
        CodeSource codeSource = GameServerBootstrap.class.getProtectionDomain().getCodeSource();
        try {
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            jarFileName = jarFile.getCanonicalPath();
            ServerContext.log("[updater] using jar file name: " + jarFileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            ServerContext.log("[updater] jar path was null. /hint/ Try exporting as a jar, select \"Extract required libraries into generated JAR\"");
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
            for (int i = 0; i < args.length; i++) {
                arglist.add(args[i]);
            }

            // restart the server by creating a new process & exiting.
            ProcessBuilder pb = new ProcessBuilder(arglist);
            ServerContext.log("[updater] calling new process with:" + String.join(" ", arglist));
            try {
                pb.start();
            } catch (IOException e) {
                ServerContext.log("[updater] failed to spawn new server process when restarting. (" + e + ")");
                System.exit(Constants.EXIT_ERROR_CANT_UPDATE);
            }
            ServerContext.log("[updater] Successfully spawned new process. Quitting this one.");
            System.exit(Constants.EXIT_SUCCESS_SCHEDULED_UPDATE);
        } else {
            ServerContext.log("[updater] failed to spawn new server process: server not running from jar.");
            System.exit(Constants.EXIT_ERROR_CANT_UPDATE);
        }
    }

    /**
     * Updater: perform automatic update.
     * May exit and/or restart the process.
     *
     * @throws InterruptedException
     * @throws IOException
     *
     * Usage: use to check for updates at a given time.
     */
    public static void update() throws InterruptedException, IOException {
        // check for updates and restart the server if we need to
        java.io.File f = new java.io.File(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
        int sleep_ms = 2000;
        int sleepTotal = 0;
        boolean fileLockSuccess = true;
        while (!f.mkdir()) {
            ServerContext.log("[updater]  Waiting to update... (" + sleepTotal + ")");
            Thread.sleep(sleep_ms);
            sleepTotal += sleep_ms;

            // exit condition so we don't endlessly loop
            if (sleepTotal >= Constants.AUTO_UPDATE_MAX_LOCK_WAIT_MS) {
                ServerContext.log(
                        "[updater] Waited too long for file lock, maybe another server crashed. Giving up and restarting instead. ("
                                + sleepTotal + ")");
                fileLockSuccess = false;
                break;
            }
        }

        if (fileLockSuccess) {
            ServerContext.log("[updater] Created lock directory (" + f.getName() + ")");

            // create id tmp file
            String idfilename = Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME + System.getProperty("file.separator")
                    + Constants.AUTO_UPDATING_ID_FILE_NAME;
            try {
                FileWriter idfile = new FileWriter(idfilename);
                idfile.write(String.join(" ", ServerConfiguration.getArgs()));
                idfile.close();
                ServerContext.log("[updater] Successfully created " + idfilename);
            } catch (IOException e) {
                ServerContext.log("[updater] Couldn't create " + idfilename + "(" + e.getMessage() + ")");
                cleanupAndRestart();
            }

            // delete digests. otherwise getdown will
            // run multiple times and get confused
            f = new File("digest.txt");
            f.delete();
            f = new File("digest2.txt");
            f.delete();

            // run getdown
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", "getdown.jar");
                Process p = pb.start();

                ServerContext.log("[updater] waiting for getdown to finish before restarting server...");
                if (p.waitFor(Constants.AUTO_UPDATE_MAX_WAIT_GETDOWN_MS, TimeUnit.MILLISECONDS)) { // blocks, times out
                    ServerContext.log("[updater] getdown finished successfully. Restarting server...");
                } else {
                    ServerContext.log("[updater] getdown didn't close in time. Maybe it crashed? Restarting server...");
                }
                restartServer();
            } catch (Exception e) {
                ServerContext.log("[updater] exception when calling getdown: " + e);
                cleanupAndRestart();
            }
        } else { // didnt lock - no cleanup needed, just restart server :)
            restartServer();
        }
        assert false : "reached invalid logic path";
    }
}
