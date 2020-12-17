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
        // #83 if lag test mode, simulate unresponsiveness
        if (context.isLagTestMode()) {
            packet.getBuffer().release(); // release to avoid netty memory leak
            return;
        }

        // #83 only handle certain packets during animation
        if (context.getBattleScreen() != null && context.isConnected() && context.getBattleScreen().isAnimationOngoing()) {
        	switch (packet.getOpcode()) {
            case IncomingPackets.SET_TIME:
            case IncomingPackets.RECEIVE_MESSAGE:
            case IncomingPackets.SEND_DAMAGE:
            case IncomingPackets.SEND_MOVE_TOKENS:
            case IncomingPackets.TARGET_SEAL:
            case IncomingPackets.LIST_ALL_MAPS:
            case IncomingPackets.GAME_SETTINGS:
                // only handle these packets
                break;
            default:
                // put these packets back in the queue for now
                queuePacket(packet);
                return;
            }
        }
        
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
        packets.put(IncomingPackets.LOGIN_RESPONSE,      new LoginResponsePacket(context));
        packets.put(IncomingPackets.SEND_MAP,            new SendMapPacket(context));
        packets.put(IncomingPackets.ADD_PLAYER_SHIP,     new AddPlayerShip(context));
        packets.put(IncomingPackets.SET_TIME,            new SetTimePacket(context));
        packets.put(IncomingPackets.SEND_DAMAGE,         new SendDamagePacket(context));
        packets.put(IncomingPackets.SEND_MOVE_TOKENS,    new SendMoveTokensPacket(context));
        packets.put(IncomingPackets.MOVE_SLOT_PLACED,    new MoveSlotPlacedPacket(context));
        packets.put(IncomingPackets.TURN_ANIMATION,      new TurnAnimationPacket(context));
        packets.put(IncomingPackets.SET_PLAYERS,         new SetPlayersPacket(context));
        packets.put(IncomingPackets.MOVES_BAR_UPDATE,    new MovesBarUpdate(context));
        packets.put(IncomingPackets.CANNONS_SLOT_PLACED, new CannonSlotPlacedPacket(context));
        packets.put(IncomingPackets.TARGET_SEAL,         new TargetSealPacket(context));
        packets.put(IncomingPackets.PLAYER_RESPAWN,      new PlayerRespawnPacket(context));
        packets.put(IncomingPackets.SEND_POSITIONS,      new SendPositionsPacket(context));
        packets.put(IncomingPackets.REMOVE_PLAYER_SHIP,  new RemovePlayerShip(context));
        packets.put(IncomingPackets.SEND_MOVES,          new SendMovesPacket(context));
        packets.put(IncomingPackets.SET_FLAGS,           new SetFlagsPacket(context));
        packets.put(IncomingPackets.SET_TEAM_NAMES,      new SetTeamNamesPacket(context));
        packets.put(IncomingPackets.RECEIVE_MESSAGE,     new ReceiveMessagePacket(context));
        packets.put(IncomingPackets.LIST_ALL_MAPS,       new ListAllMapsPacket(context));
        packets.put(IncomingPackets.GAME_SETTINGS,       new ReceiveGameSettings(context));
        packets.put(IncomingPackets.SET_TEAM,            new ReceiveTeamPacket(context));
    }
    
    public void clearPackets() {
    	packets.clear();
    	packetQueue.clear();
    }
}
