package com.benberi.cadesim.game.scene.impl.battle.map.layer;

import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;

import java.util.ArrayList;
import java.util.List;

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

    public void clear() {
        objects.clear();
    }
}
