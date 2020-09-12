package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * the manuaver slot in this case is the piece which blocks
 * larger ships moving more than 3x in a turn.
 */
public class ManuaverSlotChanged extends ServerPacketExecutor {

    public ManuaverSlotChanged(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        int newSlot = p.readByte();
        Vessel v = pl.getVessel();
        if (v.has3Moves()) {
            pl.getMoves().setManuaverSlot(newSlot);
        }
    }
}
