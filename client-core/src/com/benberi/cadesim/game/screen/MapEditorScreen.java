package com.benberi.cadesim.game.screen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.screen.impl.battle.map.GameObject;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.BigRock;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Cell;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Flag;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.SafeZone;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.SmallRock;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Whirlpool;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Wind;
import com.benberi.cadesim.game.screen.impl.screen.map.layer.BlockadeMapLayer;
import com.benberi.cadesim.util.AbstractScreen;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;

public class MapEditorScreen extends AbstractScreen implements InputProcessor {

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
	private GameContext context;

    /**
     * If the user can drag the map
     */
    private boolean canDragMap;

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;

    private static final int TILE_WIDTH_HALF = GameTile.TILE_WIDTH / 2;
    private static final int TILE_HEIGHT_HALF = GameTile.TILE_HEIGHT / 2;
    /**
     * Tiles for map - 
     * Had to create separate objects for the disabled buttons
     * Perhaps use them for preview in future
     */
    public GameTile currentTile;
    public Cell cell;
    public SafeZone safezone;
    public static final int BIG_ROCK = 1;
    public BigRock bigRock;
    public BigRock bigRockDisabled;
    public static final int SMALL_ROCK = 2;
    public SmallRock smallRock;
    public SmallRock smallRockDisabled;
    public static final int WIND_WEST = 3;
    public Wind windWest;
    public Wind windWestDisabled;
    public static final int WIND_EAST = 4;
    public Wind windEast;
    public Wind windEastDisabled;
    public static final int WIND_NORTH = 5;
    public Wind windNorth;
    public Wind windNorthDisabled;
    public static final int WIND_SOUTH = 6;
    public Wind windSouth;
    public Wind windSouthDisabled;
    public static final int WP_NW = 7;
    public Whirlpool whirlNW;
    public Whirlpool whirlNWDisabled;
    public static final int WP_NE = 8;
    public Whirlpool whirlNE;
    public Whirlpool whirlNEDisabled;
    public static final int WP_SW = 9;
    public Whirlpool whirlSW;
    public Whirlpool whirlSWDisabled;
    public static final int WP_SE = 10;
    public Whirlpool whirlSE;
    public Whirlpool whirlSEDisabled;
    public static final int FLAG_1 = 11;
    public Flag flag1;
    public Flag flag1Disabled;
    public static final int FLAG_2 = 12;
    public Flag flag2;
    public Flag flag2Disabled;
    public static final int FLAG_3 = 13;
    public Flag flag3;
    public Flag flag3Disabled;
    /**
     * booleans to tell whether add or remove whirlpool button is selected
     */
    public static volatile boolean addWhirlPool = false;
    
    /**
     * top layer
     */
    private BlockadeMapLayer<GameObject> topLayer;
    /**
     * The map tiles
     */
    public static GameTile[][] tiles;
    /**
     * The sea texture
     */
    private Texture seaTile;
    private OrthographicCamera camera;
    public MapEditorScreen(GameContext context) {
        this.context = context;
    }

    @Override
    public void buildStage() {
        tiles = new GameTile[MAP_WIDTH][MAP_HEIGHT];
        createTiles();
        createEmptyMap();
        createMenu();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.x = 0;
        camera.position.y = 200;
        MapEditorScreen screen = this;
        Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				initListeners();
		    	multiplexer.addProcessor(stage);
		    	multiplexer.addProcessor(screen);
		    	Gdx.input.setInputProcessor(multiplexer);
		    	graphics.setTitle("MapEditor - Blank");
			}
        });
        update();
    }
    
    public void update(){
        // update the camera
    	camera.position.x = clamp(camera.position.x, Gdx.graphics.getWidth() , -Gdx.graphics.getWidth());
    	camera.position.y = clamp(camera.position.y, Gdx.graphics.getHeight()+ 1000, 0);
    	camera.update();
    }

    @Override
    public void render(float delta) {
    	update();
    	stage.getBatch().setProjectionMatrix(camera.combined);
        drawSea();
        // Render the map
        renderMap();
        // Render separate layer of rocks/flags
        renderEntities();
    	drawBackground();
        stage.act();
        stage.draw();
    }

    /**
     * Get tile at coordinate X
     */
    public int getTileX(float x, float y) {
    	/*
    	 * getRegionWidth() = TILE_WIDTH_HALF 
    	 * getRegionHeight() = TILE_HEIGHT_HALF
    	 * these are the ones being added to worldCoords.x/y
        */
    	Vector3 worldCoords = camera.unproject(new Vector3(x, y, 0));
    	return (int)Math.floor((TILE_WIDTH_HALF * ((-TILE_HEIGHT_HALF + (worldCoords.y + TILE_HEIGHT_HALF)) / 
				TILE_HEIGHT_HALF) + (worldCoords.x + TILE_WIDTH_HALF)) / TILE_WIDTH_HALF / 2);
    }
    /**
     * Get tile at coordinate Y
     */
    public int getTileY(float x, float y) {
    	/*
    	 * getRegionWidth() = TILE_WIDTH_HALF 
    	 * getRegionHeight() = TILE_HEIGHT_HALF
    	 * these are the ones being added to worldCoords.x/y
        */
    	Vector3 worldCoords = camera.unproject(new Vector3(x, y, 0));
        return (int)Math.ceil((((-TILE_HEIGHT_HALF * (TILE_WIDTH_HALF + (worldCoords.x + GameTile.TILE_WIDTH)) / 
				TILE_WIDTH_HALF) + (worldCoords.y + GameTile.TILE_HEIGHT)) / TILE_HEIGHT_HALF) / 2);
    }

    /**
     * Zoom camera
     */
    public boolean scrolled(float amountX, float amountY) {
    	if(camera.zoom + (amountY*0.2) > 0.2f && camera.zoom + (amountY*0.2) < 3.0f) {
        	camera.zoom+=(amountY*0.2);
    	}
		return true;
    	
    }

    /**
     * Helper method to clamp values in certain range
     */
    public float clamp(float x, float f, int min) {
        if(x > min) {
            if(x < f) {
                return x;
            } else return f;
        } else return min;
    }
    /**
     * Draws the sea background
     */
    private void drawSea() {
    	stage.getBatch().begin();
    	stage.getBatch().draw(seaTile, -2000, -1000, 0, 0, 5000, 5000);
    	stage.getBatch().end();
    }
    /**
     * Draws the entire map
     */
    private void renderMap() {
        for (int tileX = 0; tileX < tiles.length; tileX++) {
            for(int tileY = 0; tileY < tiles[tileX].length; tileY++) {
            	if(tiles[tileX][tileY] != null) {
                    TextureRegion region = tiles[tileX][tileY].getRegion();
                    int worldX = (tileX - tileY)*TILE_WIDTH_HALF - region.getRegionWidth()/2;
                    int worldY = (tileX + tileY)*TILE_HEIGHT_HALF - region.getRegionHeight()/2;
                    stage.getBatch().begin();
                    stage.getBatch().draw(region, worldX, worldY);
                    stage.getBatch().end();
            	}
            }
        }
    }
    /**
     * Draws only the top layer stuff such as rocks/flags
     */
    private void renderEntities() {
        for (int x = MAP_WIDTH - 1; x > -1; x--) {
            for (int y = MAP_HEIGHT - 1; y > -1; y--) {
                GameObject object = getObject(x, y);
                if (object != null) {
                    TextureRegion region = object.getRegion();
                    int xx = ((object.getX() - object.getY()) * TILE_WIDTH_HALF) - region.getRegionWidth() / 2;
                    int yy = ((object.getX() + object.getY()) * TILE_HEIGHT_HALF) - region.getRegionHeight() / 2;
                    int offsetX = 0;
                    int offsetY = 0;
                    if (object.isOriented()) {
                        offsetX = object.getOrientationLocation().getOffsetx();
                        offsetY = object.getOrientationLocation().getOffsety();
                    }
                    else {
                        offsetX = object.getCustomOffsetX();
                        offsetY = object.getCustomOffsetY();
                    }
                    stage.getBatch().begin();
                    stage.getBatch().draw(region, xx + offsetX, yy + offsetY);
                    stage.getBatch().end();
                }
            }
        }
    }

    /**
     * Get object on topLayer (flags/rocks)
     */
    public GameObject getObject(float x, float y) {
        for (GameObject object : topLayer.getObjects()) {
            if (object.getX() == x && object.getY() == y) {
                return object;
            }
        }
        return null;
    }
    /**
     * Add an item to a specific tile
     */
    public void addTileItem(int xTile, int yTile) {
		if(yTile >= 3 && yTile <= 32) { //leave safezone alone
			if(isAddWhirlPool() && xTile >=0 && xTile <=19 && yTile >=4 && yTile <= 33) {
				setCurrentTile(whirlNW);
				int xTileNW = xTile;
				int yTileNW = yTile;
				int xTileNE = xTile+1;
				int yTileNE = yTile;
				int xTileSW = xTile;
				int yTileSW = yTile-1;
				int xTileSE = xTile+1;
				int yTileSE = yTile-1;

				tiles[xTileNW][yTileNW] = cell;
				tiles[xTileNE][yTileNE] = cell;
				tiles[xTileSW][yTileSW] = cell;
				tiles[xTileSE][yTileSE] = cell;
                if(topLayer.get(xTileNW, yTileNW) != null) {
                	topLayer.remove(xTileNW, yTileNW);
                }
                if(topLayer.get(xTileNE, yTileNE) != null) {
                	topLayer.remove(xTileNE, yTileNE);
                }
                if(topLayer.get(xTileSW, yTileSW) != null) {
                	topLayer.remove(xTileSW, yTileSW);
                }
                if(topLayer.get(xTileSE, yTileSE) != null) {
                	topLayer.remove(xTileSE, yTileSE);
                }
				tiles[xTileNW][yTileNW] = whirlNW;
				tiles[xTileNE][yTileNE] = whirlNE;
				tiles[xTileSW][yTileSW] = whirlSW;
				tiles[xTileSE][yTileSE] = whirlSE;
			}else if(isAddWhirlPool() && xTile >=0 && xTile <=19 && yTile ==3 && yTile <= 33) {
				setCurrentTile(null);
			}
			else if(getCurrentTile() instanceof Flag) {
				tiles[xTile][yTile] = cell;
                if(topLayer.get(xTile, yTile) != null) {
                	topLayer.remove(xTile, yTile);
                }
                Flag flag = new Flag(context,xTile, yTile,((Flag) getCurrentTile()).getSize());
				topLayer.add(flag);
			}
			else if(getCurrentTile() instanceof BigRock) {
				tiles[xTile][yTile] = cell;
                if(topLayer.get(xTile, yTile) != null) {
                	topLayer.remove(xTile, yTile);
                }
				BigRock bigRock = new BigRock(context,xTile, yTile,true);
				topLayer.add(bigRock);
			}
			else if(getCurrentTile() instanceof SmallRock) {
				tiles[xTile][yTile] = cell;
                if(topLayer.get(xTile, yTile) != null) {
                	topLayer.remove(xTile, yTile);
                }
				SmallRock smallRock = new SmallRock(context,xTile, yTile,true);
				topLayer.add(smallRock);
			}else if(getCurrentTile() instanceof Wind || getCurrentTile() instanceof Whirlpool){ 
                if(topLayer.get(xTile, yTile) != null) {
                	topLayer.remove(xTile, yTile);
                }
                tiles[xTile][yTile] = getCurrentTile();
			}else if(getCurrentTile() != null){
                if(topLayer.get(xTile, yTile) != null) {
                	topLayer.remove(xTile, yTile);
                }
                tiles[xTile][yTile] = cell;
			}
		}
    }
    /**
     * Remove an item to a specific tile
     */
    public void removeTileItem(int xTile, int yTile) {
		if (yTile >= 3 && yTile <= 32) {//leave safezone alone
            tiles[xTile][yTile] = cell;
            if(topLayer.get(xTile, yTile) != null) {
            	topLayer.remove(xTile, yTile);
            }
		}
    }
    /**
     * Sets current tile for next item
     */
	public void setCurrentTile(GameTile tile) {
		currentTile = tile;
	}
    /**
     * Gets current tile
     */
	public GameTile getCurrentTile() {
		return currentTile;
	}
    /**
     * To check if add whirlpool button is clicked
     */
    public boolean isAddWhirlPool() {
		return addWhirlPool;
	}
	
    public void setAddWhirlPool(boolean addWhirlPool) {
		MapEditorScreen.addWhirlPool = addWhirlPool;
	}
	
    public void dispose() {
		topLayer.clear();
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
	public boolean touchDown(int x, int y, int pointer, int button) {
    	if(button != 2) {
			if(x < camera.viewportWidth) {
				int xTile = getTileX(x,y);
				int yTile = getTileY(x,y);
				try {
		        	if(button == 0 && x < Gdx.graphics.getWidth() - 175) {
		        		addTileItem(xTile,yTile);
		        	}else if(button == 1 && x < Gdx.graphics.getWidth() - 175) {
		        		removeTileItem(xTile,yTile);
		        	}
				}catch(Exception e) {
					
				}
			}
		}
	    if (x < camera.viewportWidth) {
	        this.canDragMap = true;
	        return true;
	    }
	    this.canDragMap = false;
	    return false;
	}
		
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.canDragMap = false;
		return false;
	}
	
	@Override
	public boolean touchDragged(int sx, int sy, int pointer) {
		if (sy > camera.viewportHeight) {
          return false;
		}
	  	//allow user to drag and add
	  	if(Gdx.input.isButtonPressed(0)) {
	  		int xTile = getTileX(sx,sy);
				int yTile = getTileY(sx,sy);
				try {
		        	if(sx < Gdx.graphics.getWidth() - 175) {
		        		addTileItem(xTile,yTile);
		        	}
				}catch(Exception e) {
					
				}
	  	}
	  	//allow user to drag and remove
	  	if(Gdx.input.isButtonPressed(1)) {
	  		int xTile = getTileX(sx,sy);
				int yTile = getTileY(sx,sy);
				try {
		        	if(sx < Gdx.graphics.getWidth() - 175) {
		        		removeTileItem(xTile,yTile);
		        	}
				}catch(Exception e) {
					
				}
	  	}
	  	//allow user to drag camera
	  	if(Gdx.input.isButtonPressed(2)) {
	          if (this.canDragMap) {
	        	  float x = Gdx.input.getDeltaX(); float y = Gdx.input.getDeltaY();
	        	  camera.position.add(-x, y, 0); 
	        	  camera.update();
	          }
	  	}

      return true;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
    
    public void createTiles() {
        seaTile = context.getManager().get(context.getAssetObject().sea);
        seaTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        safezone = new SafeZone(context);
        cell = new Cell(context);
        windEast = new Wind(context,WIND_EAST,false);
        windEastDisabled = new Wind(context,WIND_EAST,true);
        windSouth = new Wind(context,WIND_SOUTH,false);
        windSouthDisabled = new Wind(context,WIND_SOUTH,true);
        windWest = new Wind(context,WIND_WEST,false);
        windWestDisabled = new Wind(context,WIND_WEST,true);
        windNorth = new Wind(context,WIND_NORTH,false);
        windNorthDisabled = new Wind(context,WIND_NORTH,true);
        whirlSE = new Whirlpool(context,WP_SE,false);
        whirlSEDisabled = new Whirlpool(context,WP_SE,true);
        whirlSW = new Whirlpool(context,WP_SW,false);
        whirlSWDisabled = new Whirlpool(context,WP_SW,true);
        whirlNW = new Whirlpool(context,WP_NW,false);
        whirlNWDisabled = new Whirlpool(context,WP_NW,true);
        whirlNE = new Whirlpool(context,WP_NE,false);
        whirlNEDisabled = new Whirlpool(context,WP_NE,true);
        topLayer = new BlockadeMapLayer<GameObject>();
        //need to initialize with no location to avoid nullpointer
        smallRock = new SmallRock(context,false);
        smallRockDisabled = new SmallRock(context,true);
        bigRock = new BigRock(context,false);
        bigRockDisabled = new BigRock(context,true);
        flag1 = new Flag(context,1,false);
        flag2 = new Flag(context,2,false);
        flag3 = new Flag(context,3,false);
        flag1Disabled = new Flag(context,1,true);
        flag2Disabled = new Flag(context,2,true);
        flag3Disabled = new Flag(context,3,true);
        //initialize currentTile
        setCurrentTile(cell);
    }
    
    public void createMenu() {
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
	    
	    Table menuTable = new Table();
	    menuTable.add(windTable).row();
	    menuTable.add(whirlTable).row();
	    menuTable.add(rockTable).row();
	    menuTable.add(flagTable).row();
	    menuTable.add(table).row();
	    stage.addActor(menuTable);
	    menuTable.setPosition(Gdx.graphics.getWidth()-88, Gdx.graphics.getHeight()/2);
    }
    
    public void initListeners() {
    	 windNButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(windNorth);
         		windNButton.setDisabled(true);
         	}
         });
         whirlSEButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(whirlSE);
         		whirlSEButton.setDisabled(true);
         	}
         });
         whirlSWButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(whirlSW);
         		whirlSWButton.setDisabled(true);
         	}
         });
         whirlNEButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(whirlNE);
         		whirlNEButton.setDisabled(true);
         	}
         });
         whirlNWButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(whirlNW);
         		whirlNWButton.setDisabled(true);
         	}
         });
         windEButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(windEast);
         		windEButton.setDisabled(true);
         	}
         });
         windWButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(windWest);
         		windWButton.setDisabled(true);
         	}
         });
         windSButton.addListener(new ClickListener() {
         	@Override
             public void clicked(InputEvent event, float x, float y) {
         		setAddWhirlPool(false);
         		for(Button button : buttonList) {
         			button.setDisabled(false);
         		}
         		setCurrentTile(windSouth);
         		windSButton.setDisabled(true);
         	}
         });
        flag3Button.addListener(new TextTooltip("3-Point flag", skin));
     	flag3Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setCurrentTile(flag3);
        		flag3Button.setDisabled(true);
        	}
        });
    	flag2Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setCurrentTile(flag2);
        		flag2Button.setDisabled(true);
        	}
        });
    	flag1Button.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setCurrentTile(flag1);
        		flag1Button.setDisabled(true);
        	}
        });
    	bigRockButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setCurrentTile(bigRock);
        		bigRockButton.setDisabled(true);
        	}
        });
    	smallRockButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setCurrentTile(smallRock);
        		smallRockButton.setDisabled(true);
        	}
        });
        loadButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		loadMap();
        	}
        });
        addWhirlButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		setAddWhirlPool(true);
        		addWhirlButton.setDisabled(true);
        	}
        });
        saveButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		saveMap();
        	}
        });
        helpButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		showHelp();
        	}
        });
        newButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		setAddWhirlPool(false);
        		setCurrentTile(null);
        		for(Button button : buttonList) {
        			button.setDisabled(false);
        		}
        		createEmptyMap();
        	}
        });
        lobbyButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
        		context.exitMapEditor();
        	}
        });
    }
    
    public void initButtons() {
    	windNregularDrawable = new TextureRegionDrawable(windNorthDisabled.getRegion());
        windNhoverDrawable = new TextureRegionDrawable(windNorth.getRegion());
        
        windNStyle = new ImageButtonStyle();
        windNStyle.imageUp = windNhoverDrawable;
        windNStyle.imageDown = windNregularDrawable;
        windNStyle.imageDisabled = windNregularDrawable;
        
        windNButton = new ImageButton(windNStyle);
        
    	windSregularDrawable = new TextureRegionDrawable(windSouthDisabled.getRegion());
        windShoverDrawable = new TextureRegionDrawable(windSouth.getRegion());
        
        windSStyle = new ImageButtonStyle();
        windSStyle.imageUp = windShoverDrawable;
        windSStyle.imageDown = windSregularDrawable;
        windSStyle.imageDisabled = windSregularDrawable;
        
        windSButton = new ImageButton(windSStyle);
        //
    	windWregularDrawable = new TextureRegionDrawable(windWestDisabled.getRegion());
        windWhoverDrawable = new TextureRegionDrawable(windWest.getRegion());
        
        windWStyle = new ImageButtonStyle();
        windWStyle.imageUp = windWhoverDrawable;
        windWStyle.imageDown = windWregularDrawable;
        windWStyle.imageDisabled = windWregularDrawable;
        
        windWButton = new ImageButton(windWStyle);
        
        //
        //
    	windEregularDrawable = new TextureRegionDrawable(windEastDisabled.getRegion());
        windEhoverDrawable = new TextureRegionDrawable(windEast.getRegion());
        
        windEStyle = new ImageButtonStyle();
        windEStyle.imageUp = windEhoverDrawable;
        windEStyle.imageDown = windEregularDrawable;
        windEStyle.imageDisabled = windEregularDrawable;
        
        windEButton = new ImageButton(windEStyle);
        
        //WhirlPool
    	whirlNWregularDrawable = new TextureRegionDrawable(whirlNWDisabled.getRegion());
    	whirlNWhoverDrawable = new TextureRegionDrawable(whirlNW.getRegion());
        
    	whirlNWStyle = new ImageButtonStyle();
    	whirlNWStyle.imageUp = whirlNWhoverDrawable;
    	whirlNWStyle.imageDown = whirlNWregularDrawable;
    	whirlNWStyle.imageDisabled = whirlNWregularDrawable;
        
    	whirlNWButton = new ImageButton(whirlNWStyle);
        
    	whirlNEregularDrawable = new TextureRegionDrawable(whirlNEDisabled.getRegion());
    	whirlNEhoverDrawable = new TextureRegionDrawable(whirlNE.getRegion());
        
    	whirlNEStyle = new ImageButtonStyle();
    	whirlNEStyle.imageUp = whirlNEhoverDrawable;
    	whirlNEStyle.imageDown = whirlNEregularDrawable;
    	whirlNEStyle.imageDisabled = whirlNEregularDrawable;
        
    	whirlNEButton = new ImageButton(whirlNEStyle);
        
    	whirlSWregularDrawable = new TextureRegionDrawable(whirlSWDisabled.getRegion());
    	whirlSWhoverDrawable = new TextureRegionDrawable(whirlSW.getRegion());
        
    	whirlSWStyle = new ImageButtonStyle();
    	whirlSWStyle.imageUp = whirlSWhoverDrawable;
    	whirlSWStyle.imageDown = whirlSWregularDrawable;
    	whirlSWStyle.imageDisabled = whirlSWregularDrawable;
        
    	whirlSWButton = new ImageButton(whirlSWStyle);

    	whirlSEregularDrawable = new TextureRegionDrawable(whirlSEDisabled.getRegion());
    	whirlSEhoverDrawable = new TextureRegionDrawable(whirlSE.getRegion());
        
    	whirlSEStyle = new ImageButtonStyle();
    	whirlSEStyle.imageUp = whirlSEhoverDrawable;
    	whirlSEStyle.imageDown = whirlSEregularDrawable;
    	whirlSEStyle.imageDisabled = whirlSEregularDrawable;
        
    	whirlSEButton = new ImageButton(whirlSEStyle);
        
        //rocks
    	smallRockregularDrawable = new TextureRegionDrawable(smallRockDisabled.getRegion());
    	smallRockhoverDrawable = new TextureRegionDrawable(smallRock.getRegion());
        
    	smallRockStyle = new ImageButtonStyle();
    	smallRockStyle.imageUp = smallRockhoverDrawable;
    	smallRockStyle.imageDown = smallRockregularDrawable;
    	smallRockStyle.imageDisabled = smallRockregularDrawable;
        
    	smallRockButton = new ImageButton(smallRockStyle);
    	
    	bigRockregularDrawable = new TextureRegionDrawable(bigRockDisabled.getRegion());
    	bigRockhoverDrawable = new TextureRegionDrawable(bigRock.getRegion());
        
    	bigRockStyle = new ImageButtonStyle();
    	bigRockStyle.imageUp = bigRockhoverDrawable;
    	bigRockStyle.imageDown = bigRockregularDrawable;
    	bigRockStyle.imageDisabled = bigRockregularDrawable;
        
    	bigRockButton = new ImageButton(bigRockStyle);

    	flag1regularDrawable = new TextureRegionDrawable(flag1Disabled.getRegion());
    	flag1hoverDrawable = new TextureRegionDrawable(flag1.getRegion());
        
    	flag1Style = new ImageButtonStyle();
    	flag1Style.imageUp = flag1hoverDrawable;
    	flag1Style.imageDown = flag1regularDrawable;
    	flag1Style.imageDisabled = flag1regularDrawable;
        
    	flag1Button = new ImageButton(flag1Style);
    	
    	flag2regularDrawable = new TextureRegionDrawable(flag2Disabled.getRegion());
    	flag2hoverDrawable = new TextureRegionDrawable(flag2.getRegion());
        
    	flag2Style = new ImageButtonStyle();
    	flag2Style.imageUp = flag2hoverDrawable;
    	flag2Style.imageDown = flag2regularDrawable;
    	flag2Style.imageDisabled = flag2regularDrawable;
        
    	flag2Button = new ImageButton(flag2Style);
    	
    	flag3regularDrawable = new TextureRegionDrawable(flag3Disabled.getRegion());
    	flag3hoverDrawable = new TextureRegionDrawable(flag3.getRegion());
        
    	flag3Style = new ImageButtonStyle();
    	flag3Style.imageUp = flag3hoverDrawable;
    	flag3Style.imageDown = flag3regularDrawable;
    	flag3Style.imageDisabled = flag3regularDrawable;
        
    	flag3Button = new ImageButton(flag3Style);
        
    }

    /**
     * Draws the menu background
     */
	private void drawBackground() {
     	Gdx.gl.glEnable(GL20.GL_BLEND);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(38 / 255f, 38 / 255f, 38 / 255f, 1f));
        renderer.rect(Gdx.graphics.getWidth()-175, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderer.setColor(new Color(72 / 255f, 72 / 255f, 72 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-174, 0, 1, Gdx.graphics.getHeight());

        renderer.setColor(new Color(135 / 255f, 161 / 255f, 188 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-173, 0, 1, Gdx.graphics.getHeight());

        renderer.setColor(new Color(135 / 255f, 135 / 255f, 135 / 255f, 1));
        renderer.rect(Gdx.graphics.getWidth()-171, 0, 1, Gdx.graphics.getHeight());
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


    public void showHelp() {
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

    /**
     * Creates an empty map with SafeZone tiles
     */
    public void createEmptyMap() {
    	Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				graphics.setTitle("MapEditor - Blank");
			}
    	});
    	//clear the tiles
    	topLayer.clear();
    	// Create the sea tiles
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (y < 3 || y > 32) {
                	tiles[x][y] = safezone;
                }
                else {
                	tiles[x][y] = cell;
                }
            }
        }
    }
    /**
     * Helper method, flip array horizontally
     */
    public static int[][] flipArray(int[][] theArray) {
        for(int i = 0; i < (theArray.length / 2); i++) {
            int[] temp = theArray[i];
            theArray[i] = theArray[theArray.length - i - 1];
            theArray[theArray.length - i - 1] = temp;
        }
        return theArray;
    }
    /**
     * Helper method, flipv vertically
     */
    public static int[][] inverseArray(int[][] matrix)
    {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] transposedMatrix = new int[n][m];

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < m; j++)
            {
                transposedMatrix[i][j] = matrix[j][i];
            }
        }

        return transposedMatrix;
    }
    /**
     * Wrapper to clear tiles
     */
    public void clearTiles() {
    	//clear the tiles
    	topLayer.clear();
    	createEmptyMap();
    }
    /**
     * Save current map
     */
    public void saveMap() {
        FileChooser fileChooser = new FileChooser(Mode.SAVE);
    	fileChooser.setSize(500, 300);
        fileChooser.setDirectory(System.getProperty("user.home"));
        FileTypeFilter typeFilter = new FileTypeFilter(true); //allow "All Types" mode where all files are shown
        typeFilter.addRule("Text files (*.txt)", "txt");
        fileChooser.setFileTypeFilter(typeFilter);
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setListener(new FileChooserAdapter() {
        	@Override
        	public void selected (Array<FileHandle> file) {
        		File selectedFile = file.first().file();
        		String name = selectedFile.getName();
            	Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
        				graphics.setTitle("MapEditor - current map: " + name);
        			}
            	});
        		if(!selectedFile.getAbsolutePath().endsWith(".txt") ) {
        			String fname = selectedFile.getAbsolutePath().toString() + ".txt";
        			selectedFile = new File(fname);
        		}
            	int[][] tempTiles = new int[MAP_WIDTH][MAP_HEIGHT];
            	//iterate through layer first to get entities
            	for(int x = 0; x < tiles.length; x++) {
            		for(int y = 0; y < tiles[x].length; y++) {
            			for (GameObject object : topLayer.getObjects()) {
    	                    if (object != null && object.getX() == x && object.getY() == y) {
                        		if(object instanceof Flag) {
                        			if(((Flag) object).getSize() == 1) {
                        				tempTiles[x][y] = FLAG_1;
                        			}else if(((Flag) object).getSize() == 2) {
                        				tempTiles[x][y] = FLAG_2;
                        			}else if(((Flag) object).getSize() == 3) {
                        				tempTiles[x][y] = FLAG_3;
                        			}
                        		}else if(object instanceof SmallRock) {
                        			tempTiles[x][y] = SMALL_ROCK;
                        		}else if(object instanceof BigRock) {
                        			tempTiles[x][y] = BIG_ROCK;
                        		}
                        	}
                        }
                    }
            	}
            	//iterate through tiles and change to numbers
            	for(int x = 0; x < tiles.length; x++) {
            		for(int y = 0; y < tiles[x].length; y++) {
            			if(tiles[x][y] == windEast) {
            				tempTiles[x][y] = WIND_EAST;
            			}else if(tiles[x][y] == windWest) {
            				tempTiles[x][y] = WIND_WEST;
            			}else if(tiles[x][y] == windSouth) {
            				tempTiles[x][y] = WIND_SOUTH;
            			}else if(tiles[x][y] == windNorth) {
            				tempTiles[x][y] = WIND_NORTH;
            			}else if(tiles[x][y] == whirlNW) {
            				tempTiles[x][y] = WP_NW;
            			}else if(tiles[x][y] == whirlNE) {
            				tempTiles[x][y] = WP_NE;
            			}else if(tiles[x][y] == whirlSW) {
            				tempTiles[x][y] = WP_SW;
            			}else if(tiles[x][y] == whirlSE) {
            				tempTiles[x][y] = WP_SE;
            			}
            		}
            	}
            	//flip and inverse array
            	tempTiles = flipArray(tempTiles);
            	tempTiles = inverseArray(tempTiles);
            	StringBuilder builder = new StringBuilder();
            	for (int i = tempTiles.length - 1; i >= 0; i--) {
            	    for (int j = tempTiles[i].length - 1; j >= 0; j--) {
            			builder.append(tempTiles[i][j]);
            			if(j < tempTiles[i].length && j !=0){
            				//if this is not the last row element
           		         	builder.append(",");
            			}
            		}
            		builder.append("\n");//append new line at the end of the row
            	}
            	BufferedWriter writer;
        		try {
        			writer = new BufferedWriter(new FileWriter(selectedFile));
        	    	writer.write(builder.toString());//save the string representation of the board
        	    	writer.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        });
    }
    /**
     * Load selected map
     */
    public void loadMap() {
    	FileChooser fileChooser = new FileChooser(Mode.OPEN);
    	fileChooser.setSize(500, 300);
        fileChooser.setDirectory(System.getProperty("user.home"));
        FileTypeFilter typeFilter = new FileTypeFilter(true); //allow "All Types" mode where all files are shown
        typeFilter.addRule("Text files (*.txt)", "txt");
        fileChooser.setFileTypeFilter(typeFilter);
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setListener(new FileChooserAdapter() {
        	@Override
        	public void selected(Array<FileHandle> file) {
        		int[][] tempTiles = new int[MAP_WIDTH][MAP_HEIGHT];
        		File selectedFile = file.first().file();
        		int x = 0;
    	        int y = 0;
    	        clearTiles();
    	        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))) {
    	            String line;
    	            while ((line = br.readLine()) != null) {
    	                String[] split = line.split(",");
    	                for (String tile : split) {
    	                	tempTiles[x][y] = Integer.parseInt(tile);
    	                    x++;
    	                }
    	                x = 0;
    	                y++;
    	            }
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }

    	        int x1 = 0;
    	        int y1 = 0;
    	        for (int i = 0; i < tempTiles.length; i++) {
    	            for (int j = tempTiles[i].length - 1; j > -1; j--) {
            			if(tempTiles[i][j] == WIND_EAST) {
            				tiles[x1][y1] = windEast;
            			}else if(tempTiles[i][j] == WIND_WEST) {
            				tiles[x1][y1] = windWest;
            			}else if(tempTiles[i][j] == WIND_SOUTH) {
            				tiles[x1][y1] = windSouth;
            			}else if(tempTiles[i][j] == WIND_NORTH) {
            				tiles[x1][y1] = windNorth;
            			}else if(tempTiles[i][j] == WP_NW) {
            				tiles[x1][y1] = whirlNW;
            			}else if(tempTiles[i][j] == WP_NE) {
            				tiles[x1][y1] = whirlNE;
            			}else if(tempTiles[i][j] == WP_SW) {
            				tiles[x1][y1] = whirlSW;
            			}else if(tempTiles[i][j] == WP_SE) {
            				tiles[x1][y1] = whirlSE;
            			}else if(tempTiles[i][j] == SMALL_ROCK) {
            				SmallRock small = new SmallRock(context,x1,y1,false);
            				topLayer.add(small);
            			}else if(tempTiles[i][j] == BIG_ROCK) {
            				BigRock big = new BigRock(context,x1,y1,false);
            				topLayer.add(big);
            			}else if(tempTiles[i][j] == FLAG_1) {
    	        			Flag flag_1 = new Flag(context,x1,y1,1);	
            				topLayer.add(flag_1);
            			}else if(tempTiles[i][j] == FLAG_2) {
            				Flag flag_2 = new Flag(context,x1,y1,2);
            				topLayer.add(flag_2);
            			}else if(tempTiles[i][j] == FLAG_3) {
            				Flag flag_3 = new Flag(context,x1,y1,3);
            				topLayer.add(flag_3);
            			}else if(tempTiles[i][j] == 0) {
                            if (y1 < 3 || y1 > 32) {
                            	tiles[x1][y1] = safezone;
                            }
                            else {
                            	tiles[x1][y1] = cell;
                            }
            			}
            			
    	                y1++;
    	            }
    	            y1 = 0;
    	            x1++;
    	        }
    	        String name = selectedFile.getName();
            	Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
        				graphics.setTitle("MapEditor - loaded map: " + name);
        			}
            	});
        	}
        });
    }
    
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
	
}