package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.game.cade.Team;

public class ReceiveMessagePacket extends ClientPacketExecutor {

    public ReceiveMessagePacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        String sender  = p.readIntString();
        String message = p.readIntString();
        if(p.getBuffer().readableBytes() > 0) {
        	String channel = p.readByteString();
        	Team chatTeam = Team.forId(p.readInt());
        	if(channel.matches("team")) {
        		if(getContext().myTeam == chatTeam) {
        			getContext().getControl().getBnavComponent().addNewMessage(sender, message, Constants.serverTeam);
        		}
        	}else {
        		getContext().getControl().getBnavComponent().addNewMessage(sender, message);
        	}
        }else {
        	getContext().getControl().getBnavComponent().addNewMessage(sender, message);
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
