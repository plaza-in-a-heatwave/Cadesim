package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class LoginResponsePacket extends OutgoingPacket {

    public static final int SUCCESS = 0;
    public static final int NAME_IN_USE = 1;
    public static final int SERVER_FULL = 2;
    public static final int BAD_VERSION = 3;
    public static final int BAD_SHIP = 4;
    public static final int BAD_NAME = 5;

    /**
     * The response
     */
    private int response = SUCCESS;
    
    /**
     * Any constants we need to make the client aware of
     */
    private int roundDuration;
    private int turnDuration;

    public LoginResponsePacket() {
        super(OutGoingPackets.LOGIN_RESPONSE);
    }

    public void setResponse(int response) {
        this.response = response;
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
        setLength(5); // byte + (2 * short)
        writeByte(response);
        writeShort(turnDuration);   // 2-byte
        writeShort(roundDuration);  // 2-byte
    }
}
