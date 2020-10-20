package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The settings packet requests the server to
 */
public class SendSettingsPacket extends OutgoingPacket {
	
	private int proposedTurnDuration;
	private int proposedRoundDuration;
	private int proposedSinkPenalty;
	private String proposedDisengageBehavior;
	private String proposedJobberQuality;
	private String proposedMapName;
	
    public SendSettingsPacket() {
        super(11);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeInt(getProposedTurnDuration());
        writeInt(getProposedRoundDuration());
        writeInt(getProposedSinkPenalty());
        writeByteString(getProposedDisengageBehavior());
        writeByteString(getProposedJobberQuality());
        writeByteString(getProposedMapName());
        setLength(getBuffer().readableBytes());
    }
    
    public int getProposedTurnDuration() {
    	return this.proposedTurnDuration;
    }
    
    public void setProposedTurnDuration(int value) {
    	this.proposedTurnDuration = value;
    }

    public int getProposedRoundDuration() {
		return proposedRoundDuration;
	}
    
    public void setProposedRoundDuration(int value) {
    	this.proposedRoundDuration = value;
    }

    public int getProposedSinkPenalty() {
    	return this.proposedSinkPenalty;
    }
	
    public void setProposedSinkPenalty(int value) {
    	this.proposedSinkPenalty = value;
    }
    
    public String getProposedDisengageBehavior() {
    	return this.proposedDisengageBehavior;
    }
    
    public void setProposedDisengageBehavior(String value) {
    	this.proposedDisengageBehavior = value;
    }
	
    public String getProposedJobberQuality() {
    	return this.proposedJobberQuality;
    }
    
    public void setProposedJobberQuality(String value) {
    	this.proposedJobberQuality = value;
    }
    
    public String getProposedMapName() {
    	return this.proposedMapName;
    }
    
    public void setProposedMapName(String value) {
    	this.proposedMapName = value;
    }
}
