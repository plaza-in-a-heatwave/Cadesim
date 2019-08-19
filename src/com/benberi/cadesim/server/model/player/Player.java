package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.cade.map.BlockadeMap;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.player.collision.PlayerCollisionStorage;
import com.benberi.cadesim.server.model.player.domain.JobbersQuality;
import com.benberi.cadesim.server.model.player.domain.MoveGenerator;
import com.benberi.cadesim.server.model.player.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.player.move.MoveTokensHandler;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Player extends Position {



    /**
     * The channel of the player
     */
    private Channel channel;

    /**
     * The packet manager
     */
    private PlayerPacketManager packets;

    /**
     * The name of the player
     */
    private String name = "";

    /**
     * Player's vessel
     */
    private Vessel vessel;

    /**
     * The team
     */
    private Team team;

    /**
     * The turn move handler
     */
    private TurnMoveHandler moves;

    /**
     * Move tokens handler
     */
    private MoveTokensHandler tokens;

    /**
     * The animation structure
     */
    private MoveAnimationStructure animation = new MoveAnimationStructure();

    private JobbersQuality jobbersQuality = JobbersQuality.ELITE;

    /**
     * The face of the player (rotation id)
     */
    private VesselFace face = VesselFace.EAST;

    private MoveGenerator moveGenerator;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * If the player is registered
     */
    private boolean isRegistered;

    /**
     * The last damage update
     */
    private long lastDamageUpdate;

    /**
     * The turn the ship sunk at
     */
    private int sunkTurn = -1;

    private boolean outOfSafe = false;

    /**
     * Count delay for ship to be manauverable again
     */
    private int turnsUntilControl = 0; // manauverable now

    /**
     * The collision storage
     */
    private PlayerCollisionStorage collisionStorage;

    /**
     * If the player needs a respawn
     */
    private boolean needsRespawn;

    /**
     * Mark turn animation finished client-sided notification
     */
    private boolean turnFinished;

    /**
     * The waiting time for animation to finish
     */
    private int turnFinishWaitingTicks;
    private List<Flag> flags;

    public Player(ServerContext ctx, Channel c) {
        this.channel = c;
        this.context = ctx;
        this.packets = new PlayerPacketManager(this);
        this.moveGenerator = new MoveGenerator(this);
        this.tokens = new MoveTokensHandler(this);
        this.moves = new TurnMoveHandler(this);
        this.collisionStorage = new PlayerCollisionStorage(this);

        set(-1, -1);
    }

    /**
     * Logical updates during the game
     */
    public void update() {
        if (vessel.getDamagePercentage() >= jobbersQuality.getMinDamageForBilge()) {
            double bilge = Constants.BILGE_INCREASE_RATE_PER_TICK + (vessel.getDamagePercentage() / 100) / 10; // Rate increases as damage is higher
            vessel.appendBilge(bilge);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        if (vessel.getDamagePercentage() > 0) {
            double rate = jobbersQuality.getFixRate() / ServerConfiguration.getTurnDuration();
            vessel.decreaseDamage(rate);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        if (vessel.getBilgePercentage() > 0) {
            double rate = (jobbersQuality.getBilgeFixRate() / ServerConfiguration.getTurnDuration()) - (vessel.getDamage() / 10000);
            vessel.decreaseBilge(rate);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        moveGenerator.update();
    }

    @Override
    public Position set(Position pos) {
        if (!needsRespawn) {
            if (!outOfSafe && !context.getMap().isSafe(pos)) {
                this.outOfSafe = true;
            }

            if (outOfSafe && context.getMap().isSafe(pos)) {
                needsRespawn = true;
            }
        }
        return super.set(pos);
    }

    /**
     * Sends a packet
     *
     * @param packet The packet to send
     */
    public void sendPacket(OutgoingPacket packet) {
        packets.queueOutgoing(packet);
    }

    /**
     * Gets the channel
     *
     * @return {@link #channel}
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets the jobbers quality set
     *
     * @return {@link JobbersQuality}
     */
    public JobbersQuality getJobbersQuality() {
        return jobbersQuality;
    }

    /**
     * Checks if hes out of safe
     * @return  The out of safe state
     */
    public boolean isOutOfSafe() {
        return outOfSafe;
    }

    /**
     * Sets out of safe zone state
     * @param flag  The state to set
     */
    public void setOutOfSafe(boolean flag) {
        this.outOfSafe = flag;
    }

    /**
     * Gets the collision storage
     *
     * @return {@link #collisionStorage}
     */
    public PlayerCollisionStorage getCollisionStorage() {
        return collisionStorage;
    }

    /**
     * Gets the server context
     *
     * @return {@link #context}
     */
    public ServerContext getContext() {
        return context;
    }

    /**
     * Gets the player name
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the vessel instance
     *
     * @return {@link #vessel}
     */
    public Vessel getVessel() {
        return vessel;
    }


    /**
     * Gets the player face
     *
     * @return  {@link VesselFace}
     */
    public VesselFace getFace() {
        return face;
    }

    /**
     * Sets the vessel face
     *
     * @param face  The new vessel face to set
     */
    public void setFace(VesselFace face) {
        this.face = face;
    }

    public Team getTeam() {
        return this.team;
    }

    /**
     * Sets the player registered
     *
     * @param name  The name to register
     */
    public void register(String name, int ship, int team) {
        this.name = name;
        this.team = Team.forId(team);
        this.vessel = Vessel.createVesselByType(this, ship);
        this.isRegistered = true;
        ServerContext.log(
        		"Registered player \"" + name + "\", " +
        		Team.teamIDToString(team) + ", " +
        		Vessel.vesselIDToString(ship) + "."
        );
        respawn();
    }

    /**
     * Respawns the player
     */
    public void respawn() {
        respawnHandler(false);
    }
    
    /**
     * respawn oceanside - when button pressed
     */
    public void requestRespawnToOceanSide() {
    	if (isInSafe()) { // only if in safe
	    	respawnHandler(true);
	    	packets.sendRespawn(this);
    	}
    }
    
    /**
     * Handle respawn logic
     */
    private void respawnHandler(boolean oceanSide) {
    	int x;
        int y;
        if ((team == Team.GREEN) && (!oceanSide)) {
            y = BlockadeMap.MAP_HEIGHT-1;
            x = ThreadLocalRandom.current().nextInt(0,BlockadeMap.MAP_WIDTH);
            setFace(VesselFace.SOUTH);
        } else {
            y = 0;
            x = ThreadLocalRandom.current().nextInt(0,BlockadeMap.MAP_WIDTH);
            setFace(VesselFace.NORTH);
        }
        while(context.getPlayerManager().getPlayerByPosition(x, y) != null) {
            x++;
            x = x % BlockadeMap.MAP_WIDTH;
        }
        set(x, y);
        setNeedsRespawn(false);
        outOfSafe = false;
        vessel.resetDamageAndBilge();
    }

    /**
     * Sets need respawn flag
     * @param flag  The flag to set
     */
    public void setNeedsRespawn(boolean flag) {
        this.needsRespawn = flag;
    }

    /**
     * Checks if needs respawn
     * @return  If needs respawn
     */
    public boolean isNeedsRespawn() {
        return needsRespawn;
    }

    /**
     * Gets the animation structure of the player
     *
     * @return {@link MoveAnimationStructure}
     */
    public MoveAnimationStructure getAnimationStructure() {
        return animation;
    }

    /**
     * Gets the player's selected turn moves
     *
     * @return {@link TurnMoveHandler}
     */
    public TurnMoveHandler getMoves() {
        return moves;
    }

    /**
     * If the player is registered
     *
     * @return  {@link #isRegistered}
     */
    public boolean isRegistered() {
        return this.isRegistered;
    }

    /**
     * Gets the move tokens
     *
     * @return {@link MoveTokensHandler}
     */
    public MoveTokensHandler getMoveTokens() {
        return tokens;
    }

    /**
     * Proper equality check
     *
     * @param o The object to check - supports for Player, Channel
     * @return <code>TRUE</code> If the given object equals to either channel, player or other.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            return this == o;
        } else if (o instanceof Channel) {
            return this.channel == o;
        }
        return super.equals(o);
    }
    /**
     * Places a move
     * @param slot  The slot to place at
     * @param move  The move to place
     */
    public void placeMove(int slot, int move) {
        if (vessel.isManuaver() && moves.getManuaverSlot() == slot) {
            return;
        }
        MoveType moveType = MoveType.forId(move);
        if (moveType != null) {
            MoveType currentMove = moves.getMove(slot);
            if (currentMove == null) {
                currentMove = MoveType.FORWARD;
                ServerContext.log("MOVE TYPE IS NULL, set to forward for " + name);
            }
            if (tokens.useTokenForMove(moveType)) {
                if (currentMove != MoveType.NONE) {
                    tokens.returnMove(currentMove);
                }

                moves.setMove(slot, moveType);
                //packets.sendMovePlaceVerification(slot, move);
                packets.sendSelectedMoves();
                packets.sendTokens();
            }
            else {
                if (currentMove != MoveType.NONE) {
                    tokens.returnMove(currentMove);
                    //packets.sendMovePlaceVerification(slot, MoveType.NONE.getId());
                    packets.sendSelectedMoves();
                    packets.sendTokens();
                }
            }

            context.getPlayerManager().sendMoveBar(this);
        }
    }

    /**
     * Attempts to place a cannon
     *
     * @param slot  The slot to place
     * @param side  The side to place
     */
    public void placeCannon(int slot, int side) {
        if (tokens.getCannons() <= 0) {
            return;
        }
        if (side == 0) {
            int shoots = moves.getLeftCannons(slot);
            if (shoots == 0) {
                moves.setLeftCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else {
                if (vessel.isDualCannon()) {
                    if (shoots < 2) {
                        moves.setLeftCannons(slot, 2);
                        tokens.removeCannons(1);
                    }
                    else {
                        moves.setLeftCannons(slot, 0);
                        tokens.addCannons(2);
                    }
                }
                else {
                    moves.setLeftCannons(slot, 0);
                    tokens.addCannons(1);
                }
            }

           // packets.sendPlaceCannonVerification(slot, 0);
            packets.sendSelectedMoves();
        }
        else {
            int shoots = moves.getRightCannons(slot);
            if (shoots == 0) {
                moves.setRightCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else {
                if (vessel.isDualCannon()) {
                    if (shoots < 2) {
                        moves.setRightCannons(slot, 2);
                        tokens.removeCannons(1);
                    }
                    else {
                        moves.setRightCannons(slot, 0);
                        tokens.addCannons(2);
                    }
                }
                else {
                    moves.setRightCannons(slot, 0);
                    tokens.addCannons(1);
                }
            }

           // packets.sendPlaceCannonVerification(slot, 1);
            packets.sendSelectedMoves();
        }

        packets.sendTokens();
        context.getPlayerManager().sendMoveBar(this);
    }

    public boolean isInSafe() {
        return context.getMap().isSafe(this);
    }

    /**
     * Gets the packet handler
     *
     * @return {@link PlayerPacketManager}
     */
    public PlayerPacketManager getPackets() {
        return packets;
    }

    public void processAfterTurnUpdate() {
        // if we just sunk, return control after x turns
        if (getTurnsUntilControl() > 0) {
            setTurnsUntilControl(getTurnsUntilControl() - 1);

            // reset tokens once we're allowed to move again
            if (getTurnsUntilControl() == 0) {
            	tokens = new MoveTokensHandler(this);
            }
        }

        tokens.clearTemp();
        tokens.tickExpiration();

        // Send moves
        packets.sendAnimationStructure();

        packets.sendDamage();
        moves.resetTurn();
        packets.sendTokens();

    }

    public void setSunk(int sunk) {
        this.sunkTurn = sunk;

        // if we just sunk, return control after x turns
        // +1 - gets deprecated at end of turn
        this.setTurnsUntilControl(ServerConfiguration.getRespawnDelay() + 1);
    }

    public boolean isSunk() {
        return sunkTurn > -1;
    }

    public int getSunkTurn() {
        return sunkTurn;
    }

    public void giveLife() {
        sunkTurn = -1;
        vessel.resetDamageAndBilge();
        tokens = new MoveTokensHandler(this);
        moves = new TurnMoveHandler(this);
        animation = new MoveAnimationStructure();

        respawn();

        for (Player p : context.getPlayerManager().listRegisteredPlayers()) {
            p.packets.sendRespawn(this);
        }

        outOfSafe = false;
        packets.sendDamage();
        packets.sendTokens();
    }

    public int getTurnFinishWaitingTicks() {
        return turnFinishWaitingTicks;
    }

    public void updateTurnFinishWaitingTicks() {
        this.turnFinishWaitingTicks++;
    }

    public boolean isTurnFinished() {
        return turnFinished;
    }

    public void setTurnFinished(boolean turnFinished) {
        this.turnFinished = turnFinished;
    }

    public void resetWaitingTicks() {
        this.turnFinishWaitingTicks = 0;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

	public int getTurnsUntilControl() {
		return turnsUntilControl;
	}

	public void setTurnsUntilControl(int turnsUntilControl) {
		this.turnsUntilControl = turnsUntilControl;
	}
}
