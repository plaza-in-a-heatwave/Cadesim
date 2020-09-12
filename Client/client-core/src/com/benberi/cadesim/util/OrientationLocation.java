package com.benberi.cadesim.util;

public class OrientationLocation {

    private int x;
    private int y;
    private int width;
    private int height;
    private int offsetx;
    private int offsety;

    public OrientationLocation(int x, int y, int width, int height, int offsetx, int offsety) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.offsetx = offsetx;
        this.offsety = offsety;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetx() {
        return offsetx;
    }

    public int getOffsety() {
        return offsety;
    }
}
