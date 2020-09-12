package com.benberi.cadesim.util;

import java.util.HashMap;
import java.util.Map;

public class PackedObjectOrientation {

    private String name;

    private Map<Integer, OrientationLocation> orientations = new HashMap<Integer, OrientationLocation>();

    public PackedObjectOrientation(String name) {
        this.name = name;
    }

    public PackedObjectOrientation(String name, Map<Integer, OrientationLocation> o) {
        this.name = name;
        this.orientations = o;
    }

    public String getName() {
        return this.name;
    }

    public OrientationLocation getOrienation(int key) {
        return this.orientations.get(key);
    }

    public Map<Integer, OrientationLocation> getAllOrientations() {
        return this.orientations;
    }

    public void addOrientation(int key, OrientationLocation location) {
        orientations.put(key, location);
    }
}
