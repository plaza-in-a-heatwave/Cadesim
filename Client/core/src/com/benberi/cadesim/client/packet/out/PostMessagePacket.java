package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * Post a message from the client's chat window to the server
 */
public class PostMessagePacket extends OutgoingPacket {
    public PostMessagePacket() {
        super(8);
    }
    
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeIntString(message);
        setLength(getBuffer().readableBytes());
    }
}
