package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;

public class SendSettingsPacket extends OutgoingPacket {

    public SendSettingsPacket() {
        super(OutGoingPackets.GAME_SETTINGS);
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeInt(ServerConfiguration.getTurnDuration());
        writeInt(ServerConfiguration.getRoundDuration());
        writeInt(ServerConfiguration.getRespawnDelay());
        writeByteString(ServerConfiguration.getDisengageBehavior());
        writeByteString(ServerConfiguration.getJobbersQualityAsString());
        setLength(getBuffer().readableBytes());
    }
}
