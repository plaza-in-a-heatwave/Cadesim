package com.benberi.cadesim.game.screen.impl.connect;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TeamTypeLabel extends Label {
	
	public static final int ATTACKER = 0;
    public static final int DEFENDER = 1;
    
    private int type;

    public TeamTypeLabel( CharSequence text, LabelStyle style, int type) {
        super(text, style);
        this.type = type;
    }

    @Override
    public String toString() {
        return "" + getText();
    }

    public int getType() {
        return type;
    }
}
