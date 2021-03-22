package com.benberi.cadesim.util;

import com.badlogic.gdx.Gdx;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.GameTile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class RandomUtils {

    public static String readStringFromFile(String path) {
        File file = Gdx.files.internal(path).file();
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    
    /*
     * Verify if url is valid or not based on string representation
     * @param url - string version of url address
     * @return boolean - is valid or not
     */
    public static boolean validUrl (String urlStr) {
    	try {
    		InetAddress address = InetAddress.getByName(urlStr); 
    		if(validIP(address.getHostAddress())) {
    			return true;
    		}
    		else {
    			return false;
    		}
		} catch(UnknownHostException e) {
			return false;
		}
    }
    
    /*
     * Verify if ip is valid or not based on string representation
     * @param ip - string version of ip address
     * @return boolean - is valid or not
     */
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    /*
     * Utility method to check if object is instance of any of classes that extend GameTile
     * @param obj - object to check
     * @param cls - vararg classes to verify with
     */
    @SafeVarargs
	public static boolean instanceOf(Object obj, Class<? extends GameTile>... cls) {
        for (Class<?> type : cls) {
        	if (type.isInstance(obj)) {
        		return true;
        	}
        }
        return false;
    }

}
