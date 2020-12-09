package com.benberi.cadesim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.game.scene.GameScene;



public class BlockadeSimulator extends ApplicationAdapter{

	/**
	 * The game context
	 */
	private GameContext context;

	@Override
	public void create () {
		context = new GameContext(this);
		context.create();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// always tick packet queue
		context.getPacketHandler().tickQueue();

		if (!context.isConnected()) {
		    // Just update lobby
			context.getConnectScene().update();
			context.getConnectScene().render();
		}
		else
		{
	        // Render and update all scenes
	        for (GameScene scene : context.getScenes()) {
	            scene.update();
	            scene.render();
	        }
		}
		// render only for map editor
		if(context.isInMapEditor()) {
//			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	        for (GameScene scene : context.getScenes()) {
	            scene.update();
	            scene.render();
	        }
		}

	}
	
	@Override
	public void dispose () {
	}
	
	public void setScreenRect (int width, int height){
        context.getConnectScene().screenWidth = width;
        context.getConnectScene().screenHeight = height;
	}
	
	@Override
	public void resize (int width, int height) {
		if(!context.isConnected() &&  !context.isStartedMapEditor()) {
			if(context.gameStage == null) {
				context.gameStage = new Stage(new FitViewport(width, height));
			}
			context.gameStage.getViewport().update(width, height, true);
			context.getConnectScene().stage = new Stage(new FitViewport(width, height));
			context.getConnectScene().stage.getViewport().update(width, height, true);
			context.getConnectScene().setActorPositions(width);
			setScreenRect(width,height);
			context.getConnectScene().addStage();
			context.getConnectScene().setup();
		}
		else {
			Gdx.graphics.setResizable(false);
		}
	}
}
