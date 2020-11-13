package com.benberi.cadesim.server.codec.packet;

import com.benberi.cadesim.server.codec.util.Packet;
import io.netty.channel.Channel;

public class IncomingPacket {

    /**
     * The sending channel
     */
    private Channel channel;

    /**
     * The packet
     */
    private Packet packet;

    public IncomingPacket(Channel c, Packet p) {
        this.channel = c;
        this.packet = p;
    }

    public Channel getChannel() {
        return channel;
    }

    public Packet getPacket() {
        return packet;
    }
}
