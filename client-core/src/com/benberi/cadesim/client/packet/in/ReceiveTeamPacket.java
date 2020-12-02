package com.benberi.cadesim.client.packet.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.entity.vessel.Vessel;

public class ReceiveTeamPacket extends ClientPacketExecutor {

    public ReceiveTeamPacket(GameContext ctx) {
        super(ctx);
    }

	@Override
    public void execute(Packet p) {
		//why is teams not setting correctly from server?
        int length = p.readInt();
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(p.readBytes(length)));
            try {
            	@SuppressWarnings("unchecked")
				HashMap<String,Integer> team_info = (HashMap<String, Integer>) ois.readObject();
        		for(Vessel vessel : getContext().getEntities().listVesselEntities()) {
     		       for (Map.Entry<String,Integer> player : team_info.entrySet()) {
     		    	   String playerName = (String)player.getKey();
     		    	   int playerTeam = (int)player.getValue();
     		    	   if(vessel.getName().matches(getContext().myVessel) && playerName.matches(getContext().myVessel)) {
     		    		   getContext().setTeam(playerTeam);
     		    		   vessel.setTeam(Team.forId(playerTeam));
     		    		  Gdx.graphics.setTitle("GC: " + getContext().myVessel + " (" + getContext().myTeam.toString() + ")");
     		    	   }else if(playerName.matches(vessel.getName())){
     		    		   vessel.setTeam(Team.forId(playerTeam));	   
     		    	   }
     		       }
        		}
     		
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
                ois.close();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Vessel vessel : getContext().getEntities().listVesselEntities()) {
	    	   vessel.setDefaultTexture();
		}
		
		getContext().getBattleScene().getInformation().setTeamColors();
    }

    @Override
    public int getSize() {
        return -1;
    }
}
