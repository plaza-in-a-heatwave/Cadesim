package com.benberi.cadesim.client.packet.in;

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

    @Override
    public void execute(Packet p) {
        int response = p.readByte();
        int turnDuration = p.readShort();
        int roundDuration = p.readShort();
        int respawnDelay = p.readShort();
        String disengageBehavior = p.readByteString();
        String jobberQuality = p.readByteString();
        getContext().setTurnDuration(turnDuration);
        getContext().setRoundDuration(roundDuration);
        
        getContext().setDefaultTurnDuration(turnDuration);
        getContext().setDefaultRoundDuration(roundDuration);
        getContext().setDefaultRespawnDelay(respawnDelay);
        getContext().setDefaultDisengageBehavior(disengageBehavior);
        getContext().setDefaultJobberQuality(jobberQuality);
        
        getContext().setProposedTurnDuration(turnDuration);
        getContext().setProposedRoundDuration(roundDuration);
        getContext().setProposedRespawnDelay(respawnDelay);
        getContext().setProposedDisengageBehavior(disengageBehavior);
        getContext().setProposedJobberQuality(jobberQuality);
        getContext().handleLoginResponse(response);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
