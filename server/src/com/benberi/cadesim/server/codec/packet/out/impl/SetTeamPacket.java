package com.benberi.cadesim.server.codec.packet.out.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SetTeamPacket extends OutgoingPacket {

	private HashMap<String,Integer> teams_info;
	
    public SetTeamPacket() {
        super(OutGoingPackets.SET_TEAM);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try { 
            ObjectOutputStream ooStream = new ObjectOutputStream(baos);
            ooStream.writeObject(getTeamInfo());
            ooStream.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeInt(baos.toByteArray().length);
        writeBytes(baos.toByteArray());
        setLength(getBuffer().readableBytes());
    }

	public HashMap<String,Integer> getTeamInfo() {
		return teams_info;
	}

	public void setTeamInfo(HashMap<String,Integer> team_info) {
		this.teams_info = team_info;
	}
}
