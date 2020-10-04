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

    private int timeUntilBreak; // current time position until next break
    private int breakTime;      // current time position within a break

	private int turnDuration; // how long a turn lasts
	private int roundDuration;// how long a round lasts

	private int counter;      // a rolling byte counter to keep track of how lagged the player is


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

    public void setCounter(byte value) {
        this.counter = value;
    }

    /**
     * @param value -1 if breaks not used, 0 if in break, >0 otherwise
     */
    public void setTimeUntilBreak(int value) {
        this.timeUntilBreak = value;
    }

    /**
     * @param value -1 if breaks not used. ignored if not in break
     */
    public void setBreakTime(int value) {
        this.breakTime= value;
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
        writeInt(timeUntilBreak);
        writeInt(breakTime);
        writeInt(turnDuration);
        writeInt(roundDuration);
        writeByte(counter);
        setLength(getBuffer().readableBytes());
    }
}
