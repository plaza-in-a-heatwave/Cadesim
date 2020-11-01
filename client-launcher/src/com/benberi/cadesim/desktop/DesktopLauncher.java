package com.benberi.cadesim.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

		// schedule update.
		//     enabled  if not defined.
        //     enabled  if defined and == "yes".
		//     disabled if defined and != "yes".
		String updateType = prop.getProperty("autoupdate");
		if ((updateType != null) && (!updateType.equalsIgnoreCase("yes")))
		{
			System.out.println("Automatic updates are disabled in user.config.");
		}
		else {
			// check for updates each run, unless a flag is passed.
	        // getdown will set this for example
	        boolean autoupdate = true; // by default
	        for (String s : arg) {
	            if (s.equals("--no-update")) {
	                autoupdate = false;
	            }
	        }
	        if (!autoupdate) {
	            System.out.println("Automatic updates are disabled by CLI.");
	        }
	        else
	        {
	            System.out.println("Automatic updates are enabled. Checking for updates...");
                try {
                    System.out.println("Performing update; deleting digest files...");
                    new File("digest.txt").delete();
                    new File("digest2.txt").delete();
                    System.out.println("Performing update; closing client and running getdown...");
                    new ProcessBuilder("java", "-jar", "getdown.jar").start();
                    System.exit(0);
                }catch(Exception e){
                    System.out.println("Unable to start getdown.jar; run manually. Please delete digest files and re-run. " + e);
                }
	        }
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		BlockadeSimulator cadesim = new BlockadeSimulator();

		config.resizable = false;
		config.width = Integer.parseInt(prop.getProperty("user.width"));
		config.height = Integer.parseInt(prop.getProperty("user.height"));
		//config.backgroundFPS = 20;    // bugfix high CPU
		config.vSyncEnabled = false; // "
		config.title = "CadeSim: v" + Constants.VERSION;
		new LwjglApplication(cadesim, config);
	}
}