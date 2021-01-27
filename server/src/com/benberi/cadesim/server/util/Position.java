package com.benberi.cadesim.server.util;

import com.benberi.cadesim.server.model.player.vessel.VesselFace;

/**
 * 2D Position
 */
public class Position {

    /**
     * X-axis position
     */
    private int x;

    /**
     * Y-axis position
     */
    private int y;

    public Position() {
    }

    /**
     * Create the instance with given position
     *
     * @param x The X
     * @param y The Y
     */
    public Position(int x, int y) {
        set(x, y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Appends X
     *
     * @param toAdd The X to append
     *
     * @return  This position
     */
    public Position addX(int toAdd) {
        this.x += toAdd;
        return this;
    }

    /**
     * Appends Y
     *
     * @param toAdd The Y to append
     *
     * @return  This position
     */
    public Position addY(int toAdd) {
        this.y += toAdd;
        return this;
    }

    /**
     * Appends X and Y
     *
     * @param x The X to append
     * @param y The Y to append
     *
     * @return This position
     */
    public Position add(int x, int y) {
        addX(x);
        addY(y);
        return this;
    }

    public void set(int x, int y) {
        setX(x);
        setY(y);
    }

    public Position get() {
        return this;
    }

    public void set(Position other) {
        set(other.getX(), other.getY());
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void resetPosition() {
        set(0, 0);
    }

    /**
     * Calculates distance between two positions
     * @param other The other position target
     * @return  The distance between the two positions
     */
    public double distance(Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) +  Math.pow(y - other.y, 2));
    }

    /**
     * Copies this position object
     * @return The copied object
     */
    public Position copy() {
        return new Position(x, y);
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Position) {
            Position other = (Position) o;
            return o == this || this.x == other.getX() && other.getY() == this.y;
        }

        return super.equals(o);
    }
    
    @Override
    public String toString() {
    	return "Position (x,y): " + this.getX() + "," + this.getY();
    }
    
    public static Position Forward(VesselFace face) {
    	switch(face) {
    		case NORTH:
    			return new Position(0,1);
    		case SOUTH:
    			return new Position(0,-1);
    		case WEST:
    			return new Position(-1,0);
    		case EAST:
    			return new Position(1,0);	
    		default:
    			return new Position(0,0);
    	}
    }
    
    public static Position Left(VesselFace face) {
    	switch(face) {
    		case NORTH:
    			return new Position(-1,1);
    		case SOUTH:
    			return new Position(1,-1);
    		case WEST:
    			return new Position(-1,-1);
    		case EAST:
    			return new Position(1,1);	
    		default:
    			return new Position(0,0);
    	}
    }
    
    public static Position Right(VesselFace face) {
    	switch(face) {
    		case NORTH:
    			return new Position(1,1);
    		case SOUTH:
    			return new Position(-1,-1);
    		case WEST:
    			return new Position(-1,1);
    		case EAST:
    			return new Position(1,-1);	
    		default:
    			return new Position(0,0);
    	}
    }
    
    public Position add(Position other) {
    	return new Position(this.x += other.x, this.y += other.y);
    }
}
