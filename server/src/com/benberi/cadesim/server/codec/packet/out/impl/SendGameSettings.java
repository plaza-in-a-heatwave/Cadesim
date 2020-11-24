package com.benberi.cadesim.server.codec.packet.out.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class SendGameSettings extends OutgoingPacket {

    public SendGameSettings() {
        super(OutGoingPackets.GAME_SETTINGS);
    }

    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
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
        writeInt(baos.size());
        writeBytes(baos.toByteArray());
        setLength(getBuffer().readableBytes());
    }
}
