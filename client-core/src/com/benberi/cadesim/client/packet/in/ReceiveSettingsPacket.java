package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class ReceiveSettingsPacket extends ClientPacketExecutor {

    public ReceiveSettingsPacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int turnDuration = p.readInt();
        int roundDuration = p.readInt();
        int sinkPenalty = p.readInt();
        String disengageBehavior = p.readByteString();
        String jobberQuality = p.readByteString();
        getContext().setProposedTurnDuration(turnDuration);
        getContext().setProposedRoundDuration(roundDuration);
        getContext().setProposedSinkPenalty(sinkPenalty);
        getContext().setProposedDisengageBehavior(disengageBehavior);
        getContext().setProposedJobberQuality(jobberQuality);
    }

    @Override
    public int getSize() {
        return -1;
    }
}
