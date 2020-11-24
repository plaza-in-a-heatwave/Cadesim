package com.benberi.cadesim.client.packet.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class ReceiveGameSettings extends ClientPacketExecutor {

    public ReceiveGameSettings(GameContext ctx) {
        super(ctx);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void execute(Packet p) {
        //set GUI settings each time settings are changed
        if(getContext().getBattleSceneMenu() != null) {
            getContext().getBattleSceneMenu().clearDisengageBehavior();
            getContext().getBattleSceneMenu().clearQuality();
            int length = p.readInt();
            ObjectInputStream ois;
    		try {
    			ois = new ObjectInputStream(new ByteArrayInputStream(p.readBytes(length)));
            	getContext().getGameSettings().clear();
            	getContext().getGameSettings().addAll((ArrayList<Object>) ois.readObject());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        }
    }

    @Override
    public int getSize() {
        return -1;
    }
}
