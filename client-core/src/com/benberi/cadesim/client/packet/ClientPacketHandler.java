package com.benberi.cadesim.client.packet;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.in.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * The packet handler
 */
public class ClientPacketHandler {

    private Logger logger = Logger.getLogger("Packet Handler");

    /**
     * The game context
     */
    private GameContext context;

    /**
     * Received packets queue
     */
    private Queue<Packet> packetQueue = new LinkedList<Packet>();

    /**
     * The packets map
     */
    private Map<Integer, ClientPacketExecutor> packets = new HashMap<Integer, ClientPacketExecutor>();

    public ClientPacketHandler(GameContext context) {
        this.context = context;
        registerPackets();
    }

    /**
     * Ticks the packets queue
     */
    public void tickQueue() {
        if (packetQueue.isEmpty()) {
            return;
        }

        Packet packet = packetQueue.poll();
        handle(packet);
    }

    /**
     * Adds a packet to the queue
     * @param packet    The packet to add
     */
    public void queuePacket(Packet packet) {
        packetQueue.add(packet);
    }

    /**
     * Handles a packet
     * @param packet    The packet to handle
     */
    public void handle(Packet packet) {
    	try {
	        for (Map.Entry<Integer, ClientPacketExecutor> entry : packets.entrySet()) {
	            int opcode = entry.getKey();
	            ClientPacketExecutor p = entry.getValue();
	            if (packet.getOpcode() == opcode) {
	                p.execute(packet);
	                packet.getBuffer().release();
	                return;
	            }
	        }
	        packet.getBuffer().release();
	        logger.info("Packet with unknown opcode: " + packet.getOpcode() + " got dropped.");
    	}catch(NullPointerException e) {
    		//TO-DO
	    }
    }

    private void registerPackets() {
        packets.put(0, new LoginResponsePacket(context));
        packets.put(1, new SendMapPacket(context));
        packets.put(2, new AddPlayerShip(context));
        packets.put(3, new SetTimePacket(context));
        packets.put(4, new SendDamagePacket(context));
        packets.put(5, new SendMoveTokensPacket(context));
        packets.put(6, new MoveSlotPlacedPacket(context));
        packets.put(7, new TurnAnimationPacket(context));
        packets.put(8, new SetPlayersPacket(context));
        packets.put(9, new MovesBarUpdate(context));
        packets.put(10, new CannonSlotPlacedPacket(context));
        packets.put(11, new TargetSealPacket(context));
        packets.put(12, new PlayerRespawnPacket(context));
        packets.put(13, new SendPositionsPacket(context));
        packets.put(14, new RemovePlayerShip(context));
        packets.put(15, new SendMovesPacket(context));
        packets.put(16, new SetFlagsPacket(context));
        packets.put(17, new SetPlayerFlagsPacket(context));
        packets.put(18, new SetTeamNamesPacket(context));
        packets.put(19, new ReceiveMessagePacket(context));
        packets.put(20, new ListAllMapsPacket(context));
    }
    
    public void clearPackets() {
    	packets.clear();
    	packetQueue.clear();
    }
}
