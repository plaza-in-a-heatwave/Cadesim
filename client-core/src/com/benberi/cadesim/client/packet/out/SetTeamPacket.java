package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The settings packet requests the server to
 */
public class SetTeamPacket extends OutgoingPacket {
	
	private int team;
	
    public SetTeamPacket() {
        super(12);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(getTeam());
        setLength(getBuffer().readableBytes());
    }

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
}
