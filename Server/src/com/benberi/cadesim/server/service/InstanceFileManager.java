package com.benberi.cadesim.server.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

/**
 * This class handles the instance files which are dropped on disk when running.
 *
 *  Each server puts down a unique file indicating it's alive e.g. .CADESIM_INSTANCE_
 *  Servers remove these files when shutting down cleanly.
 *  A shutdown script places a .STOP file in the directory
 *  On seeing .STOP (e.g. polling every few seconds), each server deletes its file & shuts down
 *  The script waits up to n seconds for all INSTANCE files to disappear.
 */
public class InstanceFileManager {
    static long lastStopfileCheck = System.currentTimeMillis();
    static boolean handleStopFileFirstRun = false;

    /**
     * Gets a filename for the instance based on the args hash.
     *
     * @return the filename, or null if couldn't calculate it.
     */
    private static String getInstanceFileName() {
        String s = null;
        try {
            // make a filename based on a base64 hash of the args
            s = Constants.INSTANCE_FILENAME_PREFIX + new String(
                Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("MD5").digest(
                        String.join(
                                " ", ServerConfiguration.getArgs()
                        ).getBytes("UTF-8")
                    )
                )
            );

            // translate to a-zA-Z0-9 to avoid upsetting filesystems.
            s = s.replace("/", "s");
            s = s.replace("+", "p");
            s = s.replace("=", "e");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            ServerContext.log("WARNING: " + e.toString() + " (name generation failed) the stop script may fail to stop this server...");
        }
        return s;
    }

    public static void placeInstanceFile() {
        try {
            new File(getInstanceFileName()).createNewFile();

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
              public void run()
              {
                  try {
                      new File(getInstanceFileName()).delete();
                  }
                  catch (Exception e) {
                      // pass. no big deal if can't delete the file
                  }
              }
            });
        } catch (IOException | SecurityException | NullPointerException e) {
            ServerContext.log("WARNING: " + e.toString() + " (instance file creation) the stop script may fail to stop this server...");
        }
    }

    public static void handleStopFilePresent() {
        // put instance file once and only once
        if (!handleStopFileFirstRun) {
            handleStopFileFirstRun = true;
            placeInstanceFile();
            ServerContext.log("Placing/clobbering instance file: " + getInstanceFileName());
        }

        // poll for stopfile and quit if exists
        long now = System.currentTimeMillis();
        if (now - lastStopfileCheck >= Constants.SERVER_STOPFILE_CHECK_MILLIS)
        {
            lastStopfileCheck = now;
            boolean present = new File(Constants.STOP_FILENAME).isFile();
            if (present) {
                ServerContext.log("Found stopfile: " + Constants.STOP_FILENAME + ", quitting...");
                System.exit(Constants.EXIT_SUCCESS_SHUTDOWN_BY_STOPFILE);
            }
        }
    }
}
