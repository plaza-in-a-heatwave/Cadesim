package com.benberi.cadesim.server.model.player;
import com.benberi.cadesim.server.config.ServerConfiguration;

import com.benberi.cadesim.server.codec.packet.IncomingPacket;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.codec.packet.out.impl.*;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.model.player.move.MoveTokensHandler;
import com.benberi.cadesim.server.model.player.move.MoveType;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerPacketManager {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The outgoing packets queue
     */
    private Queue<OutgoingPacket> outgoingPackets = new ConcurrentLinkedQueue<>();
    /**
     * The incoming packets queue
     */
    private Queue<IncomingPacket> incomingPackets = new ConcurrentLinkedQueue<>();

    public PlayerPacketManager(Player p) {
        this.player = p;
    }

    /**
     * Sends the game board to the client
     */
    public void sendBoard() {
        SendMapPacket packet = new SendMapPacket();
        packet.setMap(player.getContext().getMap());
        player.sendPacket(packet);
    }
    
    /**
     * Sends the team names to the client
     */
    public void sendTeams() {
        SendTeamNamesPacket packet = new SendTeamNamesPacket();
        player.sendPacket(packet);
    }

    /**
     * Sends animation structure
     */
    public void sendAnimationStructure() {
        SendPlayersAnimationStructurePacket packet = new SendPlayersAnimationStructurePacket();
        packet.setPlayers(player.getContext().getPlayerManager().listRegisteredPlayers());

        player.sendPacket(packet);
    }

    /**
     * Sends ship damage packet
     */
    public void sendDamage() {
        SendDamagePacket packet = new SendDamagePacket();
        packet.setDamage((int)player.getVessel().getDamagePercentage());
        packet.setBilge((int)player.getVessel().getBilgePercentage());
        player.sendPacket(packet);
    }

    /**
     * Sets the target seal position
     */
    public void sendTargetSealPosition() {
        SendTargetSealPosition packet = new SendTargetSealPosition();
        packet.setPosition(player.getMoveTokens().getTargetSeal().getId());

        player.sendPacket(packet);
    }

    /**
     * Sends move tokens to the client from the token handler
     */
    public void sendTokens() {
    	SendMoveTokensPacket packet = new SendMoveTokensPacket();
    	// disallow all moves until player is allowed to move again
    	if (player.getTurnsUntilControl() > 0) {
    		packet.setLeft(0);
    		packet.setRight(0);
    		packet.setForward(0);
    		packet.setCannons(0);
    	} else
    	{
    	    MoveTokensHandler m = player.getMoveTokens();
            packet.setLeft(m.countLeftMoves());
            packet.setRight(m.countRightMoves());
            packet.setForward(m.countForwardMoves());
            packet.setNew(m.getNewLeft(), m.getNewForward(), m.getNewRight());
            packet.setCannons(m.getCannons());
    	}

        player.sendPacket(packet);
    }

    /**
     * @deprecated
     * Sends move place verification
     *
     * @param slot  The slot to place at
     * @param move  The placed move
     */
    public void sendMovePlaceVerification(int slot, int move) {
        MovePlaceVerificationPacket packet = new MovePlaceVerificationPacket();
        packet.setSlot(slot);
        packet.setMove(move);
        player.sendPacket(packet);
    }

    /**
     * @deprecated
     * Sends cannon place verification
     *
     * @param slot  The slot to place at
     * @param side  The side to place at
     */
    public void sendPlaceCannonVerification(int slot, int side) {
        CannonPlaceVerificationPacket p = new CannonPlaceVerificationPacket();
        p.setSide(side);
        p.setSlot(slot);
        int amount = side == 0 ? player.getMoves().getLeftCannons(slot) : player.getMoves().getRightCannons(slot);
        p.setAmount(amount);
        player.sendPacket(p);
    }

    /**
     * Sends a move bar of another player
     *
     * @param other The player's move bar to show
     */
    public void sendMoveBar(Player other) {
        SendPlayerMoveBar packet = new SendPlayerMoveBar();
        packet.setPlayerName(other.getName());
        packet.setMoves(other.getMoves());
        player.sendPacket(packet);
    }

    /**
     * Sends the time to the player
     */
    public void sendTime() {
        BlockadeTimeMachine tm = player.getContext().getTimeMachine();
        PlayerManager pm = player.getContext().getPlayerManager();
        int gameTime = tm.getRoundTime();
        int turnTime = tm.getTurnTime();
        SendTimePacket packet = new SendTimePacket();

        packet.setGameTime((gameTime>0)?gameTime:0);
        packet.setTurnTime((turnTime>0)?turnTime:0);
        packet.setTimeUntilBreak(tm.getTimeUntilBreak() / 10);
        packet.setBreakTime(tm.getBreakTime() / 10);
        packet.setTurnDuration(pm.getTurnDuration() / 10);
        packet.setRoundDuration(pm.getRoundDuration() / 10);

        player.sendPacket(packet);
    }

    /**
     * Sends another player
     *
     * @param other The player to send
     */
    public void sendPlayer(Player other) {
        AddPlayerShipPacket packet = new AddPlayerShipPacket();
        packet.setName(other.getName());
        packet.setX(other.getX());
        packet.setY(other.getY());
        packet.setFace(other.getFace());
        packet.setShip(other.getVessel().getID());
        packet.setTeam(other.getTeam().getID());
        player.sendPacket(packet);
    }

    /**
     * Sends all players
     */
    public void sendPlayers() {
        List<Player> players = player.getContext().getPlayerManager().listRegisteredPlayers();
        SendPlayersPacket sendPlayersPacket = new SendPlayersPacket();
        sendPlayersPacket.setPlayers(players);
        player.sendPacket(sendPlayersPacket);

        for (Player p : players) {
            sendMoveBar(p);
        }
    }

    /**
     * Sends a login response code
     *
     * @param responseCode The login response code
     */
    public void sendLoginResponse(int responseCode) {
        LoginResponsePacket login = new LoginResponsePacket();
        
        // the actual response
        login.setResponse(responseCode);
        
        // and any constants client needs to know in advance
        // descale constants before sending
        login.setTurnDuration(ServerConfiguration.getTurnDuration()   / 10);
        login.setRoundDuration(ServerConfiguration.getRoundDuration() / 10);
        player.sendPacket(login);
    }

    public void sendRespawn(Player p) {
        RespawnPlayerPacket packet = new RespawnPlayerPacket();
        packet.setName(p.getName());
        packet.setX(p.getX());
        packet.setY(p.getY());
        packet.setFace(p.getFace());

        player.sendPacket(packet);
    }

    public void sendPositions() {
        SendPlayerPositions packet = new SendPlayerPositions();
        packet.setPlayers(player.getContext().getPlayerManager().listRegisteredPlayers());

        player.sendPacket(packet);
    }

    public void sendRemovePlayer(Player p) {
        RemovePlayerShipPacket packet = new RemovePlayerShipPacket();
        packet.setName(p.getName());

        player.sendPacket(packet);
    }

    public void sendSelectedMoves() {
        SendSelectedMoves packet = new SendSelectedMoves();
        byte[] moves = new byte[4];
        byte[] left = new byte[4];
        byte[] right = new byte[4];

        for (int i = 0; i < 4; i++) {
            MoveType move = player.getMoves().getMove(i);
            int l = player.getMoves().getLeftCannons(i);
            int r = player.getMoves().getRightCannons(i);

            moves[i] = (byte) move.getId();
            left[i] = (byte) l;
            right[i] = (byte) r;
        }


        packet.setMoves(moves);
        packet.setLeft(left);
        packet.setRight(right);

        player.sendPacket(packet);
    }

    public void sendFlags() {
        SetFlagsPacket packet = new SetFlagsPacket();
        packet.setPointsDefender(player.getContext().getPlayerManager().getPointsDefender());
        packet.setPointsAttacker(player.getContext().getPlayerManager().getPointsAttacker());
        packet.setFlags(player.getContext().getMap().getFlags());

        player.sendPacket(packet);
    }

    public void sendPlayerFlags() {
        if (player.getFlags() == null) {
            return;
        }
        SetPlayerFlagsPacket packet = new SetPlayerFlagsPacket();
        packet.setPlayers(player.getContext().getPlayerManager().listRegisteredPlayers());

        player.sendPacket(packet);
    }
    
    public void sendReceiveMessage(String player, String message) {
    	ReceiveMessagePacket packet = new ReceiveMessagePacket();
    	packet.setPlayer(player);
    	packet.setMessage(message);
    	this.player.sendPacket(packet);
    }

    public void queueOutgoing(OutgoingPacket packet) {
        outgoingPackets.add(packet);
    }

    public void queueIncoming(IncomingPacket packet) {
        incomingPackets.add(packet);
    }

    /**
     * Handles outgoing packets of player
     */
    public void queueOutgoingPackets() {
        int count = 0;
        while(!outgoingPackets.isEmpty()) {
            outgoingPackets.poll().send(player);
            if (count > Constants.OUTGOING_PACKETS_PLAYER_PER_TICK) {
                break;
            }
            count++;
        }
    }

    /**
     * Handles incoming packets of the player
     */
    public void queueIncomingPackets() {
        int count = 0;
        while(!incomingPackets.isEmpty()) {
            IncomingPacket packet = incomingPackets.poll();
            player.getContext().getPackets().process(packet.getChannel(), packet.getPacket());
            packet.getPacket().getBuffer().release(); // bugfix #4 netty memory leak
            if (count > Constants.INCOMING_PACKETS_PLAYER_PER_TICK) {
                break;
            }
            count++;
        }
    }
}
