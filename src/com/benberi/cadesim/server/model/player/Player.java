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

    private JobbersQuality jobbersQuality = ServerConfiguration.getJobbersQuality();

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
    private boolean isRegistered = false;
    
    /**
     * When the player joined
     * Used to timeout players who join but never register
     */
    private long joinTime;

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
     * Any respawn (except the first entry) will leave it
     * Unable to be manauvered for n turns
     */
    private int turnsUntilControl = 0;  // manauverable now

    /**
     * Resolve bug spawning in - ship has bad coords first time
     * so flag first run to ensure landside sailing
     */
    private boolean firstEntry = true;

    public boolean isFirstEntry() {
		return firstEntry;
	}

	public void setFirstEntry(boolean firstEntry) {
		this.firstEntry = firstEntry;
	}

    /**
     * The collision storage
     */
    private PlayerCollisionStorage collisionStorage;

    /**
     * If the player needs a respawn
     * TODO convert to enum
     */
    private boolean needsRespawn;                 // we need a respawn
    private boolean enteredSafeLandside = false;  // reason why
    private boolean enteredSafeOceanside = false; // "

    /**
     * Mark turn animation finished client-sided notification
     */
    private boolean turnFinished;

    /**
     * The waiting time for animation to finish
     */
    private int turnFinishWaitingTicks = 0;
    private List<Flag> flags;

    public Player(ServerContext ctx, Channel c) {
    	this.joinTime = System.currentTimeMillis();
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
    	// model bilge additions/reductions as piecewise linear, 3 segments:
    	// ----------------+-----------------------------------------------
    	// b <= 0          | no change
    	// ----------------+-----------------------------------------------
    	// 0  <  b < t1    | bilge reducing   at a constant (maximum)  rate
    	// t1 <= b < t2    | bilge reducing   at a linearly-increasing rate
    	// t2 <= b < 100   | bilge increasing at a linearly-increasing rate
    	// ----------------+-----------------------------------------------
    	// b >= 100        | no change
        if (vessel.getDamagePercentage() >= jobbersQuality.getMinDamageForBilge()) {
            // add bilge, scaling up linearly beyond MinDamageForBilge until 100% (e.g. 60-100%)
        	double t2 = jobbersQuality.getMinDamageForBilge();
            double scale = (vessel.getDamagePercentage() - t2) / (100.0 - t2);            
            double min = Constants.MIN_BILGE_INCREASE_PERCENT_PER_SEC / (1000.0 / Constants.SERVICE_LOOP_DELAY);
            double max = Constants.MAX_BILGE_INCREASE_PERCENT_PER_SEC / (1000.0 / Constants.SERVICE_LOOP_DELAY);
  
            vessel.appendBilge((scale * (max - min)) + min);
        }
        else if (vessel.getDamagePercentage() >= jobbersQuality.getBilgeMaxReductionThreshold() && vessel.getBilgePercentage() > 0)
        {
        	// remove bilge, scaling down linearly from MinDamageForBilge() to BilgeMaxReductionThreshold
        	double t2 = jobbersQuality.getMinDamageForBilge();
        	double t1 = jobbersQuality.getBilgeMaxReductionThreshold();
        	double scale = (t2 - vessel.getDamagePercentage()) / (t2 - t1);
        	double min = 0;
        	double max = jobbersQuality.getBilgeFixPerTick();
        	
        	vessel.decreaseBilge((scale * (max - min)) + min);
        }
        else if (vessel.getBilgePercentage() > 0) {
        	// remove bilge at the maximum rate permitted per tick
            vessel.decreaseBilge(jobbersQuality.getBilgeFixPerTick());
        }
        else {
        	// no change
        }

        // reduce carp if needed
        if (vessel.getDamagePercentage() > 0) {
            vessel.decreaseDamage(jobbersQuality.getFixRatePerTick());
        }

        // send packets if needed
        if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
            packets.sendDamage();
            lastDamageUpdate = System.currentTimeMillis();
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
                outOfSafe = false;

                // mark which safe zone we sailed through
                if (context.getMap().isSafeLandside(pos))
                {
                    enteredSafeLandside = true;
                }
                else if (context.getMap().isSafeOceanside(pos))
                {
                    enteredSafeOceanside = true;
                }
                else
                {
                    ServerContext.log("ERROR - safe zone type was neither land or ocean. Treating it as sink.");
                }
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
        		"[player-joined] Registered player \"" + name + "\", " +
        		Team.teamIDToString(team) + ", " +
        		Vessel.VESSEL_IDS.get(ship) + ", on " +
        		channel.remoteAddress() + ". " +
        		context.getPlayerManager().printPlayers()
        );
        respawn();
    }

    /**
     * Respawns the player to the correct side. It is
     * triggered by the server requiring an action e.g.
     * a player entering safe zone or sinking or spawning.
     * This is 1 of 2 wrappers around respawnOnLandside().
     * The other wrapper is requestRespawnToOceanside()
     * which is triggered by a player clicking Go Oceanside.
     */
    public void respawn() {
        // refresh tokens, but leave guns as they were. ships just joining will have 0 guns.
    	tokens.assignDefaultTokens();
        this.getPackets().sendTokens();
        getMoveTokens().setTargetTokenGeneration(MoveType.FORWARD, true);

    	// where to respawn?
    	if (isFirstEntry()) {
    		setFirstEntry(false);
    		// start on land side regardless of where
    		// map says we are currently - it's wrong
    		respawnOnLandside(true);
        } else if (enteredSafeLandside) { // drove into safe
    		// only defenders may use the landside safe zone
    		// so only defenders should be respawned here
    		respawnOnLandside(true);
        } else if (enteredSafeOceanside) { // drove into safe
    		// both teams may use the oceanside safe zone
    		respawnOnLandside(false);
    	} else {
    		context.getPlayerManager().serverBroadcastMessage(this.getName() + " was sunk!");

    		// after sink, return control after x turns
    		this.setTurnsUntilControl(context.getPlayerManager().getRespawnDelay());
    		if (getTurnsUntilControl() > 0)
    		{
    			context.getPlayerManager().serverPrivateMessage(
    				this, "You can't move for " +
    				getTurnsUntilControl() +
    				" turns after sinking"
    			);
    		}

            // sunk, 'respawn' on land side with new ship
    		respawnOnLandside(true);
    	}
    }

	/**
     * respawn on one of two sides
     * @param landSide true if want to spawn on landside else false
     */
    private void respawnOnLandside(boolean landSide) {
    	int x = ThreadLocalRandom.current().nextInt(0,BlockadeMap.MAP_WIDTH);
    	int y = landSide?(BlockadeMap.MAP_HEIGHT-1):0;
        setFace(landSide?VesselFace.SOUTH:VesselFace.NORTH);

        while(context.getPlayerManager().getPlayerByPosition(x, y) != null) {
            x++;
            x = x % BlockadeMap.MAP_WIDTH;
            // TODO what if cant enter the map?
        }
        set(x, y);

        // reset flags
        setNeedsRespawn(false);
        outOfSafe = false;
        enteredSafeLandside = false;
        enteredSafeOceanside = false;
        vessel.resetDamageAndBilge();

        // send packets
        for (Player p:context.getPlayerManager().listRegisteredPlayers()) {
            p.packets.sendRespawn(this); // bugfix players' moves disappearing when anyone else (re)spawns
        }
    }

    /**
     * respawn oceanside - when button pressed
     */
    public void requestRespawnToOceanSide() {
    	// disengage has several modes.
    	String mode = context.getPlayerManager().getDisengageBehavior();
    	if (mode.equals("off"))
    	{
    		// do nothing - can't use the button at all
    		context.getPlayerManager().serverPrivateMessage(
    			this,
    			"Disengage is not enabled at the moment. Start a vote to enable it."
    		);
    	}
    	else if (mode.equals("simple"))
    	{
    		// strictly, in a cade only the attacker would be
        	// able to do this. However in simple mode we provide the ability
    		// for the defender to go Oceanside too to keep things fair.
        	if (!outOfSafe) {
        		if (context.getMap().isSafeLandside(this))
        		{
        			context.getPlayerManager().serverPrivateMessage(this, "Going Oceanside");
        		}
        		respawnOnLandside(false);
        	}
        	else
        	{
        		context.getPlayerManager().serverPrivateMessage(this, "You must be in a safe zone to disengage");
        	}
    	}
    	else if (mode.equals("realistic"))
    	{
    		// can only dis/re if in safe
    		if (!outOfSafe) {
        		if (context.getMap().isSafeLandside(this))
        		{
        			if (getTeam() == Team.ATTACKER)
        			{
        				// attackers in the landside can go oceanside
            			context.getPlayerManager().serverPrivateMessage(this, "Going Oceanside");
            			respawnOnLandside(false);
        			}
        			else
        			{
        				// defenders in the landside can just respawn landside
        				respawnOnLandside(true);
        			}
        			
        		}
        		else
        		{
        			if (getTeam() == Team.ATTACKER)
        			{
        				// attackers in the oceanside can just respawn oceanside
            			respawnOnLandside(false);
        			}
        			else
        			{
        				// defenders in the oceanside can just respawn oceanside
        				respawnOnLandside(false);
        			}
        		}
        	}
        	else
        	{
        		context.getPlayerManager().serverPrivateMessage(this, "You must be in a safe zone to disengage");
        	}
    	}
    	else
    	{
    		ServerContext.log("WARNING - unhandled mode for disengageBehavior: " + mode);
    	}
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
     * Get join time
     */
    public long getJoinTime() {
    	return this.joinTime;
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
	 * helper method to extract an IP from a remoteAddress
	 * they are formatted like:
	 *     /127.0.0.1:1004
	 * we return:
	 *     127.0.0.1
	 */
	public String getIP()
	{
		return getChannel().remoteAddress().toString().replace("/", "").split(":")[0];
	}
    
    /**
     * Places a move
     * @param slot  The slot to place at
     * @param move  The move to place
     */
    public void placeMove(int slot, int move) {
        if (vessel.has3Moves() && moves.getManuaverSlot() == slot) {
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
     * swaps two existing already-placed moves
     */
    public void swapMove(int slot1, int slot2) {
        MoveType tmpMove = moves.getMove(slot1);
        moves.setMove(slot1, moves.getMove(slot2));
        moves.setMove(slot2, tmpMove);

        packets.sendSelectedMoves();
    }

    /**
     * Attempts to place a cannon
     *
     * @param slot  The slot to place
     * @param side  The side to place
     */
    public void placeCannon(int slot, int side) {
    	if (side == 0) {
            int shoots = moves.getLeftCannons(slot);
            if (shoots == 0 && tokens.getCannons() > 0) {
                moves.setLeftCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else if (shoots > 0) {
                if (vessel.isDualCannon()) {
                    if (shoots < 2 && tokens.getCannons() > 0) {
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
            if (shoots == 0 && tokens.getCannons() > 0) {
                moves.setRightCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else if (shoots > 0) {
                if (vessel.isDualCannon()) {
                    if (shoots < 2 && tokens.getCannons() > 0) {
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
            	tokens.assignDefaultTokens();
                tokens.assignCannons(0); // new ships start with 0 cannons
            }
        }

        tokens.clearTemp();
        tokens.tickExpiration();

        // Send moves
        packets.sendAnimationStructure();

        packets.sendDamage();
        moves.resetTurn();
        packets.sendTokens();

        // reset any glitched moves/guns that remain set by client
        packets.sendSelectedMoves();
    }

    public void setSunk(int sunk) {
        this.sunkTurn = sunk;
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
