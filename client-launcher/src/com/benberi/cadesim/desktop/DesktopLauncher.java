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
					super.setScreenRect(width * 2, height);
				}else {
					super.resize(width, height);	
				}
			}
		};
		config.width = windowWidth;
		config.height = windowHeight;
		String [] windowSize = getSizeProperty("user.config","user.width","user.height");
		if(windowSize[0] == null || !(windowSize[0].matches("[0-9]{3,}")) ||
				windowSize[1] == null || !(windowSize[1].matches("[0-9]{3,}"))) {
			config.width = windowWidth;
			config.height = windowHeight;
			changeSizeProperty("user.config","user.width","user.height", Integer.toString(windowWidth), Integer.toString(windowHeight));
		}else {
			config.width = Integer.parseInt(windowSize[0]);
			config.height = Integer.parseInt(windowSize[1]);
		}
		config.vSyncEnabled = false; // "
		config.title = "GC: v" + Constants.VERSION;
		config.addIcon("gclogo16.png", FileType.Internal);
		config.addIcon("gclogo32.png", FileType.Internal);
		config.addIcon("gclogo64.png", FileType.Internal);
		config.addIcon("gclogo128.png", FileType.Internal);
		new LwjglApplication(cadesim, config);
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
}