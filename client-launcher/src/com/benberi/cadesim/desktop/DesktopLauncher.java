package com.benberi.cadesim.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.benberi.cadesim.BlockadeSimulator;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.util.ScreenManager;
import com.kotcrab.vis.ui.widget.file.FileChooser;

public class DesktopLauncher {
    private static Random random = new Random();
    private static String servers = "";
    
	public static void main (String[] arg) {
		new SplashScreen();
        for (String s : arg) {
            if (s.equals("--no-update")) {
				Constants.AUTO_UPDATE = false;
			}
		}
		int windowWidth = 800; //minimum window width size
		int windowHeight = 600; //minimum window height size
        Constants.USERPROPERTIES = getUserProperties();
        getSettings();
        splitRoomInfo();
        FileChooser.setDefaultPrefsName("com.cadesim.mapeditor.filechooser");
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowSizeLimits(windowWidth, windowHeight, 99999, 99999);
		BlockadeSimulator cadesim = new BlockadeSimulator();
		ScreenManager.getInstance().initialize(cadesim);
		config.setWindowedMode(windowWidth, windowHeight);
		String [] windowSize = getSizeProperty("user.config","user.width","user.height");
		if(windowSize[0] == null || !(windowSize[0].matches("[0-9]{3,}")) ||
				windowSize[1] == null || !(windowSize[1].matches("[0-9]{3,}"))) {
			config.setWindowedMode(windowWidth, windowHeight);
			changeSizeProperty("user.config","user.width","user.height", Integer.toString(windowWidth), Integer.toString(windowHeight));
		}else {
			config.setWindowedMode(Integer.parseInt(windowSize[0]), Integer.parseInt(windowSize[1]));
		}
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setTitle("GC: v" + Constants.VERSION);
		config.setWindowIcon("gclogo.png");
		new Lwjgl3Application(cadesim,config);
	}
	
    public static void changeSizeProperty(String filename, String width, String value, String height, String otherValue){
        Properties prop =new Properties();
        try {
            prop.load(new FileInputStream(filename));
            prop.setProperty(width, value);
            prop.setProperty(height, otherValue);
            prop.store(new FileOutputStream(filename),null);
		}
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
    }

    public static String[] getSizeProperty(String filename, String width, String height){
        try {
            Properties prop =new Properties();
            prop.load(new FileInputStream(filename));
            return new String[] {prop.getProperty(width),prop.getProperty(height)};
		}
		catch (FileNotFoundException e) {
		    return new String[] {"800","600"};
		}
		catch (IOException e) {
		    return new String[] {"800","600"};
		}
    }
    
    public static String getProperty(String filename, String property){
        try {
            Properties prop =new Properties();
            prop.load(new FileInputStream(filename));
            return prop.getProperty("property");
		}
		catch (FileNotFoundException e) {
		    return null;
		}
		catch (IOException e) {
		    return null;
		}
    }
    
    public static HashMap<String,String> getUserProperties(){
        Properties prop =new Properties();
        try {
			prop.load(new FileInputStream("user.config"));
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.accountname", prop.getProperty("user.accountname"));
					put("user.username", prop.getProperty("user.username"));
					put("user.last_room_index", prop.getProperty("user.last_room_index"));
					put("user.last_ship", prop.getProperty("user.last_ship"));
					put("user.last_team", prop.getProperty("user.last_team"));
					put("user.width", prop.getProperty("user.width"));
					put("user.height", prop.getProperty("user.height"));
					put("user.volume", prop.getProperty("user.volume"));
					put("autoupdate", prop.getProperty("autoupdate"));
					put("url", prop.getProperty("url"));
					put("servers", prop.getProperty("servers"));
			}};
		}catch (FileNotFoundException e) {
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.accountname", "");
					put("user.username", "User"+Integer.toString(random.nextInt(9999)));
					put("user.last_room_index", "0");
					put("user.last_team", "0");
					put("user.last_ship", prop.getProperty("user.last_ship"));
					put("user.width", "800");
					put("user.height", "600");
					put("user.volume", "0.15");
					put("autoupdate", "yes");
					put("url", prop.getProperty("url"));
					put("servers", prop.getProperty("servers"));
			}};
		} catch (IOException e) {
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.accountname", "");
					put("user.username", "User"+Integer.toString(random.nextInt(9999)));
					put("user.last_room_index", "0");
					put("user.last_team", "0");
					put("user.last_ship", "0");
					put("user.width", "800");
					put("user.height", "600");
					put("user.last_ship", "11");
					put("user.volume", "0.15");
					put("autoupdate", "yes");
					put("url", prop.getProperty("url"));
					put("servers", prop.getProperty("servers"));
			}};
		}
    }

    public static void getSettings() {
    	try {
    		String line = null;
    		Scanner sc = new Scanner(new URL(Constants.USERPROPERTIES.get("url")).openStream());
	        while((line = sc.nextLine())!=null) {	
				if(line.isEmpty() || line.startsWith("#")) {	
					continue;	
				}	
				if(line.startsWith("server=")) {
					servers = line.split("=")[1];
					sc.close();
					break;
				}
	        }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void splitRoomInfo() {
		//Split info for each room (Port:Server Code)
    	
		String[] keyValuePairs = servers.split(",");
		for(String pair : keyValuePairs)                        //iterate over the pairs
		{
		    String[] entry = pair.split(":");                   //split the pairs to get key and value 
		    Constants.SERVER_ROOMS.put(Integer.parseInt(entry[0].trim()), entry[1].trim());          //add them to the hashmap and trim whitespaces
		}
	
    }
}