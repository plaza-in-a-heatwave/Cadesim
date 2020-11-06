package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class SendMapPacket extends OutgoingPacket {

    private BlockadeMap bmap;

    public SendMapPacket() {
        super(OutGoingPackets.SEND_MAP);
    }

    public void setMap(BlockadeMap bmap) {
        this.bmap = bmap;
    }

    public void encode() {
        int[][] map = bmap.getMap();
        setPacketLengthType(PacketLength.MEDIUM);
        writeInt(ServerConfiguration.getProposedTurnDuration());   // 2-byte
        writeInt(ServerConfiguration.getProposedRoundDuration());  // 2-byte
        writeInt(ServerConfiguration.getProposedRespawnDelay());
	    writeByteString(ServerConfiguration.getProposedDisengageBehavior());
	    writeByteString(ServerConfiguration.getProposedJobbersQualityAsString());
	    writeInt(ServerConfiguration.isCustomMap()? 1 : 0);
        writeByteString(ServerConfiguration.getMapName());
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int tile = map[x][y];
                if (tile != 0) {
                    writeByte(tile);
                    writeByte(x);
                    writeByte(y);
                }
            }
        }
        writeByte(ServerConfiguration.getIslandId());
        setLength(getBuffer().readableBytes());
    }
}
