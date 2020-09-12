package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class ReceiveMessagePacket extends OutgoingPacket {

    private String message;
    private String fromPlayer;

    public ReceiveMessagePacket() {
        super(OutGoingPackets.RECEIVE_MESSAGE);
    }
    
    public void setPlayer(String fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeIntString(fromPlayer);
        writeIntString(message);
        setLength(getBuffer().readableBytes());
    }
}
