package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class AddPlayerShipPacket extends OutgoingPacket {

    private String name;
    private int x;
    private int y;
    private int face;
    private int ship;
    private int team;

    public AddPlayerShipPacket() {
        super(OutGoingPackets.ADD_SHIP);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setFace(VesselFace face) {
        this.face = face.getDirectionId();
    }

    public void setShip(int ship) {
        this.ship = ship;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByteString(name);
        writeByte(x); // x
        writeByte(y); // y
        writeByte(face); // face
        writeByte(ship);
        writeByte(team);
        setLength(getBuffer().readableBytes());
    }
}
