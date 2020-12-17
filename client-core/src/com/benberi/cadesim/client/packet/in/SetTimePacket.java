package com.benberi.cadesim.client.packet.in;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketExecutor;
import com.benberi.cadesim.client.packet.out.ClientAlivePacket;

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
        byte counter       = p.readByte();

        // current positions within timeframes
        getContext().getControl().setTime(turnTime);
        getContext().getBattleScreen().getInformation().setTime(gameTime);
        getContext().setTurnDuration(getContext().getTurnSetting()); //
        // breaks
        getContext().getBattleScreen().getInformation().setTimeUntilBreak(timeUntilBreak);
        getContext().getBattleScreen().getInformation().setBreakTime(breakTime);

        // send a response packet to show client is still alive
        ClientAlivePacket packet = new ClientAlivePacket();
        packet.setCounter(getContext().getNextLagCounter()); // server can calculate whether we are lagging or not.
        getContext().sendPacket(packet);
        getContext().setLagCounter(counter); // re-sync with the server
    }

    @Override
    public int getSize() {
        return 8;
    }
}
