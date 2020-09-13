package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class ReceiveMessagePacket extends ClientPacketExecutor {

    public ReceiveMessagePacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        String sender  = p.readIntString();
        String message = p.readIntString();

        getContext().getControlScene().getBnavComponent().addNewMessage(sender, message);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
