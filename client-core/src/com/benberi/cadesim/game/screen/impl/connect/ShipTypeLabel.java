package com.benberi.cadesim.game.screen.impl.connect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ShipTypeLabel extends Label {
    private Texture type;
    private Integer index;

    public ShipTypeLabel(Texture type, Integer index, CharSequence text, LabelStyle style) {
        super(text, style);
        this.type = type;
        this.index = index;
    }

    @Override
    public String toString() {
        return "" + getText();
    }

    public Texture getType() {
        return type;
    }

	public int getIndex() {
		return index;
	}
}
