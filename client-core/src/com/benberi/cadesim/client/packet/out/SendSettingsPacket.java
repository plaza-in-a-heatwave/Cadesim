package com.benberi.cadesim.client.packet.out;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.benberi.cadesim.client.codec.util.PacketLength;
import com.benberi.cadesim.client.packet.OutgoingPacket;

/**
 * The settings packet requests the server to
 */
public class SendSettingsPacket extends OutgoingPacket {
	
	private ArrayList<Object> settings;
	
    public SendSettingsPacket() {
        super(11);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try { 
            ObjectOutputStream ooStream = new ObjectOutputStream(baos);
            ooStream.writeObject(getSettings());
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
    
	public ArrayList<Object> getSettings() {
		return settings;
	}

	public void setSettings(ArrayList<Object> settings) {
		this.settings = settings;
	}
}
