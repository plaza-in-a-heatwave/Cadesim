package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class ListAllMapsPacket extends ClientPacketExecutor {
	private int size;
	private GameContext context;

    public ListAllMapsPacket(GameContext ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public void execute(Packet p) {
        size = (int)p.readByte();
        for(int i=0;i<size;i++) {
            context.getMaps().add((String)p.readByteString()); //writes all map names to list
        }
    }

	@Override
	public int getSize() {
		return -1;
	}
}
