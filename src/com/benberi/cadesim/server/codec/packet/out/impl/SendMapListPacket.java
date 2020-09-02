package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;

public class SendMapListPacket extends OutgoingPacket{
    public SendMapListPacket() {
		super(OutGoingPackets.SEND_MAPNAMES);
	}

	@Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeByte((byte)ServerConfiguration.getAvailableMaps().size());
        for (int i=0; i<ServerConfiguration.getAvailableMaps().size(); i++)
        {
        	String map = ServerConfiguration.getAvailableMaps().get(i).replace(".txt", "");
            writeByteString(map);
        }
        setLength(getBuffer().readableBytes());
    }
}
