package com.benberi.cadesim.server.model.cade.map;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

import java.util.ArrayList;
import java.util.List;

public class BlockadeMap {

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;


    // Rocks
    public static final int BIG_ROCK = 1;
    public static final int SMALL_ROCK = 2;

    //Winds
    public static final int WIND_WEST = 3;
    public static final int WIND_EAST = 4;
    public static final int WIND_NORTH = 5;
    public static final int WIND_SOUTH = 6;

    // Whirlpool parts
    public static final int WP_NW = 7;
    public static final int WP_NE = 8;
    public static final int WP_SW = 9;
    public static final int WP_SE = 10;

    // Flag types
    public static final int FLAG_1 = 11;
    public static final int FLAG_2 = 12;
    public static final int FLAG_3 = 13;


    /**
     * The map
     */
    private int[][] map = new int[MAP_WIDTH][MAP_HEIGHT];

    /**
     * List of flags
     */
    private List<Flag> flags = new ArrayList<>();

    public BlockadeMap(ServerContext ctx) {
    	this.map = MapType.DEFAULT.load(this);
    }

    public int[][] getMap() {
        return this.map;
    }

    /** determine whether item is rock
     *
     * @param x x coord
     * @param y y coord
     * @param p player colliding
     *
     * note: p is required because attackers can't access
     * landside safe zone
     * @return
     */
    public boolean isRock(int x, int y, Player p) {
        if (isOutOfBounds(x,y)) {
        	// out of bounds xy is not rock
            return false;
        } else if (this.isSafeLandside(new Position(x,y))) {
        	// attackers cannot port in landside safe zone
        	return (!(p.getTeam() == Team.DEFENDER) && (!p.isInSafe()));
        } else {
        	return this.map[x][y] == BIG_ROCK || this.map[x][y] == SMALL_ROCK;
        }
    }

    public boolean isBigRock(int x, int y) {
        if (isOutOfBounds(x,y)) {
            return false;
        }
         return this.map[x][y] == BIG_ROCK;
    }

    /**
     * Determine position within safe zones (and which safe zone)
     */
    public boolean isSafe(Position p) {
        return isSafeLandside(p) || isSafeOceanside(p);
    }
    public boolean isSafeLandside(Position p) {
    	return p.getY() >= 33;
    }
    public boolean isSafeOceanside(Position p) {
    	return p.getY() <= 2;
    }

    public boolean isOutOfBounds(int x, int y) {
        return x > 19 | x < 0 || y > 35 || y < 0;
    }

    public boolean isActionTile(int x, int y) {
        return this.isActionTile(this.map[x][y]);
    }

    public boolean isActionTile(int tile) {
        return tile >= 3 && tile <= 10;
    }

    public int getTile(int x, int y) {
        if (isOutOfBounds(x,y)) {
            return 0;
        }
        return this.map[x][y];
    }

    public Position getNextActionTilePosition(int tile, Position pos, int phase) {
        if (tile == -1) {
            tile = map[pos.getX()][pos.getY()];
        }
        // No phase 2 (2 because 0 and 1) for winds
        if (tile >= 3 && tile <= 6 && phase == 1) {
            return pos;
        }

        switch (tile) {
            case WIND_NORTH:
                return pos.copy().addY(1);
            case WIND_SOUTH:
                return pos.copy().addY(-1);
            case WIND_WEST:
                return pos.copy().addX(-1);
            case WIND_EAST:
                return pos.copy().addX(1);

            case WP_NE:
                switch (phase) {
                    case 0:
                        return pos.copy().addY(-1);
                    case 1:
                        return pos.copy().addX(-1);
                }
            case WP_NW:
                switch (phase) {
                    case 0:
                        return pos.copy().addX(1);
                    case 1:
                        return pos.copy().addY(-1);
                }
            case WP_SE:
                switch (phase) {
                    case 0:
                        return pos.copy().addX(-1);
                    case 1:
                        return pos.copy().addY(1);
                }
            case WP_SW:
                switch (phase) {
                    case 0:
                        return pos.copy().addY(1);
                    case 1:
                        return pos.copy().addX(1);
                }
        }

        return pos;
    }

    public VesselFace getNextActionTileFace(VesselFace face) {
        return face.getNext();
    }

    public Position getNextActionTilePositionForTile(Position pos, int tile) {
        switch (tile) {
            case WIND_NORTH:
                return pos.copy().addY(1);
            case WIND_SOUTH:
                return pos.copy().addY(-1);
            case WIND_WEST:
                return pos.copy().addX(-1);
            case WIND_EAST:
                return pos.copy().addX(1);
        }

        return pos;
    }

    public boolean isWhirlpool(int tile) {
        return tile >= 7 && tile <= 10;
    }

    public Flag getFlag(int x, int y) {
        for (Flag f : flags) {
            if (f.equals(x, y)) {
                return f;
            }
        }
        return null;
    }

    public List<Flag> getFlags() {
        return this.flags;
    }

    public List<Flag> getInfluencingFlags(Player player) {
        List<Flag> localFlags = new ArrayList<>();
        int diameter = player.getVessel().getInfluenceDiameter();
        int radius = diameter / 2;
        int squareRadius = radius;

        for (int x = player.getX() - squareRadius; x < player.getX() + squareRadius; x++) {
            for (int y = player.getY() - squareRadius; y < player.getY() + squareRadius; y++) {
                if((java.lang.Math.pow(x - player.getX(), 2) + java.lang.Math.pow(y - player.getY(), 2)) < java.lang.Math.pow(radius, 2)) {
                    Flag flag = getFlag(x, y);
                    if ((flag != null) && (!localFlags.contains(flag))) { // add once only
                        localFlags.add(flag);
                    }
                }
            }
        }

        Flag north = getFlag(player.getX(), player.getY() + radius);
        Flag south = getFlag(player.getX(), player.getY() - radius);
        Flag west = getFlag(player.getX() - radius, player.getY());
        Flag east = getFlag(player.getX() + radius, player.getY());

        // add the four cardinal flags.
        // in case radius is 0, our cardinal flags will be 4x identical.
        // avoid adding 4x in this case.
        if ((north != null) && (!localFlags.contains(north))) {
            localFlags.add(north);
        }
        if ((south != null) && (!localFlags.contains(south))) {
            localFlags.add(south);
        }
        if ((west != null) && (!localFlags.contains(west))) {
            localFlags.add(west);
        }
        if ((east != null) && (!localFlags.contains(east))) {
            localFlags.add(east);
        }

        return localFlags;
    }

    public void resetFlags() {
        for (Flag flag : flags) {
            flag.reset();
        }
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
    }
}
