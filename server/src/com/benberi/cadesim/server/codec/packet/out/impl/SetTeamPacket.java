package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SetTeamPacket extends OutgoingPacket {

	private int team;
	private String name;
    public SetTeamPacket() {
        super(OutGoingPackets.SET_TEAM);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
    	writeByteString(getName());
    	writeByte(getTeam());
        setLength(getBuffer().readableBytes());
    }

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
