package com.benberi.cadesim.client.packet.out;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The login packet requests the server to
 * connect to the game, with the given display name.
 */
public class LoginPacket extends OutgoingPacket {

    private String name;
    private String code;
    private int ship;
    private int version;
    private int team;

    public LoginPacket() {
        super(0);
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setCode(String code) {
    	this.code = code;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(version);
        writeByte(ship);
        writeByte(team);
        writeByteString(name);
        writeByteString(code);
        setLength(getBuffer().readableBytes());
    }

    public void setShip(int ship) {
        this.ship = ship;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
