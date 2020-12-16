package com.benberi.cadesim.game.screen.impl.screen.map.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.benberi.cadesim.game.screen.impl.battle.map.GameObject;

public class BlockadeMapLayer<T extends GameObject> {

    /**
     * The objects in this layer
     */
    private List<T> objects = new ArrayList<>();

    public void add(T o) {
        objects.add(o);
    }

    public List<T> getObjects() {
        return this.objects;
    }

    public T get(int x, int y) {
        for (T object : objects) {
            if (object.getX() == x && object.getY() == y) {
                return object;
            }
        }
        return null;
    }
    
    public void remove(int x, int y) {
    	
    	for (Iterator<T> iterator = objects.iterator(); iterator.hasNext();) {
    	    T object = iterator.next();
            if (object.getX() == x && object.getY() == y && object != null) {
            	iterator.remove();
            }
    	}
    }

    public void clear() {
        objects.clear();
    }
}
