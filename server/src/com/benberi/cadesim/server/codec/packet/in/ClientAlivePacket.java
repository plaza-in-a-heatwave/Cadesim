package com.benberi.cadesim.server.codec.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.ServerPacketExecutor;

/**
 * the client indicates to the server that it is alive periodically.
 * 
 * Should be greater than Constants.PLAYER_LAG_TIMEOUT
 */
public class ClientAlivePacket extends ServerPacketExecutor {

    public ClientAlivePacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        // compare the counters for byte rollover. "a" needs to catch up with "b".
        int a = Byte.toUnsignedInt(p.readByte());
        int b = Byte.toUnsignedInt(getContext().getPingCounter());
        final int M = Constants.PLAYER_LAG_TOLERABLE_MISSED_PACKETS;
        boolean lagged = false;
        if (a <= 127 && b <= 127) {
            if ((b - a) > M) {
                lagged = true;
            }
        }
        else if (a <= 127 && b > 127) {
            if ((b - a) <= 127) {
                if ((b - a) > M) {
                    lagged = true;
                }
            }
        }
        else if (a > 127 && b <= 127) {
            if ((a - b) > 127) {
                if ((b - (256 - a)) > M) {
                    lagged = true;
                }
            }
        }
        else { // a > 127, b > 127
            if ((b - a) > M) {
                lagged = true;
            }
        }

        if (lagged) {
            ServerContext.log("INFO: player " + pl.getName() + " is lagged, sending catch up packets...");
            pl.getPackets().sendPositions();
            pl.getPackets().sendFlags();
            for (Player other : getContext().getPlayerManager().listRegisteredPlayers()) {
                getContext().getPlayerManager().sendMoveBar(other);
            }
            pl.getPackets().sendSelectedMoves();
        }
        pl.updateResponseTime(System.currentTimeMillis());
    }
}