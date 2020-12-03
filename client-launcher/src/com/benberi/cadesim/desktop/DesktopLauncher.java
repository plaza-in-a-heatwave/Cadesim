package com.benberi.cadesim.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.lwjgl.opengl.Display;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
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
		int windowWidth = 800; //minimum window width size
		int windowHeight = 600; //minimum window height size
		//unable to use LWJGL3 as it does not cooperate with macOS very well. 
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//since LWJGL2 has no minimum window size setter; had to create our own - causes a graphics glitch when undersized
		BlockadeSimulator cadesim = new BlockadeSimulator() {
			public void resize (int width, int height) {
				if(Display.wasResized() && Display.getWidth() < windowWidth  || Display.getHeight() < windowHeight) {
					Gdx.graphics.setWindowedMode(windowWidth,windowHeight);
				}else {
					super.resize(width, height);	
				}
			}
		};
		config.width = windowWidth;
		config.height = windowHeight;
		config.addIcon("gclogo.png", FileType.Internal);
		if(prop.getProperty("user.width") == null || !(prop.getProperty("user.width").matches("[0-9]{3,}")) ||
				prop.getProperty("user.height") == null || !(prop.getProperty("user.height").matches("[0-9]{3,}"))) {

			config.width = windowWidth;
			config.height = windowHeight;
			try {
				changeProperty("user.config","user.width", Integer.toString(windowWidth));
				changeProperty("user.config","user.height", Integer.toString(windowHeight));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			config.width = Integer.parseInt(prop.getProperty("user.width"));
			config.height = Integer.parseInt(prop.getProperty("user.height"));
		}
		//config.backgroundFPS = 20;    // bugfix high CPU
		config.vSyncEnabled = false; // "
		config.title = "GC: v" + Constants.VERSION;
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