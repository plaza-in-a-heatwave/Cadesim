package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendTimePacket extends OutgoingPacket {

    private int gameTime;     // current time position within a round
    private int turnTime;     // current time position within a turn
	private int turnDuration; // how long a turn lasts
	private int roundDuration;// how long a round lasts

    public SendTimePacket() {
        super(OutGoingPackets.TIME_PACKET);
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
        if (turnTime < 0) {
            this.turnTime = 0;
        }
    }
    
    public void setTurnDuration(int turnDuration) {
    	this.turnDuration = turnDuration;
    }
    
    public void setRoundDuration(int roundDuration) {
    	this.roundDuration = roundDuration;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeInt(gameTime);
        writeInt(turnTime);
        writeInt(turnDuration);
        writeInt(roundDuration);
        setLength(getBuffer().readableBytes()); // 4 x byte
    }
}
