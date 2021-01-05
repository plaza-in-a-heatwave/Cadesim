package com.benberi.cadesim.client.packet.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class LoginResponsePacket extends ClientPacketExecutor {

    public static final int SUCCESS = 0;
    public static final int NAME_IN_USE = 1;
    public static final int SERVER_FULL = 2;
    public static final int BAD_VERSION = 3;
    public static final int BAD_SHIP = 4;
    public static final int BAD_NAME = 5;

    public LoginResponsePacket(GameContext ctx) {
        super(ctx);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void execute(Packet p) {
        int response = p.readByte();
        String string = getContext().handleLoginResponse(response);
        int length = p.readInt();
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(p.readBytes(length)));
            try {
            	getContext().getGameSettings().clear();
            	getContext().getGameSettings().addAll((ArrayList<Object>) ois.readObject());
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
                ois.close();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if(string != null) {
	                getContext().getLobbyScreen().setPopupMessage(string);
	                getContext().getLobbyScreen().showPopup();	
				}
			}
    	});
    }

    @Override
    public int getSize() {
        return -1;
    }
}
