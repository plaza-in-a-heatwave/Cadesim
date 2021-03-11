package com.benberi.cadesim.util;

import java.util.Map;

public class UtilMethods {

	/*
	 * Initialize listeners for actors of stage
	 */
    public static Integer getKeysFromValue(Map<?, ?> hm, Object value){
    	System.out.println("Value::"+ value);
        for(Object o:hm.keySet()){
            if(hm.get(o).equals(value)) {
            	return (Integer)o;
            }
        }
        return null;
      }
}
