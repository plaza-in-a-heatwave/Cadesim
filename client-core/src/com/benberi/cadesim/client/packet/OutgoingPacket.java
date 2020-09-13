package com.benberi.cadesim.client.packet;

import com.benberi.cadesim.client.codec.util.Packet;

public abstract class OutgoingPacket extends Packet {

    public OutgoingPacket(int opcode) {
        super(opcode);
    }

    /**
     * Encodes the packet
     */
    public abstract void encode();
}
