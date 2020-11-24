package com.benberi.cadesim.server.codec.packet.out.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;
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
    
    public LoginResponsePacket() {
        super(OutGoingPackets.LOGIN_RESPONSE);
    }

    public void setResponse(int response) {
        this.response = response;
    }
    
    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeByte(response);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try { 
            ObjectOutputStream ooStream = new ObjectOutputStream(baos);
            ooStream.writeObject(ServerConfiguration.getGameSettings());
            ooStream.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeInt(baos.toByteArray().length);
        writeBytes(baos.toByteArray());
        setLength(getBuffer().readableBytes());
    }
}
