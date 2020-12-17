package com.benberi.cadesim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;

public class BlockadeSimulator extends Game{

	/**
	 * The game context
	 */
	private GameContext context;

	@Override
	public void create () {
		context = new GameContext(this);
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, context);
	}
	
	@Override
	public void dispose () {
		try {
			context.getService().shutdownNow();
			context.getConnectTask().dispose();
		}catch(NullPointerException e) {
			//throws null pointer if not connected; but cleanup anyway if user logs in then returns to lobby
		}
		Gdx.app.exit();
	}
	@Override
	public void render() {
		super.render();
		context.getPacketHandler().tickQueue();
	}
}
