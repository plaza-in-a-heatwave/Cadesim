package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;

public class SendMapListPacket extends OutgoingPacket{
    public SendMapListPacket() {
		super(OutGoingPackets.SEND_MAPNAMES);
	}

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM); //
        writeByte((byte)ServerConfiguration.getAvailableMaps().size());
        for (int i=0; i<ServerConfiguration.getAvailableMaps().size(); i++)
        {
            String map = ServerConfiguration.getAvailableMaps().get(i).replace(".txt", "");
            writeByteString(map);
            String mapDir = String.format("maps/%s.png", map);
            try {
            	File imageFile = new File(mapDir);
            	byte[] fileContent = Files.readAllBytes(imageFile.toPath());
            	writeInt((int)imageFile.length()); //this should be the same as fileContent.length
            	writeBytes(fileContent); 
            } catch (IOException e) {
//              e.printStackTrace();
                writeInt(0);
            }
        }
        setLength(getBuffer().readableBytes());
    }
}
