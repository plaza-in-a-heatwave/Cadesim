package com.benberi.cadesim.game.scene.impl.mapeditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;
import com.benberi.cadesim.game.scene.impl.battle.map.layer.BlockadeMapLayer;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.BigRock;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Cell;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Flag;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.SafeZone;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.SmallRock;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Whirlpool;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Wind;

public class MapEditorMapScene implements GameScene {
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
     * The camera view of the scene
     */
    private OrthographicCamera camera;

    /**
     * The shape renderer
     */
    private ShapeRenderer renderer;

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
    /**
     * File open/save dialog
     */
    private JFileChooser fileChooser;
    
    public MapEditorMapScene(GameContext context) {
    	Gdx.graphics.setTitle("MapEditor - Blank");
        this.context = context;
        fileChooser = new JFileChooser();
        tiles = new GameTile[MAP_WIDTH][MAP_HEIGHT];
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
    /**
     * Creates an empty map with SafeZone tiles
     */
    public void createEmptyMap() {
    	Gdx.graphics.setTitle("MapEditor - Blank");
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
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	fileChooser.setFileFilter(new FileNameExtensionFilter("txt file","txt"));
    	int result = fileChooser.showSaveDialog(null);
    	if(result == JFileChooser.APPROVE_OPTION) {
    		File selectedFile = fileChooser.getSelectedFile();
    		Gdx.graphics.setTitle("MapEditor - current map: " + selectedFile.getName());
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
    }
    /**
     * Load selected map
     */
    public void loadMap() {
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	fileChooser.setFileFilter(new FileNameExtensionFilter("txt file","txt"));
    	fileChooser.setDialogTitle("Select Map File");
    	int result = fileChooser.showOpenDialog(null);
    	if(result == JFileChooser.APPROVE_OPTION) {
    		int[][] tempTiles = new int[MAP_WIDTH][MAP_HEIGHT];
    		File selectedFile = fileChooser.getSelectedFile();
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
	        Gdx.graphics.setTitle("MapEditor - loaded map: "+selectedFile.getName());
    	}
    	
    }

    @Override
    public void create() {
        renderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.x = 0;
        camera.position.y = 200;
    }

    @Override
    public void update(){
        // update the camera
    	camera.position.x = clamp(camera.position.x, Gdx.graphics.getWidth() , -Gdx.graphics.getWidth());
    	camera.position.y = clamp(camera.position.y, Gdx.graphics.getHeight()+ 1000, 0);
    	camera.update();
    }

    @Override
    public void render() {
    	Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawSea();
        // Render the map
        renderMap();
        // Render separate layer of rocks/flags
        renderEntities();
        batch.end();
    }

    @Override
    public boolean handleDrag(float sx, float sy, float x, float y) {
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
                camera.translate(-x, y);
            }
    	}

        return true;
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
    
    @Override
    public boolean handleClick(float x, float y, int button) {
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
    public boolean handleMouseMove(float x, float y) {
        return false;
    }
    /**
     * Zoom camera
     */
    public boolean scrolled(int amount) {
    	if(camera.zoom + (amount*0.2) > 0.2f && camera.zoom + (amount*0.2) < 3.0f) {
        	camera.zoom+=(amount*0.2);
    	}
		return true;
    	
    }

    @Override
    public boolean handleClickRelease(float x, float y, int button) {
        this.canDragMap = false;
        return false;
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
        batch.draw(seaTile, -2000, -1000, 0, 0, 5000, 5000);
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
                    batch.draw(region, worldX, worldY);
            	}
            }
        }
    }
    /**
     * Draws only the top layer stuff such as rocks/flags
     */
    private void renderEntities() {
        renderer.setProjectionMatrix(camera.combined);

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
                    batch.draw(region, xx + offsetX, yy + offsetY);
                }
            }
        }
    }
    /**
     * Determines whether we can render
     */
    @SuppressWarnings("unused")
	private boolean canDraw(float x, float y, int width, int height) {
        return x + width >= camera.position.x - camera.viewportWidth / 2 && x <= camera.position.x + camera.viewportWidth / 2 &&
                y + height >= camera.position.y - camera.viewportHeight / 2 && y <= camera.position.y + camera.viewportHeight / 2;
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
			}else {
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
		MapEditorMapScene.addWhirlPool = addWhirlPool;
	}
	
    public void dispose() {
		camera = null;
		topLayer.clear();
	}
}
