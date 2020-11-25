package com.benberi.cadesim.client.packet.in;

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
		while(p.getBuffer().readableBytes() > 0) {
			String name = p.readByteString();
			int teamID = p.readByte();
    		for(Vessel vessel : getContext().getEntities().listVesselEntities()) {
				if(getContext().myVessel.matches(name) && vessel.getName().matches(name)) {
					vessel.setTeam(Team.forId(teamID));
					getContext().setTeam(teamID); //needed to set clients team
    				Gdx.graphics.setTitle("CadeSim: " + getContext().myVessel + " (" + vessel.getTeam().toString() + ")");
				}
    		}
    		
    		for(Vessel vessel : getContext().getEntities().listVesselEntities()) {
    			vessel.setDefaultTexture();	
    		}
    		for(Vessel vessel : getContext().getEntities().listVesselEntities()) {
    			System.out.println(vessel.getName()+":"+vessel.getTeam());
    		}	
		}
    }

    @Override
    public int getSize() {
        return -1;
    }
}
