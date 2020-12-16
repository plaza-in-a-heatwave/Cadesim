package com.benberi.cadesim.game.screen.impl.battle.map.tile.impl;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.screen.impl.battle.map.GameObject;
import com.benberi.cadesim.util.RandomUtils;

public class Flag extends GameObject {

    private static final int flagWidth = 50;
    private static final int flagHeight = 69;

    public static final int GREEN = 0;
    public static final int RED = 1;
    public static final int NONE = 2;
    public static final int WAR = 3;

    private int size = 1;
    private boolean atWar;
    private Team controllerTeam;
    private boolean local;

    public Flag(GameContext context, int x, int y) {
        super(context);
        set(x, y);
        setTexture(
        		context.getManager().get(context.getAssetObject().flagTexture));

        setCustomOffsetX(2);
        setCustomOffsetY(22);

        size = RandomUtils.randInt(1, 3);
    }
    
    public Flag(GameContext context, int x, int y, int flagSize) {
        super(context);
        set(x, y);
        setTexture(
        		context.getManager().get(context.getAssetObject().flagTexture));
        setCustomOffsetX(2);
        setCustomOffsetY(22);
        setSize(flagSize);
        updateTextureRegion();
    }
    
    public Flag(GameContext context, int flagSize, boolean disabled) {
        super(context);
        if(!disabled) {
            setTexture(
            		context.getManager().get(context.getAssetObject().flagTexture));	
        }else {
            setTexture(
            		context.getManager().get(context.getAssetObject().flagTexture_disabled));
        }
        setCustomOffsetX(2);
        setCustomOffsetY(22);
        setSize(flagSize);
        updateTextureRegion();
    }
    
    public void setLocation(int x, int y) {
    	set(x, y);
    }
    
    public boolean isAtWar() {
        return this.atWar;
    }

    public int getSize() {
        return this.size;
    }

    public Team getControllerTeam() {
        return this.controllerTeam;
    }

    @Override
    public boolean isOriented() {
        return false;
    }

    public void updateTextureRegion() {
        if (local) {
            switch (size) {
                case 1:
                    getRegion().setRegion(0, 0, flagWidth, flagHeight);
                    break;
                case 2:
                    getRegion().setRegion(0, 69, flagWidth, flagHeight);
                    break;
                case 3:
                    getRegion().setRegion(0, 138, flagWidth, flagHeight);
                    break;
            }
            return;
        }
        if (atWar) {
            switch (size) {
                case 1:
                    getRegion().setRegion(200, 0, flagWidth, flagHeight);
                    break;
                case 2:
                    getRegion().setRegion(200, 69, flagWidth, flagHeight);
                    break;
                case 3:
                    getRegion().setRegion(200, 138, flagWidth, flagHeight);
                    break;
            }
            return;
        }
        if (controllerTeam == null) {
            switch (size) {
                case 1:
                    getRegion().setRegion(250, 0, flagWidth, flagHeight);
                    break;
                case 2:
                    getRegion().setRegion(250, 69, flagWidth, flagHeight);
                    break;
                case 3:
                    getRegion().setRegion(250, 138, flagWidth, flagHeight);
                    break;
            }
            return;
        }
        switch (controllerTeam) {
            case DEFENDER:
                switch (size) {
                    case 1:
                        getRegion().setRegion(50, 0, flagWidth, flagHeight);
                        break;
                    case 2:
                        getRegion().setRegion(50, 69, flagWidth, flagHeight);
                        break;
                    case 3:
                        getRegion().setRegion(50, 138, flagWidth, flagHeight);
                        break;
                }
                break;
            case ATTACKER:
                switch (size) {
                    case 1:
                        getRegion().setRegion(100, 0, flagWidth, flagHeight);
                        break;
                    case 2:
                        getRegion().setRegion(100, 69, flagWidth, flagHeight);
                        break;
                    case 3:
                        getRegion().setRegion(100, 138, flagWidth, flagHeight);
                        break;
                }
                break;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAtWar(boolean atWar) {
        this.atWar = atWar;
    }

    public void setControllerTeam(Team controllerTeam) {
        this.controllerTeam = controllerTeam;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}
