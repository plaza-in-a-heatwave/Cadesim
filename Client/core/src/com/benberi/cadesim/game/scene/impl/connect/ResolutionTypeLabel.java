package com.benberi.cadesim.game.scene.impl.connect;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ResolutionTypeLabel extends Label {
	// informed by
	// www.rapidtables.com/web/dev/screen-resolution-statistics.html
	public static final String[] RES_LIST = {
		"800x600",
	    "1024x768",
	    "1280x800",
	    "1280x1024",
	    "1366x768",
	    "1440x900",
	    "1600x900",
	    "1680x1050",
	    "1920x1080",
	    "2500x1400",
	    "3600x2000",
	};	
    
	/**
	 * helper methods for if you use the whole RES_LIST.
	 * if not, dont use these method.
	 */
    public static String[] restypeToRes(int restype) {
        return RES_LIST[restype].split("x");
    }
    public static int resToResType(String[] res) {
    	String combinedRes = res[0] + "x" + res[1];
    	for (int i=0; i<RES_LIST.length; i++) {
    		if (RES_LIST[i].equals(combinedRes))
    		{
    			return i;
    		}
    	}
    	
    	// didnt find it
    	return -1;
    }
    public static String resToString(String[] res) {
    	String combinedRes = res[0] + "x" + res[1];
    	return combinedRes;
    }
    private int type;

    public ResolutionTypeLabel(int type, CharSequence text, LabelStyle style) {
        super(text, style);
        this.type = type;
    }

    @Override
    public String toString() {
        return getText().toString();
    }

    public int getType() {
        return type;
    }
}
