package com.benberi.cadesim.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.impl.control.ControlAreaScene;
import com.benberi.cadesim.game.scene.impl.mapeditor.MapEditorMapScene;

public class GameInputProcessor implements InputProcessor {

    private GameContext context;

    public GameInputProcessor(GameContext context) {
        this.context = context;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
    	// TODO this is garbage - but I can't pass the key any other way.
    	
    	// for LEFT/RIGHT
    	if(context.getScenes().get(0) instanceof ControlAreaScene){
            context.getControlScene().getBnavComponent().keyDown(keycode);
    	}
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
    	// TODO this is garbage - but I can't pass the key any other way.
    	
    	// for LEFT/RIGHT
    	if(context.getScenes().get(0) instanceof ControlAreaScene){
            context.getControlScene().getBnavComponent().keyUp(keycode);
    	}
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
    	// TODO this is garbage - but I can't pass the key any other way.
    	if(context.getScenes().get(0) instanceof ControlAreaScene){
            context.getControlScene().getBnavComponent().keyTyped(character);
    	}
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	for (GameScene scene : context.getScenes()) {
            if (scene.handleClick(screenX, screenY, button)) {
                break;
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (GameScene scene : context.getScenes()) {
            if (scene.handleClickRelease(screenX, screenY, button)) {
                break;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float x = Gdx.input.getDeltaX();
        float y = Gdx.input.getDeltaY();
        for (GameScene scene : context.getScenes()) {
            if (scene.handleDrag(screenX, screenY, x, y)) {
                break;
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (GameScene scene : context.getScenes()) {
            if (scene.handleMouseMove(screenX, screenY)) {
                break;
            }
        }
        return false;
    }

	@Override
	public boolean scrolled(float amountX, float amountY) {
    	if(context.getScenes().get(0) instanceof ControlAreaScene){
            context.getControlScene().getBnavComponent().scrolled(amountX,amountY);
    	}
    	
    	if(context.getScenes().get(0) instanceof MapEditorMapScene) {
    		context.getMapEditor().scrolled(amountX,amountY);
    	}
		return false;
	}
}
