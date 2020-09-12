package com.benberi.cadesim.client.packet.in;

import java.awt.image.BufferedImage;
import com.badlogic.gdx.graphics.Pixmap;
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
        context.pixmapArray = new Pixmap[size]; //create Pixmap array with number of maps
        int i=0;
        while(i<size) {
            context.getMaps().add((String)p.readByteString()); //writes all map names to list
            Integer fileSize = p.readInt(); //read size of png
            if(fileSize == 0) {//incase image not found from server
                context.pixmapArray[i] = null;
            }else {
                Pixmap pixmap = new Pixmap(p.readBytes(fileSize), 0, fileSize); //byte[] to pixmap
                context.pixmapArray[i] = pixmap; //add pixmap to pixmap array for future use
            }
            i++;
        }
    }

	@Override
	public int getSize() {
		return -1;
	}
}
