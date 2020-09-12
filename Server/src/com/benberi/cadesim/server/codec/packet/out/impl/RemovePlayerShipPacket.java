package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class RemovePlayerShipPacket extends OutgoingPacket {

    private String name;
    public RemovePlayerShipPacket() {
        super(OutGoingPackets.REMOVE_SHIP);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByteString(name);
        setLength(getBuffer().readableBytes());
    }
}
