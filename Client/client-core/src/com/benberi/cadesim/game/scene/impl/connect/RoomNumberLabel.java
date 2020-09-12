package com.benberi.cadesim.game.scene.impl.connect;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class RoomNumberLabel
extends Label {
    
    private int type;

    public RoomNumberLabel(CharSequence text, Label.LabelStyle style, int type) {
        super(text, style);
        this.type = type;
    }

    @Override
    public String toString() {
        return "" + this.getText();
    }

    public int getType() {
        return this.type;
    }
}
