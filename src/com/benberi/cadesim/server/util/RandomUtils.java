package com.benberi.cadesim.server.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Containing some random utilities.
 */
public class RandomUtils {
    public static int randInt(int min, int max) {
        Random rand = new Random();
       return rand.nextInt((max - min) + 1) + min;
    }
    
    public static String getRandomMapName(String directory) {
    	Path currentRelativePath = Paths.get("");
        File[] mapList = currentRelativePath.resolveSibling(directory).toFile().listFiles();
        String result = "<none>";
        try {
            File randomMap = mapList[RandomUtils.randInt(0, mapList.length-1)];
        	result = randomMap.getName().substring(
        		0, randomMap.getName().lastIndexOf(".")
        	);
        } catch(NullPointerException e) {
        	throw(e);
        }
        return result;
    }
}
