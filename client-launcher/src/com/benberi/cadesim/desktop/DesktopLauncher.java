package com.benberi.cadesim.desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowSizeLimits(windowWidth, windowHeight, 99999, 99999);
		BlockadeSimulator cadesim = new BlockadeSimulator();
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
//		if(System.getProperty("os.name").toLowerCase().contains("mac")) {
//			ShaderProgram.prependVertexCode = "#version 110\n#define varying out\n#define attribute in\n";
//			ShaderProgram.prependFragmentCode = "#version 110\n#define varying in\n#define texture2D texture\n#define gl_FragColor fragColor\nout vec4 fragColor;\n";
//		}
        new Lwjgl3Application(cadesim, config);
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