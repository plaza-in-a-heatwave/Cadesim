package com.benberi.cadesim.desktop;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.benberi.cadesim.BlockadeSimulator;
import com.benberi.cadesim.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String[] url = null;
					String[] version = null;
					String line = null;
					BufferedReader sc = new BufferedReader(new FileReader("getdown.txt"));
					while((line = sc.readLine())!=null) {
						if(line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						//remove spaces
						line = line.replaceAll("\\s", "");
						//check getdown.txt for url
						if(line.startsWith("appbase=")) {
							url = line.split("=");
						}
						//check getdown.txt for version
						if(line.startsWith("version=")) {
							version = line.split("=");
						}
						if(version != null && url != null) {
							break;
						}
					}
					if(version != null && url != null) {
						String txtVersion = version[1].replaceAll("\\s+","");
						URL cadesimServer = new URL(url[1] + "version.txt");
						//read version from server
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(cadesimServer.openStream()));
						String serverVersion = reader.readLine().replaceAll("\\s+","");
						boolean updateBool = serverVersion.equals(version[1].replaceAll("\\s+",""));
						if(!updateBool) {
							Constants.SERVER_VERSION_IDENTICAL = false;
							System.out.println("Your client is out-of-date.");
							System.out.println("Current version: " + txtVersion + ", Newer version: " + serverVersion);
						}else {
							Constants.SERVER_VERSION_IDENTICAL = true;
							System.out.println("Your client is up-to-date.");
							System.out.println("Current version: " + txtVersion);
						}
						sc.close();
					}
				}
				 catch (IOException e) {
					e.printStackTrace();
					System.out.println("Unable to check server version.");
					Constants.SERVER_VERSION_IDENTICAL = true; // don't update if cannot connect to server
				}
			}
		
		});

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
			System.out.println("Automatic updates are disabled.");
		}
		else
		{
			System.out.println("Automatic updates are enabled.");
			System.out.println("Checking for updates...");
			updateThread.start();
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