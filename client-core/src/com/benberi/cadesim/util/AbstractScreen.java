package com.benberi.cadesim.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AbstractScreen extends ScreenAdapter {
	protected Stage stage;
	protected Viewport viewport;
	protected InputMultiplexer multiplexer;
	
	protected AbstractScreen() {
		this(new ScreenViewport());
	}
	protected AbstractScreen(Viewport viewport) {
		this.viewport = viewport;
		stage = new Stage(viewport);
		multiplexer = new InputMultiplexer();
	}
	// Subclasses must load actors in this method
	public abstract void buildStage();

	@Override
	public void render(float delta) {
		// Clear screen
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		// Calling to Stage methods
		stage.act();
		stage.draw();
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height,true);
	}

	@Override public void hide() {}
	@Override public void pause() {}
	@Override public void resume() {}
}