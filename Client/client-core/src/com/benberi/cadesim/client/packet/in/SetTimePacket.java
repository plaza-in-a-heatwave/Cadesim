package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;

public class SetTimePacket extends ClientPacketExecutor {

    public SetTimePacket(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Packet p) {
        int gameTime       = p.readInt();
        int turnTime       = p.readInt();
        int timeUntilBreak = p.readInt();
        int breakTime      = p.readInt();
        int turnDuration   = p.readInt();
        int roundDuration  = p.readInt();

        // current positions within timeframes
        getContext().getControlScene().getBnavComponent().setTime(turnTime);
        getContext().getBattleScene().getInformation().setTime(gameTime);

        // breaks
        getContext().getBattleScene().getInformation().setTimeUntilBreak(timeUntilBreak);
        getContext().getBattleScene().getInformation().setBreakTime(breakTime);

        // durations
        getContext().setTurnDuration(turnDuration);
        getContext().setRoundDuration(roundDuration);
    }

    @Override
    public int getSize() {
        return 8;
    }
}
