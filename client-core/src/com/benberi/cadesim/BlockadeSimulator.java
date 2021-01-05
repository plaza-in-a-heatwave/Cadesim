package com.benberi.cadesim;

import java.util.Timer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;

public class BlockadeSimulator extends Game{

	/**
	 * The game context
	 */
	private GameContext context;

	@Override
	public void create () {
		context = new GameContext(this);
		VisUI.load();
        FileChooser.setDefaultPrefsName("com.cadesim.mapeditor.filechooser");
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, context);
	}
	
	@Override
	public void dispose () {
		try {
			context.getService().shutdownNow();
			context.getConnectTask().dispose();
		}catch(Exception e) {
			//throws null pointer if not connected; but cleanup anyway if user logs in then returns to lobby
		}
		Gdx.app.exit();
    	Timer t = new java.util.Timer(); //just incase system doesn't exit
    	t.schedule( 
    	        new java.util.TimerTask() {
    	            @Override
    	            public void run() {
    	            	System.exit(-1);
    	            }
    	        }, 
    	        1000 
    	);	
	}
	@Override
	public void render() {
		super.render();
		context.getPacketHandler().tickQueue();
	}
}
