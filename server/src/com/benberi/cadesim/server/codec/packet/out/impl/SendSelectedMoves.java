package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;

public class SendSelectedMoves extends OutgoingPacket {

    private byte[] moves;
    private byte[] left;
    private byte[] right;

    public SendSelectedMoves() {
        super(OutGoingPackets.SEND_MOVES);
    }

    public void setMoves(byte[] moves) {
        this.moves = moves;
    }

    public void setLeft(byte[] left) {
        this.left = left;
    }

    public void setRight(byte[] right) {
        this.right = right;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeBytes(moves);
        writeBytes(left);
        writeBytes(right);
        setLength(getBuffer().readableBytes());
    }
}
