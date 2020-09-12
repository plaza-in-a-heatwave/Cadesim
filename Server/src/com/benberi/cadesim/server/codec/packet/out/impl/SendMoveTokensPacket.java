package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendMoveTokensPacket extends OutgoingPacket {

    private int left;
    private int forward;
    private int right;
    private int leftNew    = 0; // assume 0 if not set
    private int forwardNew = 0; // "
    private int rightNew   = 0; // "
    private int cannons;

    public SendMoveTokensPacket() {
        super(OutGoingPackets.SEND_MOVE_TOKENS);
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setForward(int forward) {
        this.forward = forward;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void setCannons(int cannons) {
        this.cannons = cannons;
    }

    /**
     * number of new tokens generated (if any). Not cumulative
     */
    public void setNew(int leftNew, int forwardNew, int rightNew)
    {
        this.leftNew = leftNew;
        this.forwardNew = forwardNew;
        this.rightNew = rightNew;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);
        writeByte(left);
        writeByte(forward);
        writeByte(right);
        writeByte(leftNew);
        writeByte(forwardNew);
        writeByte(rightNew);
        writeByte(cannons);
        setLength(getBuffer().readableBytes());
    }
}
