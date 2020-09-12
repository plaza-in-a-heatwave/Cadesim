package com.benberi.cadesim.game.scene.impl.battle.map;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.impl.battle.map.layer.BlockadeMapLayer;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.*;
import com.benberi.cadesim.util.RandomUtils;

public class BlockadeMap {

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;

    public static final int BIG_ROCK = 1;
    public static final int SMALL_ROCK = 2;
    public static final int WIND_WEST = 3;
    public static final int WIND_EAST = 4;
    public static final int WIND_NORTH = 5;
    public static final int WIND_SOUTH = 6;

    public static final int WP_NW = 7;
    public static final int WP_NE = 8;
    public static final int WP_SW = 9;
    public static final int WP_SE = 10;

    public static final int FLAG_1 = 11;
    public static final int FLAG_2 = 12;
    public static final int FLAG_3 = 13;


    /**
     * The game context
     */
    private GameContext context;

    /**
     * The rocks layer
     */
    private BlockadeMapLayer<GameObject> rocks = new BlockadeMapLayer<GameObject>();

    /**
     * Flags layer
     */
    private BlockadeMapLayer<Flag> flags = new BlockadeMapLayer<>();

    /**
     * Action tiles such as winds, whirlpools
     */
    private Wind[][] winds = new Wind[MAP_WIDTH][MAP_HEIGHT];

    private Whirlpool[][] whirls = new Whirlpool[MAP_WIDTH][MAP_HEIGHT];

    /**
     * The sea tile
     */
    private Cell[][] sea = new Cell[MAP_WIDTH][MAP_HEIGHT];

    public BlockadeMap(GameContext context, int[][] tiles) {
        this.context = context;

        // Create the sea tiles
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (y < 3 || y > 32) {
                    SafeZone zone = new SafeZone(context);
                    zone.setOrientation(RandomUtils.randInt(0, 3));
                    sea[x][y] = zone;
                }
                else {
                    Cell cell = new Cell(context);
                    cell.setOrientation(RandomUtils.randInt(0, 3));
                    sea[x][y] = cell;
                }

            }
        }
        // Create the sea tiles
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                int tile = tiles[x][y];
                switch (tile) {
                    case WIND_EAST:
                    case WIND_WEST:
                    case WIND_NORTH:
                    case WIND_SOUTH:
                        winds[x][y] = new Wind(context, tile);
                        break;
                    case WP_NE:
                    case WP_NW:
                    case WP_SE:
                    case WP_SW:
                        whirls[x][y] = new Whirlpool(context, tile);
                }
            }
        }

        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                int tile = tiles[x][y];
                switch (tile) {
                    case SMALL_ROCK:
                        rocks.add(new SmallRock(context, x, y));
                        break;
                    case BIG_ROCK:
                        rocks.add( new BigRock(context, x, y));
                        break;
                }
            }
        }
    }

    /**
     * Gets the sea with randomzied orientation
     */
    public Cell[][] getSea() {
        return sea;
    }

    public GameObject getObject(float x, float y) {
        for (GameObject object : rocks.getObjects()) {
            if (object.getX() == x && object.getY() == y) {
                return object;
            }
        }
        for (GameObject object : flags.getObjects()) {
            if (object.getX() == x && object.getY() == y) {
                return object;
            }
        }
        return null;
    }

    public Wind[][] getWinds() {
        return winds;
    }

    public Whirlpool[][] getWhirls() {
        return whirls;
    }

    public BlockadeMapLayer<Flag> getFlags() {
        return flags;
    }

	public void dispose() {
		rocks.clear();
		flags.clear();
	}
}
