package com.benberi.cadesim.game.scene.impl.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    /**
     * Tiles for map
     */
    public GameTile currentTile;
    public Cell cell;
    public SafeZone safezone;
    public static final int BIG_ROCK = 1;
    public BigRock bigRock;
    public static final int SMALL_ROCK = 2;
    public SmallRock smallRock;
    public static final int WIND_WEST = 3;
    public Wind windWest;
    public static final int WIND_EAST = 4;
    public Wind windEast;
    public static final int WIND_NORTH = 5;
    public Wind windNorth;
    public static final int WIND_SOUTH = 6;
    public Wind windSouth;
    public static final int WP_NW = 7;
    public Whirlpool whirlNW;
    public static final int WP_NE = 8;
    public Whirlpool whirlNE;
    public static final int WP_SW = 9;
    public Whirlpool whirlSW;
    public static final int WP_SE = 10;
    public Whirlpool whirlSE;
    public static final int FLAG_1 = 11;
    public Flag flag1;
    public static final int FLAG_2 = 12;
    public Flag flag2;
    public static final int FLAG_3 = 13;
    public Flag flag3;

    
    /**
     * top layer
     */
    private BlockadeMapLayer<GameObject> topLayer;

    
    public static GameTile[][] tiles;
    /**
     * The sea texture
     */
    private Texture seaTile;
    
    public MapEditorMapScene(GameContext context) {
        this.context = context;
        tiles = new GameTile[MAP_WIDTH][MAP_HEIGHT];
        seaTile = context.getManager().get(context.getAssetObject().sea);
        seaTile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        safezone = new SafeZone(context);
        cell = new Cell(context);
        windEast = new Wind(context,WIND_EAST);
        windSouth = new Wind(context,WIND_SOUTH);
        windWest = new Wind(context,WIND_WEST);
        windNorth = new Wind(context,WIND_NORTH);
        whirlSE = new Whirlpool(context,WP_SE);
        whirlSW = new Whirlpool(context,WP_SW);
        whirlNW = new Whirlpool(context,WP_NW);
        whirlNE = new Whirlpool(context,WP_NE);
        topLayer = new BlockadeMapLayer<GameObject>();
        flag1 = new Flag(context,1);
        flag2 = new Flag(context,2);
        flag3 = new Flag(context,3);
        //initialize currentTile
        setCurrentTile(windNorth);
        
    }
    /**
     * Creates an empty map with SafeZone tiles
     */
    public void createMap() {
        // Create the sea tiles
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (y < 3 || y > 32) {
                    if(tiles[x][y] == null) {
                        tiles[x][y] = safezone;
                    }
                }
                else {
                    if(tiles[x][y] == null) {
                        tiles[x][y] = cell;
                    }
                }

            }
        }

    }

    @Override
    public void create() {
        renderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth()-175, Gdx.graphics.getHeight());
        //initial camera settings;
        camera.position.x = 0;
        camera.position.y = 200;
        camera.zoom = 1.5f;
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
    	Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth()-175, Gdx.graphics.getHeight());
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

        if (this.canDragMap) {
            camera.translate(-x, y);
        }

        return true;
    }
    
    @Override
    public boolean handleClick(float x, float y, int button) {	
    	if(button != 2) {
        	int xz = (int)((x - camera.position.x / GameTile.TILE_WIDTH) + (y- camera.position.y / GameTile.TILE_HEIGHT));
            int yz = (int)((y - camera.position.y / GameTile.TILE_HEIGHT) - (x- camera.position.x / GameTile.TILE_WIDTH));
            
//        	System.out.println(xz  +","+ yz);
    	}
        
//    	if(button == 0) {
//    		if(yTile > 3 && yTile < 32) { //leave safezone alone
//                flag1 = new Flag(context,xTile,yTile,3);
//                topLayer.add(flag1);
//    		}
//    	}else if(button == 1) {
//    		if (yTile > 3 && yTile < 32) {//leave safezone alone
//                tiles[xTile][yTile] = cell;
//                if(topLayer.get(xTile, yTile) != null) {
//                	topLayer.remove(xTile, yTile);
//                }
//    		}
//    	}

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
        for (int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[i].length; j++) {
                TextureRegion region = tiles[i][j].getRegion();
                int x = (i * GameTile.TILE_WIDTH / 2) - (j * GameTile.TILE_WIDTH / 2) - region.getRegionWidth() / 2;
                int y = (i * GameTile.TILE_HEIGHT / 2) + (j * GameTile.TILE_HEIGHT / 2) - region.getRegionHeight() / 2;
                batch.draw(region, x, y);
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

                    int xx = (object.getX() * GameTile.TILE_WIDTH / 2) - (object.getY() * GameTile.TILE_WIDTH / 2) - region.getRegionWidth() / 2;
                    int yy = (object.getX() * GameTile.TILE_HEIGHT / 2) + (object.getY() * GameTile.TILE_HEIGHT / 2) - region.getRegionHeight() / 2;

                    if (!object.isOriented() || canDraw(xx + object.getOrientationLocation().getOffsetx(), yy + object.getOrientationLocation().getOffsety(), region.getRegionWidth(), region.getRegionHeight())) {
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
    }
    /**
     * Determines whether we can render
     */
    private boolean canDraw(float x, float y, int width, int height) {
        return x + width >= camera.position.x - camera.viewportWidth / 2 && x <= camera.position.x + camera.viewportWidth / 2 &&
                y + height >= camera.position.y - camera.viewportHeight / 2 && y <= camera.position.y + camera.viewportHeight / 2;
    }

    public GameObject getObject(float x, float y) {
        for (GameObject object : topLayer.getObjects()) {
            if (object.getX() == x && object.getY() == y) {
                return object;
            }
        }
        return null;
    }

	public GameTile getNextTile(GameTile tile) {
		if(tile instanceof Wind) {
			if(tile == windEast) {
				return windNorth;
			}else if(tile == windNorth) {//wind
				return windSouth;
			}else if(tile == windSouth) {
				return windWest;
			}else if(tile == windWest) {
				return windEast;
			}
		}
		if(tile instanceof Whirlpool) {
			if(tile == whirlSE) {//whirl
				return whirlSW;
			}else if(tile == whirlSW) {
				return whirlNW;
			}else if(tile == whirlNW) {
				return whirlNE;
			}else if(tile == whirlNE) {
				return whirlSE;
			}
		}
		if(tile instanceof BigRock) {
			return smallRock;
		}
		if(tile instanceof SmallRock) {
			return bigRock;
		}
		if(tile instanceof Flag){
			if(tile == flag1) {//whirl
				return flag2;
			}else if(tile == flag2) {
				return flag3;
			}else if(tile == flag3) {
				return flag1;
			}
		}
		
		return cell;
	}
	
	public GameTile getPreviousTile(GameTile tile) {
		if(tile instanceof Wind) {
			if(tile == windEast) {
				return windWest;
			}else if(tile == windWest) {//wind
				return windSouth;
			}else if(tile == windSouth) {
				return windNorth;
			}else if(tile == windNorth) {
				return windEast;
			}
		}
		if(tile instanceof Whirlpool) {
			if(tile == whirlSE) {//whirl
				return whirlNE;
			}else if(tile == whirlNE) {
				return whirlNW;
			}else if(tile == whirlNW) {
				return whirlSW;
			}else if(tile == whirlSW) {
				return whirlSE;
			}
		}
		if(tile instanceof BigRock) {
			return smallRock;
		}
		if(tile instanceof SmallRock) {
			return bigRock;
		}
		if(tile instanceof Flag){
			if(tile == flag1) {//whirl
				return flag3;
			}else if(tile == flag3) {
				return flag2;
			}else if(tile == flag2) {
				return flag1;
			}
		}
		
		return cell;
	}
	
	public void setCurrentTile(GameTile tile) {
		currentTile = tile;
	}
	
	public GameTile getCurrentTile() {
		return currentTile;
	}
	
    public void dispose() {
		camera = null;
		topLayer.clear();
	}
}
