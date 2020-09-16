package com.benberi.cadesim.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.util.ArrayList;

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
    public Updater(ServerContext context) {
    }

    /**
     * Updater: cleanup any files left over from update at startup. Additionally, if
     * we locked the directory, unlock it.
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
            // pass, couldn't open idfile so assume wasn't ours
        }

        // if last update was ours, clean up
        if (lastUpdateWasOurs) {
            ArrayList<String> toDelete = new ArrayList<>();
            toDelete.add("getdown.txt");
            toDelete.add("version.txt");
            toDelete.add("digest.txt");
            toDelete.add("digest2.txt");
            toDelete.add(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME + System.getProperty("file.separator")
                    + Constants.AUTO_UPDATING_ID_FILE_NAME);
            toDelete.add(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
            boolean deleteSucceeded = true;
            for (String s : toDelete) {
                File f = new File(s);
                if (f.delete()) {
                    ServerContext.log("deleted " + f.getPath() + " on startup.");
                } else {
                    ServerContext.log("couldn't delete " + f.getPath() + " on startup");
                    deleteSucceeded = false;
                }
            }

            if (!deleteSucceeded) {
                ServerContext.log("Error: delete of at least one update file failed.");
            }
        }
    }

    /**
     * Updater: Thread to check for updates automatically
     */
    Thread updateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                BufferedReader sc = new BufferedReader(new FileReader("getdown.txt"));
                sc.readLine();
                // get server url from getDown.txt
                String cadesimUrl = sc.readLine();
                String[] url = cadesimUrl.split("=");
                // read version from getDown.txt
                String cadeSimVersion = sc.readLine();
                String[] version = cadeSimVersion.split("=");
                String txtVersion = version[1].replaceAll("\\s+", "");
                URL cadesimServer = new URL(url[1] + "version.txt");
                // read version from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(cadesimServer.openStream()));
                String serverVersion = reader.readLine().replaceAll("\\s+", "");
                isUpdateAvailable = !serverVersion.equals(txtVersion);
                System.out.println("Finished checking server version.");
                sc.close();
                checkingForUpdate = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    });

    /**
     * Updater: shared variables used by the updater thread
     */
    private boolean checkingForUpdate;
    private boolean isUpdateAvailable = false;

    /**
     * Updater: runs update thread and blocks until result.
     */
    private boolean isUpdateAvailable() {
        checkingForUpdate = true;
        updateThread.start();
        while (checkingForUpdate) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ServerContext.log(
                        "server was checking for update, but the thread was interrupted. (" + e.getMessage() + ")");
            }
        }

        // updated by thread
        return isUpdateAvailable;
    }

    /**
     * Updater: helper to clean up lock file & restart in case of error
     */
    void cleanupAndRestart() {
        File f = new File(Constants.AUTO_UPDATING_LOCK_DIRECTORY_NAME);
        if (f.delete()) {
            ServerContext.log("deleted lockfile " + f.getPath());
        } else {
            ServerContext.log("couldn't delete lockfile " + f.getPath());
        }
        restartServer();
    }

    /**
     * Spawn a new server with the args that this one was called with. Exits after
     * spawning the server.
     *
     * @throws IOException
     */
    private void restartServer() {
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
            for (int i = 0; i < args.length; i++) {
                arglist.add(args[i]);
            }

            // restart the server by creating a new process & exiting.
            ProcessBuilder pb = new ProcessBuilder(arglist);
            try {
                pb.start();
            } catch (IOException e) {
                ServerContext.log("failed to spawn new server process when restarting. (" + e + ")");
                System.exit(Constants.EXIT_ERROR_CANT_UPDATE);
            }
            System.exit(Constants.EXIT_SUCCESS_SCHEDULED_UPDATE);
        } else {
            ServerContext.log("failed to spawn new server process: server not running from jar.");
            System.exit(Constants.EXIT_ERROR_CANT_UPDATE);
        }
    }

    /**
     * Updater: perform automatic update
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void update() throws InterruptedException, IOException {
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
                ServerContext.log(
                        "UPDATER: Waited too long for file lock, maybe another server crashed. Giving up and restarting instead. ("
                                + sleepTotal + ")");
                fileLockSuccess = false;
                break;
            }
        }

        if (fileLockSuccess && isUpdateAvailable()) {
            ServerContext.log("UPDATER: Created lock directory (" + f.getName() + ")");

            // create id tmp file
            String idfilename = Constants.AUTO_UPDATE_MAX_LOCK_WAIT_MS + System.getProperty("file.separator")
                    + Constants.AUTO_UPDATING_ID_FILE_NAME;
            try {
                FileWriter idfile = new FileWriter(idfilename);
                idfile.write(String.join(" ", ServerConfiguration.getArgs()));
                idfile.close();
                ServerContext.log("Successfully created " + idfilename);
            } catch (IOException e) {
                ServerContext.log("Couldn't create " + idfilename + "(" + e.getMessage() + ")");

                // failed; cleanup
                this.cleanupAndRestart();
            }

            // copy getdown.txt.server -> getdown.txt
            try {
                Files.copy(Paths.get("getdown.txt.server"), Paths.get("getdown.txt"),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                ServerContext.log("Couldn't copy getdown.txt.server to getdown.txt" + "(" + e.getMessage() + ")");

                cleanupAndRestart();
            }

            // append apparg lines to getdown.txt
            StringBuilder sb = new StringBuilder();
            String[] args = ServerConfiguration.getArgs();
            sb.append("\n\n"); // pad
            for (int i = 0; i < args.length; i++) {
                sb.append("apparg = " + i + "\n");
            }
            try {
                Files.write(Paths.get("getdown.txt"), sb.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                ServerContext.log("Couldn't append appargs to getdown.txt" + "(" + e.getMessage() + ")");

                cleanupAndRestart();
            }

            try {
                ServerContext.log("Performing update, deleting files...");
                // delete required files in order to update client
                File digest1 = new File("digest.txt");
                File digest2 = new File("digest2.txt");
                File version = new File("version.txt");
                digest1.delete();
                digest2.delete();
                version.delete();
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", "getdown.jar");
                pb.start(); // assign to process for something in future

                ServerContext.log("quitting Server, to be restarted by getdown...");
                System.exit(Constants.EXIT_SUCCESS_SCHEDULED_UPDATE);
            } catch (Exception e) {
                ServerContext.log("exception when calling getdown: " + e);

                cleanupAndRestart();
            }
        } else { // didnt lock
            restartServer();
        }
        assert false : "reached invalid logic path";
    }
}
