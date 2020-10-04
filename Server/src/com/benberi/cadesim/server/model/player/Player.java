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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Player extends Position {



    /**
     * The channel of the player
     * Bots have a null channel
     */
    private Channel channel;

    /**
     * The packet manager
     * Bots have a null packet manager
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
    private MoveAnimationStructure animation;

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

    private boolean previouslyOutOfSafe = false;

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
     * did player join during break
     */
    private boolean joinedInBreak;

    public void setJoinedInBreak(boolean value) {
        joinedInBreak = value;
    }

    public boolean didJoinInBreak() {
        return joinedInBreak;
    }
    
    /**
     * when the player was last seen alive
     */
    private long lastAliveMilliseconds;

    public long getLastResponseTime() {
        if (isBot()) { // bots are super responsive
            return System.currentTimeMillis();
        }
        else {
            return lastAliveMilliseconds;
        }
    }

    public void updateResponseTime(long value) {
        lastAliveMilliseconds = value;
    }

    private List<Flag> flags;
    private boolean isBot;

    /**
     * @param ctx   the server context
     * @param c     the channel, or null if isBot
     * If creating a bot, remember to call register().
     */
    public Player(ServerContext ctx, Channel c) {
        this.animation = new MoveAnimationStructure();
    	this.joinTime = System.currentTimeMillis();
        this.context = ctx;
        this.moveGenerator = new MoveGenerator(this);
        this.tokens = new MoveTokensHandler(this);
        this.moves = new TurnMoveHandler(this);
        this.collisionStorage = new PlayerCollisionStorage(this);
        this.packets = new PlayerPacketManager(this);
        this.channel = c;
        this.lastAliveMilliseconds = System.currentTimeMillis();
        this.flags = new ArrayList<Flag>();
        setBot(c == null);

        set(-1, -1); // not spawned
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
    public void set(Position pos) {
        // don't allow the ship to move if it's sunk.
        if (isSunk()) {
            return;
        }
        // handle if ship wanders into safe
        if (!isNeedsRespawn()) {
            if (!getPreviouslyOutOfSafe() && !isInSafe(pos)) {
                setPreviouslyOutOfSafe(true);
            }

            else if (getPreviouslyOutOfSafe() && isInSafe(pos)) {
                setNeedsRespawn(true);
                setPreviouslyOutOfSafe(false);

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

        super.set(pos);
        return;
    }

    /**
     * Sends a packet to a Player
     *
     * @param packet The packet to send
     * Players who are bots cannot receive packets
     */
    public void sendPacket(OutgoingPacket packet) {
        if (!isBot()) {
            packets.queueOutgoing(packet);
        }
    }

    /**
     * Gets the channel
     *
     * @return {@link #channel}
     */
    public Channel getChannel() {
        return isBot()? null:channel;
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
     * Checks if last state was out of safe
     * @return  The previous of safe state
     */
    public boolean getPreviouslyOutOfSafe() {
        return previouslyOutOfSafe;
    }

    /**
     * Sets last out of safe zone state
     * @param flag  The state to set
     */
    public void setPreviouslyOutOfSafe(boolean flag) {
        this.previouslyOutOfSafe = flag;
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
        if (!isSunk()) {
            this.face = face;
        }
    }

    public Team getTeam() {
        return this.team;
    }

    /**
     * Sets the player registered
     *
     * @param name           The name to register
     * @param ship           The ship id
     * @param team           The team id
     * @param customPosition >=0, >=0 if place, otherwise null for default spawn
     * @param customFace     enum value,        otherwise null for default face
     * @param customDamage   float damage to spawn with, or 0 for default damage
     * @param shouldSpawnFullCannons spawn with full cannons?
     */
    public void register(String name, int ship, int team, int[] customPosition, VesselFace customFace, float customDamage, boolean shouldSpawnFullCannons) {
        this.name = name;
        this.team = Team.forId(team);
        this.vessel = Vessel.createVesselByType(this, ship);
        this.isRegistered = true;

        if (!ServerConfiguration.isTestMode()) {
            ServerContext.log(
                    "[player joined] Registered player \"" + name + "\"" + ", " +
            		Team.teamIDToString(team) + ", " +
            		Vessel.VESSEL_IDS.get(ship) +
                    " joined during the " + (joinedInBreak?"break":"round") + ", on " +
                    getIP() + ". " +
            		context.getPlayerManager().printPlayers()
            );
        }
        respawn(customPosition, customFace, customDamage, shouldSpawnFullCannons);
    }
    
    /**
     * wrapper for register
     */
    public void register(String name, int ship, int team) {
        register(name, ship, team, null, null, 0, false);
    }

    /**
     * Respawns the player to the correct side. It is
     * triggered by the server requiring an action e.g.
     * a player entering safe zone or sinking or spawning.
     * This is 1 of 2 wrappers around respawnOnLandside().
     * The other wrapper is requestRespawnToOceanside()
     * which is triggered by a player clicking Go Oceanside.
     * 
     * @param customPosition which position? or null if default
     * @param customFace     which facing?   or null if default
     * @param customDamage   float damage to spawn with, or 0 for default damage
     * @param shouldSpawnFullCannons spawn with full cannons?
     */
    public void respawn(int[] customPosition, VesselFace customFace, float customDamage, boolean shouldSpawnFullCannons) {
    	// where to respawn?
    	if (isFirstEntry()) {
    		setFirstEntry(false);
            // bugfix #48, #49 - zero cannons and damage when just joining or a round restarts
            tokens.assignCannons(0);
            vessel.resetDamageAndBilge();

    		// start on land side regardless of where
    		// map says we are currently - it's wrong
    		respawnOnLandside(true, customPosition, customFace, customDamage, shouldSpawnFullCannons);
        } else if (enteredSafeLandside) { // drove into safe
    		// only defenders may use the landside safe zone
    		// so only defenders should be respawned here
    		respawnOnLandside(true, customPosition, customFace, customDamage, shouldSpawnFullCannons);
        } else if (enteredSafeOceanside) { // drove into safe
    		// both teams may use the oceanside safe zone
    		respawnOnLandside(false, customPosition, customFace, customDamage, shouldSpawnFullCannons);
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
            vessel.resetDamageAndBilge();
    		respawnOnLandside(true, customPosition, customFace, customDamage, shouldSpawnFullCannons);
    	}
    }

    /**
     * wrapper for respawn
     */
    public void respawn() {
        respawn(null, null, 0, false);
    }

	/**
     * respawn on one of two sides
     * @param landSide true if want to spawn on landside else false
     * @param customPosition which position? or null if default
     * @param customFace     which facing?   or null if default
     * @param shouldSpawnFullCannons spawn with full cannons?
     * @param customDamage   float damage to spawn with, or 0 for default damage
     */
    private void respawnOnLandside(boolean landSide, int[] customPosition, VesselFace customFace, float customDamage, boolean shouldSpawnFullCannons) {
        int x = -1;
        int y = -1;
    	if (customPosition == null) {
            x = ThreadLocalRandom.current().nextInt(0,BlockadeMap.MAP_WIDTH);
        	y = landSide?(BlockadeMap.MAP_HEIGHT-1):0;
        	while(context.getPlayerManager().getPlayerByPosition(x, y) != null) {
                x++;
                x = x % BlockadeMap.MAP_WIDTH;
                // TODO what if cant enter the map?
            }
    	}
    	else {
    	    x = customPosition[0];
    	    y = customPosition[1];
    	}
    	set(x, y);
    	if (!isInSafe()) { setPreviouslyOutOfSafe(true); } // bugfix to allow shoots in first turn, if we spawned out of safe
    	
    	if (customFace == null) {
    	    setFace(landSide?VesselFace.SOUTH:VesselFace.NORTH);
    	}
    	else {
    	    setFace(customFace);
    	}

        // reset flags
        setNeedsRespawn(false);
        setPreviouslyOutOfSafe(false);
        enteredSafeLandside = false;
        enteredSafeOceanside = false;

        // refresh tokens, but leave guns as they were by default unless overriden.
        // ships just joining get 0 guns.
        tokens.assignDefaultTokens();
        this.getPackets().sendTokens();
        getMoveTokens().setTargetTokenGeneration(MoveType.FORWARD, true);
        if (shouldSpawnFullCannons) {
            getMoveTokens().addCannons(this.getVessel().getMaxCannons());
            getPackets().sendTokens();
        }

        // add any custom damage on spawn in e.g. for testing purposes
        if (customDamage > 0) {
            this.getVessel().addCustomDamage(customDamage);
        }

        // send packets
        for (Player p:context.getPlayerManager().listRegisteredPlayers()) {
            p.packets.sendRespawn(this); // bugfix players' moves disappearing when anyone else (re)spawns
        }
    }
    
    /**
     * wrapper for respawnOnLandside
     * @param landSide true if want to spawn on landside else false
     */
    private void respawnOnLandside(boolean landside) {
        respawnOnLandside(landside, null, null, 0, false);
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
                "Disengage is not enabled at the moment. Start a vote to enable it, or restart the server."
    		);
    	}
    	else if (mode.equals("simple"))
    	{
    		// strictly, in a cade only the attacker would be
        	// able to do this. However in simple mode we provide the ability
    		// for the defender to go Oceanside too to keep things fair.
            if (!getPreviouslyOutOfSafe()) {
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
            if (!getPreviouslyOutOfSafe()) {
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
     * Reset an animation structure
     */
    public void resetAnimationStructure() {
        this.animation = new MoveAnimationStructure();
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
	    if (isBot()) {
	        return "<bot>";
	    } else {
	        return getChannel().remoteAddress().toString().replace("/", "").split(":")[0];
	    }
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
    
    //need to check current position rather than the player instance
    public boolean isInSafe(Position pos) {
    	return context.getMap().isSafe(pos);
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

        tokens.clearPlacedTokens();
        tokens.tickExpiration();

        // Send moves
        packets.sendAnimationStructure();

        packets.sendDamage();
        moves.resetTurn();
        packets.sendTokens();
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

        respawn();

        for (Player p : context.getPlayerManager().listRegisteredPlayers()) {
            p.packets.sendRespawn(this);
        }

        setPreviouslyOutOfSafe(false);
        packets.sendDamage();
        packets.sendTokens();
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

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }
}
