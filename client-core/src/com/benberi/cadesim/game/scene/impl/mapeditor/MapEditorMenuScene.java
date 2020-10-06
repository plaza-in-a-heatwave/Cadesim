package com.benberi.cadesim.game.scene.impl.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.scene.impl.connect.ConnectionSceneState;
import com.benberi.cadesim.game.scene.impl.connect.TeamTypeLabel;

public class MapEditorMenuScene implements GameScene, InputProcessor {

    /**
     * The main game context
     */
    @SuppressWarnings("unused")
	private GameContext context;

    /**
     * The sprite batch renderer
     */
    private SpriteBatch batch;

    /**
     * The shape renderer
     */
    private ShapeRenderer renderer;
    
    private Stage stage;
    private Skin skin;
    private BitmapFont font;
    private TextButton loadButton;
    private TextButton saveButton;
    private TextButton newButton;
    private TextButton previousButton;
    private TextButton nextButton;
    private TextButton lobbyButton;
    private SelectBox<String> tiles;
    private Cell<?> cell;
    /**
     * The sea texture
     */
    private Texture seaTile;
    public InputMultiplexer inputMultiplexer;
//    private float initialZoom = 1.0f;
    
    public MapEditorMenuScene(GameContext context) {
        this.context = context;
        
    }

    @Override
    public void create() {
        seaTile = context.getManager().get(context.getAssetObject().sea);
        seaTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setup();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        saveButton = new TextButton("Save Map", skin);
        newButton = new TextButton("New Map", skin);
        loadButton = new TextButton("Load Map", skin);
        lobbyButton = new TextButton("Return to Lobby", skin);
        nextButton = new TextButton("Next", skin);
        previousButton = new TextButton("Previous", skin);
        font = context.getManager().get(context.getAssetObject().regularFont);
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.background = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxBackground)).getDrawable();
        selectBoxStyle.font = font;
        selectBoxStyle.fontColor = new Color(1,1,1, 1);
        selectBoxStyle.listStyle = new List.ListStyle();
        selectBoxStyle.listStyle.selection = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxListSelection)).getDrawable();
        selectBoxStyle.listStyle.selection.setLeftWidth(5);
        selectBoxStyle.listStyle.font = font;
        selectBoxStyle.listStyle.background = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxListBackground)).getDrawable();
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxStyle.background.setLeftWidth(10);
        Label tileLabel = new Label("Tile Type:", skin);
        tileLabel.setPosition(Gdx.graphics.getWidth()-159, Gdx.graphics.getHeight() - 156);
        tiles = new SelectBox<>(selectBoxStyle);
        tiles.setSize(150, 44);
        tiles.setPosition(Gdx.graphics.getWidth()-160, Gdx.graphics.getHeight() - 200);
        String[] values = new String[]{"Winds", "Whirlpool", "Rocks", "Flags"};
        tiles.setItems(values);
        Table tileTable = new Table();
        tileTable.add(previousButton).pad(3.0f);
        tileTable.add(nextButton).pad(3.0f).row();
        cell = tileTable.add().colspan(4);
        tileTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()-15);
        //menu button options
        Table table = new Table();
        table.add(newButton).pad(3.0f).row();
        table.add(loadButton).pad(3.0f).row();
        table.add(saveButton).padTop(3.0f).padBottom(40.0f).row();
        table.add(lobbyButton).pad(3.0f).row();
        stage.addActor(tiles);
        stage.addActor(tileLabel);
        stage.addActor(tileTable);
        stage.addActor(table);
        table.setPosition(Gdx.graphics.getWidth()-88, 100);
        
        tiles.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	if(tiles.getSelected() == "Winds") {
            		context.getMapEditor().setCurrentTile(context.getMapEditor().windNorth);
            	}else if(tiles.getSelected() == "Whirlpool") {
            		context.getMapEditor().setCurrentTile(context.getMapEditor().whirlNW);
            	}else if(tiles.getSelected() == "Rocks") {
            		context.getMapEditor().setCurrentTile(context.getMapEditor().smallRock);
            	}else if(tiles.getSelected() == "Flags") {
            		context.getMapEditor().setCurrentTile(context.getMapEditor().flag1);
            	}
            }
        });
        
        loadButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		System.out.println(context.getMapEditor().getCurrentTile());
        	}
        });
        
        saveButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		System.out.println("save");
        	}
        });
        
        previousButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		GameTile previous = context.getMapEditor().getPreviousTile(context.getMapEditor().getCurrentTile());
        		Image previousTile = new Image(previous.getRegion());
                cell.setActor(previousTile);
        	}
        });
        nextButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		GameTile next = context.getMapEditor().getNextTile(context.getMapEditor().getCurrentTile());
        		Image nextTile = new Image(next.getRegion());
                cell.setActor(nextTile);
        	}
        });
        
        newButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().createMap();
        	}
        });
        lobbyButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.exitMapEditor();
        	}
        });
    }
    public void setup() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(context.getInputProcessor());
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }
    @Override
    public void update(){
    }

    @Override
    public void render() {
    	stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batch.begin();
    	drawBackground();
    	batch.end();
    	stage.act();
    	stage.draw();
    }
    /**
     * Draws the menu background
     */
    private void drawBackground() {
    	Gdx.gl.glEnable(GL20.GL_BLEND);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(128 / 255f, 128 / 255f, 128 / 255f, 0.7f));
        renderer.rect(Gdx.graphics.getWidth()-175, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderer.setColor(new Color(72 / 255f, 72 / 255f, 72 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-174, 0, 1, Gdx.graphics.getHeight());

        renderer.setColor(new Color(135 / 255f, 161 / 255f, 188 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-173, 0, 1, Gdx.graphics.getHeight());

        renderer.setColor(new Color(68 / 255f, 101 / 255f, 136 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-171, 0, 1, Gdx.graphics.getHeight());
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public boolean handleDrag(float sx, float sy, float x, float y) {
        return true;
    }
    
    @Override
    public boolean handleClick(float x, float y, int button) {
        return false;
    }

    @Override
    public boolean handleMouseMove(float x, float y) {
        return false;
    }

    @Override
    public boolean handleClickRelease(float x, float y, int button) {
        return false;
    }

	public void dispose() {
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
