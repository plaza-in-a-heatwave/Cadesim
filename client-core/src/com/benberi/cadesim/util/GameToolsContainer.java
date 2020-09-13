package com.benberi.cadesim.util;

import com.google.gson.Gson;

public class GameToolsContainer {

    /**
     * Gson object
     */
    private Gson gson = new Gson();

    public Gson getGson() {
        return this.gson;
    }
}
