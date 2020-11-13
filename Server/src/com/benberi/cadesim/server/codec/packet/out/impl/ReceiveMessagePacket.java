package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class ReceiveMessagePacket extends OutgoingPacket {

    private String message;
    private String fromPlayer;
    private ServerContext context;
    
    public ReceiveMessagePacket() {
        super(OutGoingPackets.RECEIVE_MESSAGE);
    }
    
    public void setPlayer(String fromPlayer) {
        this.fromPlayer = fromPlayer;
    }
    
    public void setContext(ServerContext context) {
        this.context = context;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.MEDIUM);
        writeIntString(fromPlayer);
        writeIntString(message);
        if(context.getPlayerManager().getPlayerByName(fromPlayer) != null) {
        	writeByteString(context.getPlayerManager().getPlayerByName(fromPlayer).getChatChannel());
        	writeInt(context.getPlayerManager().getPlayerTeamByName(fromPlayer).getID());
        }
        setLength(getBuffer().readableBytes());
    }
}
