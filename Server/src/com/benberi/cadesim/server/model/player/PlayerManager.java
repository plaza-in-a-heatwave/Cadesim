package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.packet.out.impl.LoginResponsePacket;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.cade.map.flag.Flag;
import com.benberi.cadesim.server.model.player.Vote.VOTE_RESULT;
import com.benberi.cadesim.server.model.player.collision.CollisionCalculator;
import com.benberi.cadesim.server.model.player.domain.JobbersQuality;
import com.benberi.cadesim.server.model.player.domain.PlayerLoginRequest;
import com.benberi.cadesim.server.model.player.move.MoveAnimationTurn;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.service.InstanceFileManager;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;
import io.netty.channel.Channel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PlayerManager {

    /**
     * List of players in the game
     */
    private List<Player> players = new ArrayList<>();
    
    /**
     * List of temporarily banned IPs in the game
     */
    private List<String> temporaryBannedIPs = new ArrayList<>();

    /**
     * Queued players login
     */
    private Queue<PlayerLoginRequest> queuedLoginRequests = new LinkedList<>();

    /**
     * Logout requests
     */
    private Queue<Player> queuedLogoutRequests = new LinkedList<>();

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The collision calculator
     */
    private CollisionCalculator collision;

    /**
     * The points of the defender team
     */
    private int pointsDefender;

    /**
     * The points of the attacker team
     */
    private int pointsAttacker;

    /**
     * The last time time packet was sent
     */
    private long lastTimeSend;
    
    /**
     * The last time time the server did some admin
     */
    private long lastAdminCheck;
    
    /**
     * is a vote in progress
     */
    private Vote currentVote = null;
    
    /**
     * restart conditions
     */
    private boolean shouldSwitchMap = false;
    private boolean shouldRestartMap = false;
    private boolean updateScheduledAfterGame = false;
    private long    lastUpdateNotificationMillis =
        System.currentTimeMillis() -Constants.BROADCAST_SCHEDULED_UPDATE_INTERVAL_MILLIS;

	private boolean gameEnded;

	/**
	 * temporary variables for various server config settings
	 */
	private boolean persistTemporarySettings = false; // across rounds
	private int respawnDelay;
	private int turnDuration;
	private int roundDuration;
	private String disengageBehavior;

	/**
	 * helper method to split string into parts
	 * https://stackoverflow.com/a/3760193
	 */
	public static List<String> splitEqually(String text, int size) {
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}

	/**
	 * helper method to reset tmp settings
	 */
	private void resetTemporarySettings()
	{
		setRespawnDelay(ServerConfiguration.getRespawnDelay());
		setTurnDuration(ServerConfiguration.getTurnDuration());
		setRoundDuration(ServerConfiguration.getRoundDuration());
		setDisengageBehavior(ServerConfiguration.getDisengageBehavior());
		setJobbersQuality(ServerConfiguration.getJobbersQualityAsString());
	}
	
    private void setPersistTemporarySettings(boolean value)
    {
    	persistTemporarySettings  = value;
    }

    /**
     * these methods work like a dirty flag - if set true
     * during a round, settings are persisted.
     * Otherwise settings are reverted in the following round.
     */
    private boolean getPersistTemporarySettings()
    {
    	return persistTemporarySettings;
    }
	/**
	 * set with string so can load from cli
	 */
	public static void setJobbersQuality(String value){
		ServerConfiguration.setJobbersQuality(value);
	}

	public static JobbersQuality getJobbersQuality() {
		
		return ServerConfiguration.getJobbersQuality();
	}
	
	public static String getJobbersQualityAsString() {
		return ServerConfiguration.getJobbersQualityAsString();
	}
	
	public int getRespawnDelay() {
		return respawnDelay;
	}

	private void setRespawnDelay(int respawnDelay) {
		this.respawnDelay = respawnDelay;
	}

	public int getTurnDuration() {
		return turnDuration;
	}

	private void setTurnDuration(int turnDuration) {
		this.turnDuration = turnDuration;
	}

	public int getRoundDuration() {
		return roundDuration;
	}

	private void setRoundDuration(int roundDuration) {
		this.roundDuration = roundDuration;
	}
	
	public String getDisengageBehavior() {
		return disengageBehavior;
	}

	private void setDisengageBehavior(String disengageBehavior) {
		this.disengageBehavior = disengageBehavior;
	}


    public boolean isGameEnded() {
		return gameEnded;
	}

	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}

	/**
     * constructor
     */
    public PlayerManager(ServerContext context) {
        this.context = context;
        this.collision = new CollisionCalculator(context, this);
        resetTemporarySettings();
    }
    
    /**
     * getters/setters for restart-related events
     */
    public boolean shouldSwitchMap() {
    	return this.shouldSwitchMap;
    }
    public void setShouldSwitchMap(boolean value) {
    	this.shouldSwitchMap = value;
    }
    public boolean shouldRestartMap() {
        return this.shouldRestartMap;
    }

    public void setShouldRestartMap(boolean value) {
        this.shouldRestartMap = value;
    }

    public void setUpdateScheduledAfterGame(boolean value) {
        updateScheduledAfterGame = value;
    }

    public boolean isUpdateScheduledAfterGame() {
        return updateScheduledAfterGame;
    }

    public void notifyScheduledUpdate() {
        lastUpdateNotificationMillis = System.currentTimeMillis();
        serverBroadcastMessage("~~RESTART NOTICE~~ The server will restart when this game ends ( " + (String.format("%d", (Math.round(context.getTimeMachine().getRoundTime() / 60.0))))
                + " minutes) for scheduled maintenance, and should be back online within a few minutes.");
    }

    public boolean shouldNotifyScheduledUpdate() {
        return isUpdateScheduledAfterGame() && ((System.currentTimeMillis()
                - lastUpdateNotificationMillis) >= Constants.BROADCAST_SCHEDULED_UPDATE_INTERVAL_MILLIS);
    }

    /**
     * Ticks all players
     */
    public void tick() {
        BlockadeTimeMachine tm = context.getTimeMachine();

        // Send time ~ every second
    	long now = System.currentTimeMillis();
        if (now - lastTimeSend >= 1000) {
            lastTimeSend = now;
            sendTime();
        }
        
        // do admin - every n seconds
        if (now - lastAdminCheck >= Constants.SERVER_ADMIN_INTERVAL_MILLIS)
        {
        	lastAdminCheck = now;

        	// also check for vote results - may have timed out
            // other cases handled elsewhere
            if (currentVote != null)
            {
            	if (currentVote.getResult() == VOTE_RESULT.TIMEDOUT)
            	{
            		handleStopVote();
            		currentVote = null;
            	}
            }

            // also check for players who might have logged in but not
            // registered - then timed out
            timeoutUnregisteredPlayers();

            // #83 check for lagged out players
            for (Player p : listRegisteredPlayers()) {
                long response = p.getLastResponseTime();

                // did they time out?
                if ((now - response) > Constants.PLAYER_LAG_TIMEOUT_MS) {
                    serverBroadcastMessage(p.getName() + " timed out!");
                    this.kickPlayer(p);
                }
            }
        }

        // Update players (for stuff like damage fixing, bilge fixing and move token generation)
        // don't do this in breaks, but it's fine to do this during the animation.
        if (!tm.isBreak())
        {
            for (Player p : listRegisteredPlayers()) {
                if (p.isSunk()) {
                    continue;
                }
                p.update();
            }
        }

        // Handle logout/login requests any time
        handleLogoutRequests();
        handlePlayerLoginRequests();
    }

    /**
     * Handles and executes all turns
     */
    public void handleTurns() {
        if (ServerConfiguration.isTestMode()) {
            if (!context.getRegressionTests().loadNextScenario()) {
                context.getRegressionTests().getSummary();
                ServerContext.log("All tests complete, quitting.");
                System.exit(Constants.EXIT_SUCCESS);
            }
        }

        for (Player player : listRegisteredPlayers()) {
            player.getPackets().sendSelectedMoves(); // unglitch players' moves
            player.resetAnimationStructure();  // reset all of their animation structures before the animation builds up again
        }

        // Loop through all turns
        for (int turn = 0; turn < 4; turn++) {
            /*
             * Phase 1 handling
             */

            // Loop through phases in the turn (split turn into phases, e.g turn left is 2 phases, turn forward is one phase).
            // So if stopped in phase 1, and its a turn left, it will basically stay in same position, if in phase 2, it will only
            // move one step instead of 2 full steps.
            for (int phase = 0; phase < 2; phase++) {

                 // Go through all players and check if their move causes a collision
                 for (Player p : listRegisteredPlayers()) {

                     // If a player is already collided in this step or is sunk, we don't want him to move
                     // anywhere, so we skip this iteration
                     if (p.getCollisionStorage().isCollided(turn) || p.isSunk()) {
                         continue;
                     }

                     p.getCollisionStorage().setRecursionStarter(true);
                     // Checks collision for the player, according to his current step #phase index and turn
                     collision.checkCollision(p, turn, phase, true);
                     p.getCollisionStorage().setRecursionStarter(false);
                 }

                 // There we update the bumps toggles, if a bump was toggled, we need to save it to the animation storage for this
                 // current turn, and un-toggle it from the collision storage. We separate bump toggles from the main player loop above
                 // Because we have to make sure that bumps been calculated for every one before moving to the next phase
                 for (Player p : listRegisteredPlayers()) {
                     if (p.getCollisionStorage().isBumped()) {
                         p.set(p.getCollisionStorage().getBumpAnimation().getPositionForAnimation(p));
                         p.getCollisionStorage().setBumped(false);
                     }

                     // Toggle last position change save off because we made sure that it was set in the main loop of players
                     p.getCollisionStorage().setPositionChanged(false);
                 }
             }


            // There we save the animations of all bumps, collision and moves of this turn, and clearing the collision storage
            // And what-ever. We also handle shoots there and calculate the turn time
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn t = p.getAnimationStructure().getTurn(turn);
                MoveType move = p.getMoves().getMove(turn);
                p.setFace(move.getNextFace(p.getFace()));
                t.setMoveToken(move);
                if (p.getCollisionStorage().isCollided(turn)) {
                    t.setAnimation(VesselMovementAnimation.getBumpForPhase(p.getCollisionStorage().getCollisionReference(turn).getPhase()));
                }
                else {
                    if (p.getCollisionStorage().getBumpAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                        t.setAnimation(p.getCollisionStorage().getBumpAnimation());
                    }
                    else {
                        t.setAnimation(VesselMovementAnimation.getIdForMoveType(move));
                    }
                }

                // Clear the collision storage toggles and such
                p.getCollisionStorage().clear();
            }

            /*
            * Phase 2 handling
            */
            for (int phase = 0; phase < 2; phase++) {
                for (Player player : listRegisteredPlayers()) {
                    if (player.getCollisionStorage().isBumped()) {
                        continue;
                    }

                    int tile = context.getMap().getTile(player.getX(), player.getY());

                    if (player.getCollisionStorage().isOnAction()) {
                        tile = player.getCollisionStorage().getActionTile();
                    }

                    if (context.getMap().isActionTile(tile)) {

                        // Save the action tile
                        if (!player.getCollisionStorage().isOnAction()) {
                            player.getCollisionStorage().setOnAction(tile);
                        }

                        // Next position for action tile
                        Position next = context.getMap().getNextActionTilePosition(tile, player, phase);
                        if(!player.isSunk()) {
	                        player.getCollisionStorage().setRecursionStarter(true);
	                        collision.checkActionCollision(player, next, turn, phase, true);
	                        player.getCollisionStorage().setRecursionStarter(false);
                        }
                    }
                }

                for (Player p : listRegisteredPlayers()) {
                    p.getCollisionStorage().setPositionChanged(false);
                }
            }
            
        	for (Player p : listRegisteredPlayers()) {
                if (p.getCollisionStorage().isOnAction()) {
                    int tile = p.getCollisionStorage().getActionTile();
                    if (!context.getMap().isWhirlpool(tile)) {//winds
	                    if (p.getCollisionStorage().isCollided(turn)) {
	                        p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getBumpAnimationForAction(tile));
	                    } else {
	                        p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(tile));
	                    }
                    }
                    else if (context.getMap().isWhirlpool(tile))//whirls
                    	if(!p.getCollisionStorage().isCollided(turn)) {
                            p.setFace(context.getMap().getNextActionTileFace(p.getFace()));
                            p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(tile));
                    	}else {
                    		p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getBumpAnimationForAction(tile));
                    	}
                }

                p.getCollisionStorage().setBumped(false);
                p.getCollisionStorage().clear();
                p.getCollisionStorage().setOnAction(-1);

                if(!p.isSunk()) { //bugfix: only shoot if not sunk
	                // left shoots
	                int leftShoots = p.getMoves().getLeftCannons(turn);
	                // right shoots
	                int rightShoots = p.getMoves().getRightCannons(turn);

	                // Apply cannon damages if they collided with anyone
	                damagePlayersAtDirection(leftShoots, p, Direction.LEFT, turn);
	                damagePlayersAtDirection(rightShoots, p, Direction.RIGHT, turn);

	                // Set cannon animations
	                MoveAnimationTurn t = p.getAnimationStructure().getTurn(turn);
	                t.setLeftShoots(leftShoots);
	                t.setRightShoots(rightShoots);
                }
            }

        	for (Player p : listRegisteredPlayers()) {
	            if (p.getVessel().isDamageMaxed() && !p.isSunk()) {
	                p.setSunk(turn);
	                p.getAnimationStructure().getTurn(turn).setSunk(true);
	            }
        	}

        }

        // Process some after-turns stuff like updating damage interfaces, and such
        for (Player p : listRegisteredPlayers()) {
            p.processAfterTurnUpdate();
        }

        calculateInfluenceFlags();

        if (ServerConfiguration.isTestMode()) {
            context.getRegressionTests().evaluateScenario();
            context.getRegressionTests().unloadScenario();
        }
        
        // end game only after flags calculated, animations done etc
        if (context.getTimeMachine().getRoundTime() < 0)
        {
            setGameEnded(true);
        }
        else
        {
            players.removeIf(p -> (!p.isRegistered())); // purge any unregistered ships
            sendAfterTurn();

        }
    }

    private void calculateInfluenceFlags() {

        // Reset flags
        context.getMap().resetFlags();

        // Update flags for each player
        for (Player player : listRegisteredPlayers()) {
            if (player.isSunk()) {
                player.getFlags().clear(); // bugfix #21 flag influence animations not displaying properly
                continue;
            }
            List<Flag> flags = context.getMap().getInfluencingFlags(player);
            player.setFlags(flags);
        }


        // Set at war flags
        for (Flag flag : context.getMap().getFlags()) {
            Team addPointsTeam = null;

            Team team = null;
            for (Player p : listRegisteredPlayers()) {
                if (p.isSunk() || p.isNeedsRespawn()) { // bugfix flags are retained on reset
                    continue;
                }
                if (p.getFlags().contains(flag)) {
                    if (team == null) {
                        team = p.getTeam();
                        flag.setControlled(team);
                        addPointsTeam = p.getTeam();
                    }
                    else if (team != p.getTeam()) {
                        flag.setAtWar(true);
                        addPointsTeam = null;
                        break;
                    }
                }
            }

            if (addPointsTeam != null) {
                addPointsToTeam(addPointsTeam, flag.getSize().getID());
            }
        }

    }

    /**
     * Adds points to given team
     *
     * @param team      The team
     * @param points    The points
     */
    private void addPointsToTeam(Team team, int points) {
        switch (team) {
            case DEFENDER:
                pointsDefender += points;
                break;
            case ATTACKER:
                pointsAttacker += points;
                break;
            case NEUTRAL:
            	ServerContext.log("warning - tried to assign " + Integer.toString(points) + " to NEUTRAL");
            	break;
        }
    }

    /**
     * Damages entities for player's shoot
     * @param shoots        How many shoots to calculate
     * @param source        The shooting vessel instance
     * @param direction     The shoot direction
     */
    private void damagePlayersAtDirection(int shoots, Player source, Direction direction, int turnId) {
        if (shoots <= 0) {
            return;
        }else {
	        Player player = collision.getVesselForCannonCollide(source, direction);
	        if (player != null && (!source.isInSafe())) {
	            player.getVessel().appendDamage(((double) shoots * source.getVessel().getCannonType().getDamage()), source.getTeam());
	        }
        }
    }

    /**
     * Gets a player for given position
     * @param x The X-axis position
     * @param y The Y-xis position
     * @return The player instance if found, null if not
     */
    public Player getPlayerByPosition(int x, int y) {
        for (Player p : listRegisteredPlayers()) {
            if (p.getX() == x && p.getY() == y) {
                return p;
            }
        }
        return  null;
    }

    /**
     * Sends a player's move bar to everyone
     *
     * @param pl    The player's move to send
     */
    public void sendMoveBar(Player pl) {
        for (Player p : listRegisteredPlayers()) {
            p.getPackets().sendMoveBar(pl);
        }
    }

    /**
     * Lists all registered players
     *
     * @return Sorted list of {@link #players} with only registered players
     */
    public List<Player> listRegisteredPlayers() {
        List<Player> registered = new ArrayList<>();
        for (Player p : players) {
            if (p.isRegistered()) {
                registered.add(p);
            }
        }

        return registered;
    }
    
    /**
     * unregistered players get a few seconds to properly register.
     */
    public void timeoutUnregisteredPlayers() {
    	long now = System.currentTimeMillis();
    	List<Player> l= new ArrayList<Player>();
    	for (Player p : players)
    	{
    		if (!p.isRegistered())
    		{
    			if ((p.getJoinTime() + Constants.REGISTER_TIME_MILLIS) < (now ))
    			{
    				// add to kick list
    				l.add(p);
    			}
    		}
    	}
    	
    	// kick!
    	for (Player p : l)
    	{
    		ServerContext.log("WARNING - " + p.getIP() + " timed out while registering, and was kicked.");
            kickPlayer(p);
            ServerContext.log(printPlayers());
    	}
    }

    /**
     * Gets ALL players
     * @return  the players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * prints registered/players
     */
    public String printPlayers() {
    	return "Registered players:" + Integer.toString(listRegisteredPlayers().size()) + "/" +
    		Integer.toString(getPlayers().size()) + ".";
    }

    /**
     * Kick a player or bot actively
     * @param p the player to kick.
     */
    public void kickPlayer(Player p) {
        if (p.isBot()) {
            players.remove(p);
        }
        else {
            this.deRegisterPlayer(p.getChannel());
        }
    }

    public void kickAndBanPlayer(Player p) {
        if (!p.isBot()) { temporaryBannedIPs.add(p.getIP()); }
        kickPlayer(p);
        serverBroadcastMessage(p.getName() + " was kicked and banned for the rest of this round.");
    }

    public void kickAllPlayers() {
        for (Player p : this.listRegisteredPlayers()) {
            kickPlayer(p);
        }
    }
    
    /**
     * Create new NPC.
     * @param name its name
     * @param vesselID its ship type enum
     * @param team its team (team type enum)
     * @param startPosition -1,-1 to start not spawned, or >=0,>=0 to start on the map.
     * @param startFace     which way to face to start with?
     * @param startDamage   how much damage to spawn in with
     */
    public Player createBot(
            String name,
            int vesselID,
            Team team,
            int[] startPosition,
            VesselFace startFace,
            float startDamage
    ) {
        Player p = registerPlayer(null);
        p.register(
                name,
                vesselID,
                team.getID(),
                startPosition,
                startFace,
                startDamage,
                true
        );
        return p;
    }

    /**
     * Remove an NPC.
     * @param player which one to remove
     */
    public void removeBot(Player player) {
        kickPlayer(player); // kick but don't ban
    }

    /**
     * Registers a new player to the server, puts him in a hold until he sends the protocol handshake packet
     * bots never send a handshake, so you can immediately register them.
     *
     * @param c The channel to register
     * @return  the newly created player
     */
    public Player registerPlayer(Channel c) {
        Player player = new Player(context, c);
        String ip = player.getIP();
        if ((!player.isBot()) && (temporaryBannedIPs.contains(ip))) // bots are exempt
        {
        	// dont allow banned IPs into the server until the next round begins
        	ServerContext.log("Kicked player " + player.getIP() + " attempted to rejoin, and was kicked again.");
            kickPlayer(player);
        	return player;
        }

        // don't allow multiclients if settings forbid it. Bots are exempt.
        if ((!player.isBot()) && (!ServerConfiguration.getMultiClientMode()))
        {
            for (Player p : players)
            {
                if (p.getIP().equals(ip))
                {
                    ServerContext.log(
                        "Warning - " +
                        ip + " (currently logged in as " + p.getName() + ")" +
                        " attempted login on a second client, but multiclient is not permitted"
                    );
                    kickPlayer(player);
                    return player;
                }
            }
        }

        // dont allow players to join if we're updating right now.
        // bugfix for client that joins while update is applying, then channel
        // closes minutes later potentially disrupting their next game.
        if (context.getPlayerManager().isGameEnded() &&
                context.getPlayerManager().isUpdateScheduledAfterGame()) {
            ServerContext.log(
                "[kicked player] New player added to channel " +
                player.getIP() + ". Kicked because update in progress.");
            kickPlayer(player);
            return player;
        }
     	
        // otherwise ok
    	players.add(player);
    	if (!ServerConfiguration.isTestMode()) {
            ServerContext.log(
            	"[player joined] New player added to channel " +
            	player.getIP() + ". " +
            	printPlayers()
            );
    	}
        
        return player;
    }

    /**
     * De-registers a player from the server (reactionary)
     *
     * @param channel   The channel that got de-registered
     */
    public void deRegisterPlayer(Channel channel) {
        Player player = getPlayerByChannel(channel);
        if (player != null) {
            queuedLogoutRequests.add(player);
        }
        else {
            // pass - don't care if player is null
        }
    }

    /**
     * Reset all move bars
     */
    public void resetMoveBars() {
        for (Player p : listRegisteredPlayers()) {
            sendMoveBar(p);
        }
    }

    /**
     * Gets a player instance by its channel
     * @param c The channel
     * @return  The player instance if found, null if not found
     * bots are exempt.
     */
    public Player getPlayerByChannel(Channel c) {
        for(Player p : players) {
            if ((!p.isBot()) && p.getChannel().equals(c)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Gets a player by its name
     * @param name  The player name
     * @return The player
     */
    public Player getPlayerByName(String name) {
        for (Player p : listRegisteredPlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Sends a player for all players
     *
     * @param player    The player to send
     */
    public void sendPlayerForAll(Player player) {
        for (Player p : players) {
            if (p == player) {
                continue;
            }
            p.getPackets().sendPlayer(player);
        }
    }

    /**
     * Queues a player login request
     *
     * @param request   The login request
     */
    public void queuePlayerLogin(PlayerLoginRequest request) {
        queuedLoginRequests.add(request);
    }

    /**
     * Handles all player login requests
     */
    @SuppressWarnings("unused")
    public void handlePlayerLoginRequests() {
        while(!queuedLoginRequests.isEmpty()) {
            PlayerLoginRequest request = queuedLoginRequests.poll();

            Player pl = request.getPlayer();
            String name = request.getName();
            int version = request.getVersion();
            int ship = request.getShip();
            int team = request.getTeam();

            // skip any players that aren't in our player list - these
            // are glitches left over from login attempts
            boolean found = false;
            for (Player p : players)
            {
                if (p.getName() == pl.getName())
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                continue;
            }

            int response = LoginResponsePacket.SUCCESS;

            if (version != Constants.PROTOCOL_VERSION) {
                ServerContext.log(
                    "Warning: Player protocol version " + version +
                    " does not match server " + Constants.PROTOCOL_VERSION
                );
                response = LoginResponsePacket.BAD_VERSION;
            }
            else if (getPlayerByName(name) != null) {
                ServerContext.log(
                        "Warning: Player name " + name+
                        " is already in use"
                );
                response = LoginResponsePacket.NAME_IN_USE;
            }
            else if (
            	(name.contains(Constants.bannedSubstring)) ||
            	(name.length() <= 0) ||
            	(name.length() > Constants.MAX_NAME_SIZE))
            {
                ServerContext.log(
                        "Warning: Player chose bad name (" + name+ ")"
                );
            	response = LoginResponsePacket.BAD_NAME;
            }
            else if (!Vessel.VESSEL_IDS.containsKey(ship)) {
                ServerContext.log(
                        "Warning: Player chose bad ship (" + ship + ")"
                );
                response = LoginResponsePacket.BAD_SHIP;
            }
            else if ((!Constants.ENABLE_CHOOSE_BLACKSHIP) && (Vessel.VESSEL_IDS.get(ship).equals("blackship"))) {
                ServerContext.log(
                        "Warning: Player chose black ship, but it is not allowed."
                );
                response = LoginResponsePacket.BAD_SHIP;
            }

            pl.getPackets().sendLoginResponse(response);

            if (response == LoginResponsePacket.SUCCESS) {
            	pl.getPackets().sendMapList();
                pl.register(name, ship, team);
                pl.getPackets().sendBoard();
                pl.getPackets().sendTeams();
                pl.getPackets().sendPlayers();
                pl.getPackets().sendDamage();
                pl.getPackets().sendTokens();
                pl.getPackets().sendFlags();
                pl.getPackets().sendPlayerFlags();
                sendPlayerForAll(pl);
                serverBroadcastMessage("Welcome " + pl.getName() + " (" + pl.getTeam() + ")");
                printCommandHelp(pl); // private message with commands
            }
        }
    }

    /**
     * Handles logouts
     */
    private void handleLogoutRequests() {
        while(!queuedLogoutRequests.isEmpty()) {
            Player player = queuedLogoutRequests.poll();
            players.remove(player);

            if (player.isRegistered()) {
            	// notify everyone else
	            for (Player p : listRegisteredPlayers()) {
	                if (p != player) {
	                    p.getPackets().sendRemovePlayer(player);
	                }
	            }
	            serverBroadcastMessage("Goodbye " + player.getName() + " (" + player.getTeam() + ")");
                printTeams(null, true); // broadcast

	            // log
	            ServerContext.log(
	            	"[player left] De-registered and logged out player \"" + player.getName() + "\", " +
	            	player.getIP() + ". " +
	            	printPlayers()
	            );

	            // end game if applicable
	            if (listRegisteredPlayers().size() == 0)
	            {
	            	this.setGameEnded(true);
	            }
            }
            else
            {
            	// just log
            	ServerContext.log(
	            	"[player left] unregistered player disconnected on " +
	            	player.getIP() + ". " +
	            	printPlayers()
	            );
            }

            // finally, force-disconnect the channel.
            try {
                player.getChannel().disconnect();
            } catch (Exception e) {
                ServerContext.log("while disconnecting player " + player.getName() + ", caught exception " + e);
            }
        }
    }

    /**
     * Sends and updates the time of the game, turn for all players
     */
    private void sendTime() {

        context.incrementPingCounter();
        for (Player player : listRegisteredPlayers()) {
            player.getPackets().sendTime();
        }
    }

    public void sendAfterTurn() {
        // deal with sunk/unspawned ships first
        for (Player p : listRegisteredPlayers()) {
            if (p.isNeedsRespawn()) {
                p.respawn();
            }

            if (p.isSunk()) {
                p.giveLife();
            }
        }

        // then deal with individual players
        for (Player p : listRegisteredPlayers()) {
            p.getPackets().sendPositions();
            sendMoveBar(p);
            p.getPackets().sendFlags();
            p.getPackets().sendPlayerFlags();
        }
    }

    public int getPointsDefender() {
        return pointsDefender;
    }

    public int getPointsAttacker() {
        return pointsAttacker;
    }

    public void queueOutgoing() {
        for (Player p : players) {
            p.getPackets().queueOutgoingPackets();
            p.getChannel().flush();
        }
    }

    public void renewGame()
    {
    	// empty temporary ban list
    	temporaryBannedIPs.clear();
    	
    	// initially dont plan to persist temporary values beyond next round
    	// votes must actively opt-in to change this
    	if (!ServerConfiguration.isVotingEnabled())
    	{
    		// dont need to do anything if no voting
    	}
    	else if (!getPersistTemporarySettings())
    	{
    		// reset to defaults
    		resetTemporarySettings();
    		serverBroadcastMessage("all temporary settings were reverted");
    	}
    	else
    	{
    		serverBroadcastMessage(
    			"the temporary settings are still applied this round. " +
    			"Vote restart to clear them, or wait for the round to end naturally."
    		);
    	}
    	setPersistTemporarySettings(false);

        setShouldSwitchMap(false);
        setShouldRestartMap(false);
        pointsAttacker = 0;
        pointsDefender = 0;

        for (Player p : listRegisteredPlayers()) {
        	p.setFirstEntry(true);
        	p.setNeedsRespawn(true);
            p.getPackets().sendBoard();
        	p.getMoveTokens().setAutomaticSealGeneration(true); // bugfix disparity between client and server
        	p.getPackets().sendFlags();
        	sendAfterTurn();
        }
        
        // game no longer ended
        setGameEnded(false);
    }
    
    /**
     * send message to all players from server
     */
    public void serverBroadcastMessage(String message)
    {
    	ServerContext.log("[chat] " + Constants.serverBroadcast + ":" + message);
    	
    	if (message.length() >= Constants.SPLIT_CHAT_MESSAGES_THRESHOLD) {
    	    List<String> messageParts = splitEqually(message, Constants.SPLIT_CHAT_MESSAGES_THRESHOLD);
    	    for (String s : messageParts) {
    	        for(Player player : context.getPlayerManager().listRegisteredPlayers()) {
    	            if (player.isBot()) { continue; }
    	            player.getPackets().sendReceiveMessage(Constants.serverBroadcast, s);
                }
    	    }
    	}
    	else
    	{
    	    for(Player player : context.getPlayerManager().listRegisteredPlayers()) {
    	        if (player.isBot()) { continue; }
                player.getPackets().sendReceiveMessage(Constants.serverBroadcast, message);
            }
    	}
    }
    
    /**
     * send a message to a single player from the server
     */
    public void serverPrivateMessage(Player pl, String message)
    {
        if (pl.isBot()) {
            return;
        }
        
    	ServerContext.log("[chat] " + Constants.serverPrivate + "(to " + pl.getName() + "):" + message);
    	
    	if (message.length() >= Constants.SPLIT_CHAT_MESSAGES_THRESHOLD) {
    	    List<String> messageParts = splitEqually(message, Constants.SPLIT_CHAT_MESSAGES_THRESHOLD);
            for (String s : messageParts) {
                pl.getPackets().sendReceiveMessage(Constants.serverPrivate, s);
            }
    	}
    	else {
    	    pl.getPackets().sendReceiveMessage(Constants.serverPrivate, message);
    	}
    }
    
    /**
     * helper method to handle vote yes/no messages
     * @param pl      the player who sent the message
     * @param voteFor true if voting for, else false
     */
    private void handleVote(Player pl, boolean voteFor)
    {
    	// subsequent people (including originator) may vote on it
		if (currentVote == null)
		{
			serverPrivateMessage(pl, "There is no vote in progress, start one with /propose");
		}
		else if (currentVote.getDescription().equals("restart"))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				handleStopVote();
				setShouldRestartMap(true);
				setPersistTemporarySettings(false); // restart cancels temporary settings
				context.getTimeMachine().stop();
				for(Player player : players) { // resets all players tokens
					player.setTurnsUntilControl(0);
					player.getMoveTokens().assignDefaultTokens();
				}
				break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().equals("nextmap"))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				handleStopVote();
				setShouldSwitchMap(true);
				setPersistTemporarySettings(true);
				context.getTimeMachine().stop();
				for(Player player : players) { // resets all players tokens
					player.setTurnsUntilControl(0);
					player.getMoveTokens().assignDefaultTokens();
				}
				break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().startsWith("change map to: "))
		{
		    VOTE_RESULT v = currentVote.castVote(pl, voteFor);
            switch(v)
            {
            case TBD:
                break;
            case FOR:
                ServerConfiguration.overrideNextMapName(
                        currentVote.getDescription().substring(
                                "change map to: ".length()
                        )
                );
                handleStopVote();
                setShouldSwitchMap(true);
                setPersistTemporarySettings(true);
                context.getTimeMachine().stop();
				for(Player player : players) { // resets all players tokens
					player.setTurnsUntilControl(0);
					player.getMoveTokens().assignDefaultTokens();
				}
                break;
            case AGAINST:
            case TIMEDOUT:
                handleStopVote();
                break;
            default:
                break;
            }
		}
		else if (currentVote.getDescription().startsWith("kick "))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				Player playerToKick = getPlayerByName(currentVote.getDescription().substring("kick ".length()));
				if (playerToKick != null)
				{
					serverBroadcastMessage("Player " + playerToKick.getName() + " was kicked by vote!");
                    kickAndBanPlayer(playerToKick);
				}
				handleStopVote();
				break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().startsWith("set turnduration "))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				BlockadeTimeMachine tm = context.getTimeMachine();
				setPersistTemporarySettings(true);
				tm.stop();
	            setTurnDuration(
	            	Integer.parseInt(
	            		currentVote.getDescription().substring("set turnduration ".length())
	            	) * 10
	            );

	            serverBroadcastMessage(
	            	"The turn duration was changed to " + (getTurnDuration() / 10) +
	            	". It will revert back to " + (ServerConfiguration.getTurnDuration() / 10) +
	            	" when the round times out, or when players vote restart."
	            );
	            
	            handleStopVote(); // handle this afterwards, otherwise currentVote == null
				break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().startsWith("set roundduration "))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				BlockadeTimeMachine tm = context.getTimeMachine();
				setPersistTemporarySettings(true);
				tm.stop();
	            setRoundDuration(Integer.parseInt(
	            		currentVote.getDescription().substring("set roundduration ".length())
	            	) * 10
	            );

	            serverBroadcastMessage(
	            	"The round duration was changed to " + (getRoundDuration() / 10) +
	            	". It will revert back to " + (ServerConfiguration.getRoundDuration() / 10) +
	            	" when the round times out, or when players vote restart."
	            );
	            
	            handleStopVote();
	            break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().startsWith("set sinkpenalty "))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:				
				BlockadeTimeMachine tm = context.getTimeMachine();
				setPersistTemporarySettings(true);
				tm.stop();

				setRespawnDelay(
					Integer.parseInt(
						currentVote.getDescription().substring("set sinkpenalty ".length())
					)
				);
				
				serverBroadcastMessage(
	            	"The sinking penalty was changed to " + getRespawnDelay() +
	            	". It will revert back to " + ServerConfiguration.getRespawnDelay() +
	            	" when the round times out, or when players vote restart."
	            );
				
				handleStopVote();
	            break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().startsWith("set disengage-behavior "))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:				
				BlockadeTimeMachine tm = context.getTimeMachine();
				setPersistTemporarySettings(true);
				tm.stop();

				setDisengageBehavior(
					currentVote.getDescription().substring("set disengage-behavior ".length())
				);
				
				serverBroadcastMessage(
	            	"The disengage button behavior was changed to " + getDisengageBehavior() +
	            	". It will revert back to " + ServerConfiguration.getDisengageBehavior() +
	            	" when the round times out, or when players vote restart."
	            );
				
				handleStopVote();
	            break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else if (currentVote.getDescription().equals("gamesettings"))
		{
			VOTE_RESULT v = currentVote.castVote(pl, voteFor);
			switch(v)
			{
			case TBD:
				break;
			case FOR:
				ServerConfiguration.setTurnDuration(ServerConfiguration.getProposedTurnDuration() * 10);
				ServerConfiguration.setRoundDuration(ServerConfiguration.getProposedRoundDuration() * 10);
				ServerConfiguration.setRespawnDelay(ServerConfiguration.getProposedRespawnDelay());
				ServerConfiguration.setDisengageBehavior(ServerConfiguration.getProposedDisengageBehavior());
				ServerConfiguration.setJobbersQuality(ServerConfiguration.getProposedJobbersQualityAsString());
				setPersistTemporarySettings(false);
				context.getTimeMachine().stop();
				if(!ServerConfiguration.isCustomMap()) {
					String match=null;
	    		    for (String s : ServerConfiguration.getAvailableMaps()) {
	    		        if (s.toLowerCase().startsWith(ServerConfiguration.getProposedMapName().toLowerCase())) {
	    		            match = s;
	    		            break;
	    		        }
	    		    }
	    		    if (match != null) {
	                    ServerConfiguration.overrideNextMapName(match);
	    		    }
	    		    else
	    		    {
	    		        serverPrivateMessage(pl, "Unknown map name.");
	    		    }		
				}else {
					ServerConfiguration.overrideNextMapName(ServerContext.getCustomMapName());
				}
			
                setShouldSwitchMap(true);
				for(Player player : players) { // resets all players tokens
					player.setTurnsUntilControl(0);
					player.getMoveTokens().assignDefaultTokens();
				}
				handleStopVote();
				break;
			case AGAINST:
			case TIMEDOUT:
				handleStopVote();
				break;
			default:
				break;
			}
		}
		else
		{
			ServerContext.log("got a vote description that wasn't understood: " + currentVote.getDescription());
		}
    }

	private void handleStartVote(Player pl, String message, String voteDescription)
    {
    	// first person to request vote creates it (and votes for it)
		if (currentVote == null)
		{
			currentVote = new Vote((PlayerManager)this, voteDescription, ServerConfiguration.getVotingMajority());
			handleVote(pl, true);
		}
		else
		{
			serverPrivateMessage(pl, "Can't start a new vote, there is one in progress, use /vote yes or /vote no to vote: " + currentVote.getDescription());
		}
    }
    
    private void handleStopVote()
    {
    	// print out the final scores
    	if (currentVote != null)
    	{
    		currentVote.getResult();
    		currentVote = null;
    	}
    }
    
    private void printCommandHelp(Player pl)
    {
    	if (!ServerConfiguration.isVotingEnabled())
		{
    		serverPrivateMessage(
        			pl,
        			"The following Cadesim commands are supported: /info, /show"
        	);
		}
    	else
    	{
    		serverPrivateMessage(
        			pl,
        			"The following Cadesim commands are supported: /propose, /vote, /info, /show"
        	);
    	}
    	
    }
    
    private void printTeams(Player pl, boolean verbose) {
        int numAttackers = 0;
        int numDefenders = 0;
        int numPlayers = this.listRegisteredPlayers().size();
        String attackers = "";
        String defenders = "";

        // compute values
        for (Player p : this.listRegisteredPlayers()) {
            if (p.getTeam().equals(Team.ATTACKER)) {
                numAttackers++;
                attackers += "\n    " + p.getName() +
                        " (" + Vessel.VESSEL_IDS.get(p.getVessel().getID()) + ")";
            }
            else if (p.getTeam().equals(Team.DEFENDER))
            {
                numDefenders++;
                defenders += "\n    " + p.getName() +
                        " (" + Vessel.VESSEL_IDS.get(p.getVessel().getID()) + ")";
            }
        }

        // null out if there aren't any attackers or defenders
        if (numAttackers == 0) { attackers = "\n    -"; }
        if (numDefenders == 0) { defenders = "\n    -"; }
        String message = "Players in server: " + numPlayers + " ( Att. " + numAttackers + ", Def. " + numDefenders + ")";

        if (verbose) {
                message +=  "\nAttackers:" + attackers + "\nDefenders:" + defenders;
        }

        // do the print
        if (pl == null) {
            serverBroadcastMessage(message);
        }
        else
        {
            serverPrivateMessage(pl, message);
        }
    }

    private String proposeSetHelp()
    {
    	return "usage: /propose set <parameter> <value> -\n" +
		"    turnduration (between 1 and 10000 inclusive)\n" +
		"    roundduration (between 1 and 10000 inclusive)\n" +
		"    sinkpenalty (between 0 and 10000 inclusive)\n" +
		"    disengage-behavior (off|realistic|simple)\n";
    }

    public void handleMessage(Player pl, String message)
    {
    	// log here (always)
        ServerContext.log("[chat] " + pl.getName() + ":" + message);

    	// if it starts with /, it is a server command
		if (message.startsWith("/"))
		{
			// cleanup
			message = message.toLowerCase();
			System.out.println(message);
			if (message.startsWith("/vote"))
			{
				// voting on a current vote
				if (!ServerConfiguration.isVotingEnabled())
				{
					serverPrivateMessage(pl, "Voting is disabled on this server");
				}
				else if (message.equals("/vote yes") || message.equals("/vote no"))
				{
					handleVote(pl, (message.equals("/vote yes")));	
				}
	    		else
	    		{
	    			serverPrivateMessage(pl, "usage: /vote (yes|no) - used to vote on a proposal");
	    		}
			}
			else if (message.startsWith("/propose"))
			{
				if (!ServerConfiguration.isVotingEnabled())
				{
					serverPrivateMessage(pl, "Voting is disabled on this server");
				}
				else if (message.equals("/propose restart"))
	    		{
					handleStartVote(pl, message, "restart");
	    		}
	    		else if (message.startsWith("/propose gamesettings"))
	    		{
	    			handleStartVote(pl, message, "gamesettings");
	    		}
	    		else if (message.equals("/propose nextmap"))
	    		{
	    			handleStartVote(pl, message, "nextmap");
	    		}
	    		else if (message.startsWith("/propose changemap"))
	    		{
	    		    // validate by name first
	    		    String match=null;
	    		    for (String s : ServerConfiguration.getAvailableMaps()) {
	    		        if (message.toLowerCase().equals("/propose changemap " + s.toLowerCase())) {
	    		            match = s;
	    		            break;
	    		        }
	    		    }
	    		    
	    		    // validate by number. must have the trailing space too.
	    		    if (match == null && message.startsWith("/propose changemap ")) {
	    		        try {
	    		            int index = Integer.parseInt(message.substring("/propose changemap ".length()));
	    		            if ((index >= 0) && (index < ServerConfiguration.getAvailableMaps().size())) {
	    		                match = ServerConfiguration.getAvailableMaps().get(index);
	    		            }
	    		            else
	    		            {
	    		                serverPrivateMessage(pl, "changemap: [" + index + "] isn't a map we know about");
	    		            }
	    		        }
	    		        catch (NumberFormatException e) {
	    		            // doesn't exist
	    		        }
	    		        catch (Exception e) {
	    		            // something else...
	    		        }
	    		    }
	    		    
	    		    if (match != null) {
	    		        handleStartVote(pl, "/propose changemap " + match, "change map to: " + match);
	    		    }
	    		    else
	    		    {
	    		        serverPrivateMessage(pl, "usage: /propose changemap <map #/name>\n    find available maps with /show maps");
	    		    }
	    		}
	    		else if (message.startsWith("/propose kick"))
	    		{
	    			boolean found = false;
	    			for (Player registeredPlayer : listRegisteredPlayers())
	    			{
                        // kick player using case insensitive matching
                        // e.g. Helloworld, helloworld, HelLoWOrLD
                        // should all kick player HelloWorld
                        if (message.toLowerCase().equals("/propose kick " + registeredPlayer.getName().toLowerCase()))
	    				{
	    					handleStartVote(pl, message, "kick " + registeredPlayer.getName());
	    					found = true;
	    					break;
	    				}
	    			}
	    			
	    			// cant kick player who doesnt exist
	    			if (!found)
	    			{
	    				serverPrivateMessage(pl, "usage: /propose kick <player>");
	    			}
	    		}
	    		else if (message.startsWith("/propose set"))
	    		{
	    			if (message.startsWith("/propose set turnduration"))
	    			{
	    				try {
	    					int value = Integer.parseInt((message.substring("/propose set turnduration ".length())));
	    					if (value > 0 && value <= 10000)
	    					{
	    						handleStartVote(pl, message, "set turnduration " + value);
	    					}
	    					else
	    					{
	    						serverPrivateMessage(pl, proposeSetHelp());
	    					}
	    				}
	    				catch(Exception e)
	    				{
	    					serverPrivateMessage(pl, proposeSetHelp());
	    				}
	    			}
	    			else if (message.startsWith("/propose set roundduration"))
	    			{
	    				try {
	    					int value = Integer.parseInt((message.substring("/propose set roundduration ".length())));
	    					if (value > 0 && value <= 10000)
	    					{
	    						handleStartVote(pl, message, "set roundduration " + value);
	    					}
	    					else
	    					{
	    						serverPrivateMessage(pl, proposeSetHelp());
	    					}
	    				}
	    				catch(Exception e)
	    				{
	    					serverPrivateMessage(pl, proposeSetHelp());
	    				}
	    			}
	    			else if (message.startsWith("/propose set sinkpenalty"))
	    			{
	    				try {
	    					int value = Integer.parseInt((message.substring("/propose set sinkpenalty ".length())));
	    					if (value >= 0 && value <= 10000)
	    					{
	    						handleStartVote(pl, message, "set sinkpenalty " + value);
	    					}
	    					else
	    					{
	    						serverPrivateMessage(pl, proposeSetHelp());
	    					}
	    				}
	    				catch(Exception e)
	    				{
	    					serverPrivateMessage(pl, proposeSetHelp());
	    				}
	    			}
	    			else if (message.startsWith("/propose set disengage-behavior"))
	    			{
	    				try {
	    					String behavior = (message.substring("/propose set disengage-behavior ".length()));
	    					if (behavior.equals("simple") || behavior.equals("realistic") || behavior.equals("off"))
	    					{
	    						handleStartVote(pl, message, "set disengage-behavior " + behavior);
	    					}
	    					else
	    					{
	    						serverPrivateMessage(pl, proposeSetHelp());
	    					}
	    				}
	    				catch(Exception e)
	    				{
	    					serverPrivateMessage(pl, proposeSetHelp());
	    				}
	    			}
	    			else
	    			{
	    				serverPrivateMessage(pl, proposeSetHelp());
	    			}
	    		}
	    		else
	    		{
	    			serverPrivateMessage(pl, "usage: /propose\n" +
	    				"    restart (restarts round)\n" +
	    				"    nextmap (gets a new map)\n" +
	    				"    changemap <map #/name>, see /show maps\n" +
	    				"    kick <player>\n" +
	    				"    set <parameter> <value> (temporarily set value)\n"
	    			);
	    		}
			}
    		else if (message.equals("/info"))
			{
                serverPrivateMessage(pl, ServerConfiguration.getConfig());
			}
    		else if (message.startsWith("/show"))
    		{
    		    // show on its own prints the help.
    		    if (message.equals("/show thismap")) {
    		        serverPrivateMessage(pl, "---thismap---\n" + ServerConfiguration.getMapName());
    		    }
    		    else if (message.equals("/show nextmap")) {
    		        serverPrivateMessage(pl, "---nextmap---\n" + ServerConfiguration.getNextMapName());
    		    }
                else if (message.equals("/show players")) { // alias
                    printTeams(pl, false);
                } else if (message.equals("/show teams")) { // verbose alias
                    printTeams(pl, true);
                }
    		    else if (message.equals("/show maps")) {
    		        StringBuilder sb = new StringBuilder("---Available maps---\n");
    		        for (int i=0; i<ServerConfiguration.getAvailableMaps().size(); i++)
    		        {
    		            sb.append("[" + i + "]  " + ServerConfiguration.getAvailableMaps().get(i) + "\n");
    		        }
    		        serverPrivateMessage(pl, sb.toString());
    		    }
    		    else {
    		        serverPrivateMessage(pl, "usage: /show\n" +
                        "    thismap (show the current selected map)\n" +
                        "    nextmap (show the next map in rotation)\n" +
                        "    maps (get a list of all available maps)\n" +
                        "    players, teams"
                    );
    		    }
    		}
			else
			{
				printCommandHelp(pl);
			}
		}
		else
		{
			// dont broadcast server commands, but broadcast everything else
			for(Player player : context.getPlayerManager().listRegisteredPlayers()) {
				player.getPackets().sendReceiveMessage(pl.getName(), message);
	        }
		}
    }
}