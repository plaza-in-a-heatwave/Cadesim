package com.benberi.cadesim.server.model.cade.map.flag;

import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.util.Position;

/**
 * A blockade score flag
 */
public class Flag extends Position {

    /**
     * The size of the flag
     */
    private FlagSize size;

    /**
     * If the flag is at war
     */
    private boolean atWar;

    /**
     * The team that controls the flag, null if none
     */
    private Team team;
    private boolean checked = false;

    public Flag(FlagSize size) {
        this.size = size;
    }
    
    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(Boolean value) {
        this.checked = value;
    }

    public boolean isControlled() {
        return this.team != null;
    }

    public void setControlled(Team team) {
        this.team = team;
    }

    public Team getController() {
        return this.team;
    }

    public FlagSize getSize() {
        return size;
    }

    public void reset() {
        this.team = null;
        this.atWar = false;
    }

    public void setAtWar(boolean atWar) {
        this.atWar = atWar;
    }

    public boolean isAtWar() {
        return atWar;
    }
}
