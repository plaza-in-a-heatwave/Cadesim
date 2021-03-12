package com.benberi.cadesim.util;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;

public class ShipSelection {

    private Texture baghlah;
    @SuppressWarnings("unused")
	private Texture blackship;
    private Texture dhow;
    private Texture fanchuan;
    private Texture grandfrig;
    private Texture junk;
    private Texture lgsloop;
    private Texture longship;
    private Texture merchbrig;
    private Texture merchgal;
    private Texture smsloop;
    private Texture warbrig;
    private Texture warfrig;
    private Texture wargal;
    private Texture xebec;
    
    private int currentShip = 0;
    
    private ArrayList<Tuple<String, Texture>> shipList = new ArrayList<Tuple<String, Texture>>();
    
    public ShipSelection(GameContext context) {
    	baghlah = context.getManager().get(context.getAssetObject().baghlahSkin);
        blackship = context.getManager().get(context.getAssetObject().blackshipSkin);
        dhow = context.getManager().get(context.getAssetObject().dhowSkin);
        fanchuan = context.getManager().get(context.getAssetObject().fanchuanSkin);
        grandfrig = context.getManager().get(context.getAssetObject().grandfrigSkin);
        junk = context.getManager().get(context.getAssetObject().junkSkin);
        lgsloop = context.getManager().get(context.getAssetObject().lgsloopSkin);
        longship = context.getManager().get(context.getAssetObject().longshipSkin);
        merchbrig = context.getManager().get(context.getAssetObject().merchbrigSkin);
        merchgal = context.getManager().get(context.getAssetObject().merchgalSkin);
        smsloop = context.getManager().get(context.getAssetObject().smsloopSkin);
        warbrig = context.getManager().get(context.getAssetObject().warbrigSkin);
        warfrig = context.getManager().get(context.getAssetObject().warfrigSkin);
        wargal = context.getManager().get(context.getAssetObject().wargalSkin);
        xebec = context.getManager().get(context.getAssetObject().xebecSkin);

        shipList.add(new Tuple<String, Texture>("Sloop",  smsloop));
        shipList.add(new Tuple<String, Texture>("Cutter",  lgsloop));
        shipList.add(new Tuple<String, Texture>("Dhow",  dhow));
        shipList.add(new Tuple<String, Texture>("Fanchuan",  fanchuan));
        shipList.add(new Tuple<String, Texture>("Longship",  longship));
        shipList.add(new Tuple<String, Texture>("Junk",  junk));
        shipList.add(new Tuple<String, Texture>("Baghlah",  baghlah));
        shipList.add(new Tuple<String, Texture>("Merchant Brig",  merchbrig));
        shipList.add(new Tuple<String, Texture>("War Brig",  warbrig));
        shipList.add(new Tuple<String, Texture>("Xebec",  xebec));
        shipList.add(new Tuple<String, Texture>("Merchant Galleon",  merchgal));
        shipList.add(new Tuple<String, Texture>("War Frigate",  warfrig));
        shipList.add(new Tuple<String, Texture>("War Galleon",  wargal));
        shipList.add(new Tuple<String, Texture>("Grand Frigate",  grandfrig));
        if (Constants.ENABLE_DEVELOPER_FEATURES) {
        	shipList.add(new Tuple<String, Texture>("Black Ship",  blackship));
        }
    }
    
    public int getCurrentShipAsInt() {
    	return currentShip;
    }
    
    public void setCurrentShipAsInt(int value) {
    	currentShip = value;
    }
    
    public Texture getCurrentShip() {
    	return (Texture) shipList.get(currentShip).getSecond();
    }
    
    public String getCurrentShipLabel() {
    	return (String) shipList.get(currentShip).getFirst();
    }
    
    public Texture getNextShip() {
    	currentShip++;
    	if(currentShip > shipList.size() - 1) {
    		currentShip = 0;
    	}
    	return (Texture) shipList.get(currentShip).getSecond();
    }
    
    public String getNextShipLabel() {
    	return (String) shipList.get(currentShip).getFirst();
    }
    
    public Texture getPreviousShip() {
    	currentShip--;
    	if(currentShip < 0) {
    		currentShip = shipList.size() - 1;
    	}
    	return (Texture) shipList.get(currentShip).getSecond();
    }
    
    public String getPreviousShipLabel() {
    	return (String) shipList.get(currentShip).getFirst();
    }
}
