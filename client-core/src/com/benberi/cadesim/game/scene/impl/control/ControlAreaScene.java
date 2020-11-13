package com.benberi.cadesim.game.scene.impl.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.VesselMoveType;
import com.benberi.cadesim.game.scene.GameScene;

public class ControlAreaScene implements GameScene {

    private GameContext context;
    
    public static int shipId = 0;

    private BattleControlComponent control;
    private ShapeRenderer shapeRenderer;

    public ControlAreaScene(GameContext context) {
        this.context = context;
    }

    @Override
    public void create()
    {
    	// make a temporary vessel to checkout properties
    	// TODO make these properties static instead
    	Vessel v = Vessel.createVesselByType(context, null, 0, 0, context.myVesselType);
        shapeRenderer = new ShapeRenderer();
        this.setControl(new BattleControlComponent(
        	context,
        	this,
        	v.getMoveType() != VesselMoveType.FOUR_MOVES, // is it a big ship
        	v.isDoubleShot()                              // single or double shot
        ));

        getControl().create();
    }

    @Override
    public void update() {
        getControl().update();
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderBackground();
        getControl().update();
        getControl().render();
    }
    
	// reset controls.
	public void reset() {
		control.reset();
	}
	
    @Override
    public void dispose() {
        getControl().dispose();
    }

    @Override
    public boolean handleDrag(float sx, float sy, float x, float y) {
        if (getControl().handleDrag(sx, sy, x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleClick(float x, float y, int button) {
        if (getControl().handleClick(x, y, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMouseMove(float x, float y) {
        if (getControl().handleMouseMove(x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleClickRelease(float x, float y, int button) {
        if (getControl().handleRelease(x, y, button)) {
            return true;
        }
        return false;
    }

    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(48 / 255f, 98 / 255f, 123 / 255f, 1));
        //shapeRenderer.setColor(new Color(65 / 255f, 101 / 255f, 139 / 255f, 1));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), 200);

        shapeRenderer.setColor(new Color(72 / 255f, 72 / 255f, 72 / 255f, 1));
        shapeRenderer.rect(0, 199, Gdx.graphics.getWidth(), 1);

        shapeRenderer.setColor(new Color(135 / 255f, 161 / 255f, 188 / 255f, 1));
        shapeRenderer.rect(0, 198, Gdx.graphics.getWidth(), 1);

        shapeRenderer.setColor(new Color(68 / 255f, 101 / 255f, 136 / 255f, 1));
        shapeRenderer.rect(0, 197, Gdx.graphics.getWidth(), 1);
        shapeRenderer.end();
    }

    public BattleControlComponent getBnavComponent() {
        return getControl();
    }

	public BattleControlComponent getControl() {
		return control;
	}

	public void setControl(BattleControlComponent control) {
		this.control = control;
	}
}
