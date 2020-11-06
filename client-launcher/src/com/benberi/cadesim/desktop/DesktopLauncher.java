package com.benberi.cadesim.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.benberi.cadesim.BlockadeSimulator;
import com.benberi.cadesim.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		// load the properties config
		Properties prop = new Properties();
		try {
		    prop.load(new FileInputStream("user.config"));
		}
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
		
        for (String s : arg) {
            if (s.equals("--no-update")) {
				Constants.AUTO_UPDATE = false;
			}
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		BlockadeSimulator cadesim = new BlockadeSimulator();
		config.resizable = false;
		if(prop.getProperty("user.width") == null || (prop.getProperty("user.width").matches("[0-9]{3,}")) ||
				prop.getProperty("user.height") == null || (prop.getProperty("user.height").matches("[0-9]{3,}")) ||
				prop.getProperty("user.last_resolution") == null || (prop.getProperty("user.last_resolution").matches("[0-9]+"))) {
			int width = 800;
			int height = 600;
			config.width = width;
			config.height = height;
			try {
				changeProperty("user.config","user.last_resolution", "0");
				changeProperty("user.config","user.width", Integer.toString(width));
				changeProperty("user.config","user.height", Integer.toString(height));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			config.width = Integer.parseInt(prop.getProperty("user.width"));
			config.height = Integer.parseInt(prop.getProperty("user.height"));
		}
		//config.backgroundFPS = 20;    // bugfix high CPU
		config.vSyncEnabled = false; // "
		config.title = "CadeSim: v" + Constants.VERSION;
		new LwjglApplication(cadesim, config);
	}
	
    public static void changeProperty(String filename, String key, String value) throws IOException {
        Properties prop =new Properties();
        prop.load(new FileInputStream(filename));
        prop.setProperty(key, value);
        prop.store(new FileOutputStream(filename),null);
    }

    public static String getProperty(String filename, String key) throws IOException {
        Properties prop =new Properties();
        prop.load(new FileInputStream(filename));
        return prop.getProperty(key);
    }
}