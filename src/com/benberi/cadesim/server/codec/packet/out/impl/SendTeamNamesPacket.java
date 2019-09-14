package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendTeamNamesPacket extends OutgoingPacket {

    private String attacker = ServerConfiguration.getAttackerName();
    private String defender = ServerConfiguration.getDefenderName();

    public SendTeamNamesPacket() {
        super(OutGoingPackets.SET_TEAM_NAMES);
    }

    @Override
    public void encode() {

        String attacker = this.attacker;
        String defender = this.defender;

        setPacketLengthType(PacketLength.BYTE);
        writeByteString(attacker);
        writeByteString(defender);
        setLength(getBuffer().readableBytes());
    }
}
