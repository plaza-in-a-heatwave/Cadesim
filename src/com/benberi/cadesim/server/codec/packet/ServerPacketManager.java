package com.benberi.cadesim.server.codec.packet;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.IncomingPackets;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.codec.packet.in.*;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

public class ServerPacketManager {

    /**
     * The packet executors
     *
     * Every packet registered incoming packet in the server
     */
    private Map<Integer, ServerPacketExecutor> executors = new HashMap<Integer, ServerPacketExecutor>();

    /**
     * The packets queue
     */
    private Queue<IncomingPacket> packetQueue = new ConcurrentLinkedQueue<>();


    /**
     * The server context
     */
    private ServerContext context;

    public ServerPacketManager(ServerContext context) {
        this.context = context;
        registerPackets();
    }

    /**
     * Queues all packets and executes them
     */
    public void queuePackets() {
        for (Player p : context.getPlayerManager().getPlayers()) {
            p.getPackets().queueIncomingPackets();
        }
    }

    /**
     * Registers packet executors
     */
    private void registerPackets() {
        executors.put(IncomingPackets.LOGIN_PACKET, new PlayerLoginPacket(context));
        executors.put(IncomingPackets.PLACE_MOVE, new PlayerPlaceMovePacket(context));
        executors.put(IncomingPackets.MANUAVER_SLOT_CHANGED, new ManuaverSlotChanged(context));
        executors.put(IncomingPackets.CANNON_PLACE, new PlayerPlaceCannonPacket(context));
        executors.put(IncomingPackets.SEAL_TOGGLE, new SealTogglePacket(context));
        executors.put(IncomingPackets.SET_SEAL_TARGET, new SetSealGenerationTargetPacket(context));
        executors.put(IncomingPackets.TURN_FINISH_NOTIFICATION, new TurnFinishNotification(context));
        executors.put(IncomingPackets.OCEANSIDE_REQUEST, new OceansideRequestPacket(context));
        executors.put(IncomingPackets.POST_MESSAGE, new PostMessagePacket(context));
    }

    /**
     * Processes a packet
     * @param c         The sender channel
     * @param packet    The packet
     * @return  status
     */
    public boolean process(Channel c, Packet packet) {
        Player p = context.getPlayerManager().getPlayerByChannel(c);
        if (p == null) {
        	SocketAddress sockaddr = c.remoteAddress();
        	if (sockaddr instanceof InetSocketAddress) {
        		String ipaddr = ((InetSocketAddress)sockaddr).getHostString();
        		String port   = Integer.toString(((InetSocketAddress)sockaddr).getPort());
        		ServerContext.log("Player not found for channel " + ipaddr + ":" + port + "(packet: " + packet.getOpcode() + ")");
        	} else {
        		// SocketAddress is abstract - but usually used with inet...
        		ServerContext.log("Player not found for non-inet channel (packet: " + packet.getOpcode() + ")");
        	}
            
            return false;
        }

        // Drop packet if player is not registered and sending other packets than login
        if (!p.isRegistered() && packet.getOpcode() != IncomingPackets.LOGIN_PACKET) {
            ServerContext.log("Channel not registered yet! packet: " + packet.getOpcode());
            return false;
        }
        for(Map.Entry<Integer, ServerPacketExecutor> entry : executors.entrySet()) {
            int opcode = entry.getKey();
            ServerPacketExecutor executor = entry.getValue();

            if (packet.getOpcode() == opcode) {
                try {
                    executor.execute(p, packet);
                }
                catch (Exception e) {
                    ServerContext.log(
                    		"WARNING - malformed high-level packet from " +
                    		p.getChannel().remoteAddress() +
                    		" error message: " + e.getMessage() + ", " +
                    		e.getClass().getName() + " (player was kicked)"
                    );
                    c.disconnect();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a packet to the queue
     * @param p The incoming packet
     */
    public void addToQueue(IncomingPacket p) {
        packetQueue.add(p);
    }
}
