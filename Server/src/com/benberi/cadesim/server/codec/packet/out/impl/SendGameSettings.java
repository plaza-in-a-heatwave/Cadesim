package com.benberi.cadesim.server.codec.packet.out.impl;

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
        writeInt(ServerConfiguration.getProposedTurnDuration());   // 2-byte
        writeInt(ServerConfiguration.getProposedRoundDuration());  // 2-byte
        writeInt(ServerConfiguration.getProposedRespawnDelay());
	    writeByteString(ServerConfiguration.getProposedDisengageBehavior());
	    writeByteString(ServerConfiguration.getProposedJobbersQualityAsString());
	    writeInt(ServerConfiguration.isCustomMap()? 1 : 0);
        setLength(getBuffer().readableBytes());
    }
}
