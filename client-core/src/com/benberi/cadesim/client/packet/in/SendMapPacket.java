package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SendMapPacket extends ClientPacketExecutor {

    public SendMapPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int[][] map = new int[20][36];
        getContext().currentMapName  = p.readByteString().replace(".txt", ""); 
        while(p.getBuffer().readableBytes() >= 3) {
            int tile = p.readByte();
            int x = p.readByte();
            int y = p.readByte();
            map[x][y] = tile;
        }
        getContext().getBattleScene().createMap(map);
        getContext().setConnected(true);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
