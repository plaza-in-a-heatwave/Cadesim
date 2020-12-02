package com.benberi.cadesim.game.scene.impl.mapeditor;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.GameScene;

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
    private TextButton loadButton;
    private TextButton helpButton;
    private TextButton saveButton;
    private TextButton newButton;
    private TextButton lobbyButton;
    
    private TextButton addWhirlButton;
    
    private ImageButton windNButton;
    private ImageButtonStyle windNStyle;
    private TextureRegionDrawable windNregularDrawable;
    private TextureRegionDrawable windNhoverDrawable;
    
    private ImageButton windSButton;
    private ImageButtonStyle windSStyle;
    private TextureRegionDrawable windSregularDrawable;
    private TextureRegionDrawable windShoverDrawable;
    
    private ImageButton windEButton;
    private ImageButtonStyle windEStyle;
    private TextureRegionDrawable windEregularDrawable;
    private TextureRegionDrawable windEhoverDrawable;
    
    private ImageButton windWButton;
    private ImageButtonStyle windWStyle;
    private TextureRegionDrawable windWregularDrawable;
    private TextureRegionDrawable windWhoverDrawable;
    
    private ImageButton whirlNWButton;
    private ImageButtonStyle whirlNWStyle;
    private TextureRegionDrawable whirlNWregularDrawable;
    private TextureRegionDrawable whirlNWhoverDrawable;
    
    private ImageButton whirlNEButton;
    private ImageButtonStyle whirlNEStyle;
    private TextureRegionDrawable whirlNEregularDrawable;
    private TextureRegionDrawable whirlNEhoverDrawable;
    
    private ImageButton whirlSWButton;
    private ImageButtonStyle whirlSWStyle;
    private TextureRegionDrawable whirlSWregularDrawable;
    private TextureRegionDrawable whirlSWhoverDrawable;
    
    private ImageButton whirlSEButton;
    private ImageButtonStyle whirlSEStyle;
    private TextureRegionDrawable whirlSEregularDrawable;
    private TextureRegionDrawable whirlSEhoverDrawable;
    
    private ImageButton smallRockButton;
    private ImageButtonStyle smallRockStyle;
    private TextureRegionDrawable smallRockregularDrawable;
    private TextureRegionDrawable smallRockhoverDrawable;  
    
    private ImageButton bigRockButton;
    private ImageButtonStyle bigRockStyle;
    private TextureRegionDrawable bigRockregularDrawable;
    private TextureRegionDrawable bigRockhoverDrawable;
    
    private ImageButton flag1Button;
    private ImageButtonStyle flag1Style;
    private TextureRegionDrawable flag1regularDrawable;
    private TextureRegionDrawable flag1hoverDrawable; 
    
    private ImageButton flag2Button;
    private ImageButtonStyle flag2Style;
    private TextureRegionDrawable flag2regularDrawable;
    private TextureRegionDrawable flag2hoverDrawable;  
    
    private ImageButton flag3Button;
    private ImageButtonStyle flag3Style;
    private TextureRegionDrawable flag3regularDrawable;
    private TextureRegionDrawable flag3hoverDrawable;  
    
    private ArrayList<Button> buttonList = new ArrayList<Button>();
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

        initButtons();
        addWhirlButton = new TextButton("Add WhirlPool", skin);
        addWhirlButton.getStyle().disabled = addWhirlButton.getStyle().down;
        Button[] buttonArray = {windNButton,windSButton,windWButton,windEButton,
        		whirlNWButton,whirlNEButton,whirlSWButton,whirlSEButton,
        		smallRockButton,bigRockButton,
        		flag1Button,flag2Button,flag3Button,
        		addWhirlButton};
        Collections.addAll(buttonList, buttonArray);
        saveButton = new TextButton("Save Map", skin);
        newButton = new TextButton("New Map", skin);
        loadButton = new TextButton("Load Map", skin);
        helpButton = new TextButton("Help", skin);
        lobbyButton = new TextButton("Exit Editor", skin);

        context.getManager().get(context.getAssetObject().regularFont);

        Table windTable = new Table();
        windTable.add(windNButton).pad(3.0f);
        windTable.add(windSButton).pad(3.0f).row();
        windTable.add(windWButton).pad(3.0f);
        windTable.add(windEButton).pad(3.0f);
        windTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()-55);
        
        Table whirlTable = new Table();
        whirlTable.add(whirlNWButton).pad(1.0f);
        whirlTable.add(whirlNEButton).pad(1.0f).row();
        whirlTable.add(whirlSWButton).pad(1.0f);
        whirlTable.add(whirlSEButton).pad(1.0f).row();
        whirlTable.add(addWhirlButton).colspan(2).row();
        whirlTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()-190);
        
        Table rockTable = new Table();
        rockTable.add(smallRockButton).pad(3.0f);
        rockTable.add(bigRockButton).pad(3.0f);
        rockTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()-310);
        
        Table flagTable = new Table();
        flagTable.add(flag1Button).pad(3.0f);
        flagTable.add(flag2Button).pad(3.0f);
        flagTable.add(flag3Button).pad(3.0f);
        flagTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()-370);
        
        //menu button options
        Table table = new Table();
        table.add(newButton).pad(3.0f).row();
        table.add(loadButton).pad(3.0f).row();
        table.add(saveButton).pad(3.0f).row();
        table.add(helpButton).padTop(3.0f).padBottom(20.0f).row();
        table.add(lobbyButton).pad(3.0f).row();
        stage.addActor(windTable);
        stage.addActor(whirlTable);
        stage.addActor(rockTable);
        stage.addActor(flagTable);
        stage.addActor(table);
        table.setPosition(Gdx.graphics.getWidth()-88, 100);
        
        loadButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		context.getMapEditor().setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().loadMap();
        	}
        });
        
        addWhirlButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setAddWhirlPool(true);
        		addWhirlButton.setDisabled(true);
        	}
        });
        saveButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		context.getMapEditor().setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().saveMap();
        	}
        });
        
        helpButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		context.getMapEditor().setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		showHelp();
        	}
        });
        
        newButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		context.getMapEditor().setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().createEmptyMap();
        	}
        });
        lobbyButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.exitMapEditor();
        	}
        });
    }
    
    public void initButtons() {
    	windNregularDrawable = new TextureRegionDrawable(context.getMapEditor().windNorthDisabled.getRegion());
        windNhoverDrawable = new TextureRegionDrawable(context.getMapEditor().windNorth.getRegion());
        
        windNStyle = new ImageButtonStyle();
        windNStyle.imageUp = windNhoverDrawable;
        windNStyle.imageDown = windNregularDrawable;
        windNStyle.imageDisabled = windNregularDrawable;
        
        windNButton = new ImageButton(windNStyle);
        windNButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().windNorth);
        		windNButton.setDisabled(true);
        	}
        });
        
    	windSregularDrawable = new TextureRegionDrawable(context.getMapEditor().windSouthDisabled.getRegion());
        windShoverDrawable = new TextureRegionDrawable(context.getMapEditor().windSouth.getRegion());
        
        windSStyle = new ImageButtonStyle();
        windSStyle.imageUp = windShoverDrawable;
        windSStyle.imageDown = windSregularDrawable;
        windSStyle.imageDisabled = windSregularDrawable;
        
        windSButton = new ImageButton(windSStyle);
        windSButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().windSouth);
        		windSButton.setDisabled(true);
        	}
        });
        //
    	windWregularDrawable = new TextureRegionDrawable(context.getMapEditor().windWestDisabled.getRegion());
        windWhoverDrawable = new TextureRegionDrawable(context.getMapEditor().windWest.getRegion());
        
        windWStyle = new ImageButtonStyle();
        windWStyle.imageUp = windWhoverDrawable;
        windWStyle.imageDown = windWregularDrawable;
        windWStyle.imageDisabled = windWregularDrawable;
        
        windWButton = new ImageButton(windWStyle);
        windWButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().windWest);
        		windWButton.setDisabled(true);
        	}
        });
        
        //
        //
    	windEregularDrawable = new TextureRegionDrawable(context.getMapEditor().windEastDisabled.getRegion());
        windEhoverDrawable = new TextureRegionDrawable(context.getMapEditor().windEast.getRegion());
        
        windEStyle = new ImageButtonStyle();
        windEStyle.imageUp = windEhoverDrawable;
        windEStyle.imageDown = windEregularDrawable;
        windEStyle.imageDisabled = windEregularDrawable;
        
        windEButton = new ImageButton(windEStyle);
        windEButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().windEast);
        		windEButton.setDisabled(true);
        	}
        });
        
        
        //WhirlPool
    	whirlNWregularDrawable = new TextureRegionDrawable(context.getMapEditor().whirlNWDisabled.getRegion());
    	whirlNWhoverDrawable = new TextureRegionDrawable(context.getMapEditor().whirlNW.getRegion());
        
    	whirlNWStyle = new ImageButtonStyle();
    	whirlNWStyle.imageUp = whirlNWhoverDrawable;
    	whirlNWStyle.imageDown = whirlNWregularDrawable;
    	whirlNWStyle.imageDisabled = whirlNWregularDrawable;
        
    	whirlNWButton = new ImageButton(whirlNWStyle);
        whirlNWButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().whirlNW);
        		whirlNWButton.setDisabled(true);
        	}
        });
        
    	whirlNEregularDrawable = new TextureRegionDrawable(context.getMapEditor().whirlNEDisabled.getRegion());
    	whirlNEhoverDrawable = new TextureRegionDrawable(context.getMapEditor().whirlNE.getRegion());
        
    	whirlNEStyle = new ImageButtonStyle();
    	whirlNEStyle.imageUp = whirlNEhoverDrawable;
    	whirlNEStyle.imageDown = whirlNEregularDrawable;
    	whirlNEStyle.imageDisabled = whirlNEregularDrawable;
        
    	whirlNEButton = new ImageButton(whirlNEStyle);
        whirlNEButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().whirlNE);
        		whirlNEButton.setDisabled(true);
        	}
        });
        
    	whirlSWregularDrawable = new TextureRegionDrawable(context.getMapEditor().whirlSWDisabled.getRegion());
    	whirlSWhoverDrawable = new TextureRegionDrawable(context.getMapEditor().whirlSW.getRegion());
        
    	whirlSWStyle = new ImageButtonStyle();
    	whirlSWStyle.imageUp = whirlSWhoverDrawable;
    	whirlSWStyle.imageDown = whirlSWregularDrawable;
    	whirlSWStyle.imageDisabled = whirlSWregularDrawable;
        
    	whirlSWButton = new ImageButton(whirlSWStyle);
        whirlSWButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().whirlSW);
        		whirlSWButton.setDisabled(true);
        	}
        });
        
    	whirlSEregularDrawable = new TextureRegionDrawable(context.getMapEditor().whirlSEDisabled.getRegion());
    	whirlSEhoverDrawable = new TextureRegionDrawable(context.getMapEditor().whirlSE.getRegion());
        
    	whirlSEStyle = new ImageButtonStyle();
    	whirlSEStyle.imageUp = whirlSEhoverDrawable;
    	whirlSEStyle.imageDown = whirlSEregularDrawable;
    	whirlSEStyle.imageDisabled = whirlSEregularDrawable;
        
    	whirlSEButton = new ImageButton(whirlSEStyle);
        whirlSEButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().whirlSE);
        		whirlSEButton.setDisabled(true);
        	}
        });
        
        //rocks
    	smallRockregularDrawable = new TextureRegionDrawable(context.getMapEditor().smallRockDisabled.getRegion());
    	smallRockhoverDrawable = new TextureRegionDrawable(context.getMapEditor().smallRock.getRegion());
        
    	smallRockStyle = new ImageButtonStyle();
    	smallRockStyle.imageUp = smallRockhoverDrawable;
    	smallRockStyle.imageDown = smallRockregularDrawable;
    	smallRockStyle.imageDisabled = smallRockregularDrawable;
        
    	smallRockButton = new ImageButton(smallRockStyle);
    	smallRockButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().smallRock);
        		smallRockButton.setDisabled(true);
        	}
        });
    	
    	bigRockregularDrawable = new TextureRegionDrawable(context.getMapEditor().bigRockDisabled.getRegion());
    	bigRockhoverDrawable = new TextureRegionDrawable(context.getMapEditor().bigRock.getRegion());
        
    	bigRockStyle = new ImageButtonStyle();
    	bigRockStyle.imageUp = bigRockhoverDrawable;
    	bigRockStyle.imageDown = bigRockregularDrawable;
    	bigRockStyle.imageDisabled = bigRockregularDrawable;
        
    	bigRockButton = new ImageButton(bigRockStyle);
    	bigRockButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().bigRock);
        		bigRockButton.setDisabled(true);
        	}
        });
    	
    	flag1regularDrawable = new TextureRegionDrawable(context.getMapEditor().flag1Disabled.getRegion());
    	flag1hoverDrawable = new TextureRegionDrawable(context.getMapEditor().flag1.getRegion());
        
    	flag1Style = new ImageButtonStyle();
    	flag1Style.imageUp = flag1hoverDrawable;
    	flag1Style.imageDown = flag1regularDrawable;
    	flag1Style.imageDisabled = flag1regularDrawable;
        
    	flag1Button = new ImageButton(flag1Style);
    	flag1Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().flag1);
        		flag1Button.setDisabled(true);
        	}
        });
    	
    	flag2regularDrawable = new TextureRegionDrawable(context.getMapEditor().flag2Disabled.getRegion());
    	flag2hoverDrawable = new TextureRegionDrawable(context.getMapEditor().flag2.getRegion());
        
    	flag2Style = new ImageButtonStyle();
    	flag2Style.imageUp = flag2hoverDrawable;
    	flag2Style.imageDown = flag2regularDrawable;
    	flag2Style.imageDisabled = flag2regularDrawable;
        
    	flag2Button = new ImageButton(flag2Style);
    	flag2Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().flag2);
        		flag2Button.setDisabled(true);
        	}
        });
    	
    	
    	flag3regularDrawable = new TextureRegionDrawable(context.getMapEditor().flag3Disabled.getRegion());
    	flag3hoverDrawable = new TextureRegionDrawable(context.getMapEditor().flag3.getRegion());
        
    	flag3Style = new ImageButtonStyle();
    	flag3Style.imageUp = flag3hoverDrawable;
    	flag3Style.imageDown = flag3regularDrawable;
    	flag3Style.imageDisabled = flag3regularDrawable;
        
    	flag3Button = new ImageButton(flag3Style);
    	flag3Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		context.getMapEditor().setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		context.getMapEditor().setCurrentTile(context.getMapEditor().flag3);
        		flag3Button.setDisabled(true);
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


    public void showHelp() {
    	Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    	Dialog dialog = new Dialog("Help", skin, "dialog");
		dialog.text("Basic Instructions:"
				+ "\n -------------------------------------------------------------------------------------------------------------------------"
				+ "\nLeft mouse button: adds selected game tile to selected position."
				+ "\n\nRight mouse button: sets selected to blank tile."
				+ "\n\nMiddle mouse button: allows zoom/panning around map."
				+ "\n -------------------------------------------------------------------------------------------------------------------------"
				+ "\nAdditional info:"
				+ "\n-To add an entire whirlpool, click 'Add Whirlpool' and select  tile where you want NW corner of whirlpool."
				+ "\n    (You will be unable to place a whirlpool at tile after oceanside safezone or rightmost tile of map)"
				+ "\n\n-You can also click and drag to add multiple game tiles, instead of clicking individually.");
		dialog.button("Ok");
    	dialog.setMovable(true);
		stage.addActor(dialog);
		dialog.show(stage);
		dialog.setResizable(true);
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
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}
}
