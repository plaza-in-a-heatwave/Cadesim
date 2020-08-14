package com.benberi.cadesim.server.util;

import java.util.Random;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Containing some utilities.
 */
public class Utils {
    public static int randInt(int min, int max) {
        Random rand = new Random();
       return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * print an exception's stack trace to a string.
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}