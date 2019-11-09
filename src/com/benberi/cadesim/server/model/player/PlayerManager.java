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
import com.benberi.cadesim.server.model.player.domain.PlayerLoginRequest;
import com.benberi.cadesim.server.model.player.move.MoveAnimationTurn;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;
import io.netty.channel.Channel;

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
     * should we switch map? (typically requested by player vote)
     */
    private boolean shouldSwitchMap = false;

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
	 * helper method to reset tmp settings
	 */
	private void resetTemporarySettings()
	{
		setRespawnDelay(ServerConfiguration.getRespawnDelay());
		setTurnDuration(ServerConfiguration.getTurnDuration());
		setRoundDuration(ServerConfiguration.getRoundDuration());
		setDisengageBehavior(ServerConfiguration.getDisengageBehavior());
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

	private void setGameEnded(boolean gameEnded) {
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
     * gets whether the map should be updated
     */
    public boolean shouldSwitchMap() {
    	return this.shouldSwitchMap;
    }
    
    public void setShouldSwitchMap(boolean value) {
    	this.shouldSwitchMap = value;
    }

    /**
     * Ticks all players
     */
    public void tick() {

        // Send time ~ every second
    	long now = System.currentTimeMillis();
        if (now - lastTimeSend >= 1000) {
            lastTimeSend = now;
            sendTime();
        }

        // turn finished
        if (context.getTimeMachine().isLock()) {
        	handleTime();
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
        }

        // Update players (for stuff like damage fixing, bilge fixing and move token generation)
        if (!context.getTimeMachine().isLock()) {
            for (Player p : listRegisteredPlayers()) {
                if (p.isSunk()) {
                    continue;
                }
                p.update();
            }
        }

        // Handle login/logout requests (only when not animating)
        if (!context.getTimeMachine().isLock()) {
            handlePlayerLoginRequests();
            handleLogoutRequests();
        }
    }

    /**
     * Handles and executes all turns
     */
    public void handleTurns() {

        context.getTimeMachine().renewTurn();

        for (Player player : listRegisteredPlayers()) {
            player.getPackets().sendSelectedMoves();
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
                    t.setAnimation(VesselMovementAnimation.getBumpForPhase(p.getCollisionStorage().getCollisionRerefence(turn).getPhase()));
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

                        player.getCollisionStorage().setRecursionStarter(true);
                        collision.checkActionCollision(player, next, turn, phase, true);
                        player.getCollisionStorage().setRecursionStarter(false);
                    }
                }

                for (Player p : listRegisteredPlayers()) {
                    p.getCollisionStorage().setPositionChanged(false);
                }
            }

            for (Player p : listRegisteredPlayers()) {

                if (p.getCollisionStorage().isOnAction()) {
                    int tile = p.getCollisionStorage().getActionTile();
                    if (p.getCollisionStorage().isCollided(turn)) {
                        p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getBumpAnimationForAction(tile));
                    } else {
                        p.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(tile));
                    }

                    if (context.getMap().isWhirlpool(tile))
                        p.setFace(context.getMap().getNextActionTileFace(p.getFace()));
                }

                p.getCollisionStorage().setBumped(false);
                p.getCollisionStorage().clear();
                p.getCollisionStorage().setOnAction(-1);


                // left shoots
                int leftShoots = p.getMoves().getLeftCannons(turn);
                // right shoots
                int rightShoots = p.getMoves().getRightCannons(turn);

                // Apply cannon damages if they collided with anyone
                damagePlayersAtDirection(leftShoots, p, Direction.LEFT, turn);
                damagePlayersAtDirection(rightShoots, p, Direction.RIGHT, turn);

                MoveAnimationTurn t = p.getAnimationStructure().getTurn(turn);

                // Set cannon animations
                t.setLeftShoots(leftShoots);
                t.setRightShoots(rightShoots);

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

        context.getTimeMachine().setLock(true);
    }

    /**
     * Handles a turn end
     */
    private void handleTurnEnd() {
        calculateInfluenceFlags();
        
	    // end game only after flags calculated, animations done etc
        if (context.getTimeMachine().getRoundTime() < 0)
        {
        	setGameEnded(true);
        }
        else
        {
            // purge all unregistered ships
            players.removeIf(p -> (!p.isRegistered()));

            context.getTimeMachine().setLock(false);
            sendAfterTurn();

        }
    }

    private void calculateInfluenceFlags() {

        // Reset flags
        context.getMap().resetFlags();

        // Update flags for each player
        for (Player player : listRegisteredPlayers()) {
            if (player.isSunk()) {
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
                    else if (team == p.getTeam()) {
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
        }
        Player player = collision.getVesselForCannonCollide(source, direction);
        if (player != null && source.isOutOfSafe() && !source.isSunk()) {
            player.getVessel().appendDamage(((double) shoots * source.getVessel().getCannonType().getDamage()), source.getTeam());
        }
    }

    /**
     * Handles the time
     */
    private void handleTime() {
        boolean allFinished = true;
        for (Player p : listRegisteredPlayers()) {
            if (!p.isTurnFinished()) {
                if (p.getTurnFinishWaitingTicks() > ServerConfiguration.getTurnDuration()) {
                    ServerContext.log(p.getName() +  " was kicked for timing out while animating! (" + p.getChannel().remoteAddress() + ")");
                    serverBroadcastMessage(p.getName() + " from team " + p.getTeam() + " was kicked for timing out.");
                    p.getChannel().disconnect();
                }
                else {
                    p.updateTurnFinishWaitingTicks();
                    allFinished = false;
                }
            }
        }

        if (allFinished) {
            handleTurnEnd();
        }
        else
        {
        	
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
    		ServerContext.log("WARNING - " + p.getChannel().remoteAddress() + " timed out while registering, and was kicked.");
            p.getChannel().disconnect();
            players.remove(p);
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
     * Registers a new player to the server, puts him in a hold until he sends the protocol handshake packet
     *
     * @param c The channel to register
     */
    public void registerPlayer(Channel c) {
        Player player = new Player(context, c);
        String ip = player.getIP();
        if (temporaryBannedIPs.contains(ip))
        {
        	// dont allow banned IPs into the server until the next round begins
        	ServerContext.log("Kicked player " + c.remoteAddress() + " attempted to rejoin, and was kicked again.");
        	player.getChannel().disconnect();
        	return;
        }
        
        // don't allow multiclients if settings forbid it
        if (!ServerConfiguration.getMultiClientMode())
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
                    player.getChannel().disconnect();
                    return;
                }
            }
        }
     	
        // otherwise ok
    	players.add(player);
        ServerContext.log(
        	"[player joined] New player added to channel " +
        	c.remoteAddress() + ". " +
        	printPlayers()
        );
    }


    /**
     * De-registers a player from the server
     *
     * @param channel   The channel that got de-registered
     */
    public void deRegisterPlayer(Channel channel) {
        Player player = getPlayerByChannel(channel);
        if (player != null) {
            player.setTurnFinished(true);
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
            p.getAnimationStructure().reset();
        }
    }

    /**
     * Gets a player instance by its channel
     * @param c The channel
     * @return  The player instance if found, null if not found
     */
    public Player getPlayerByChannel(Channel c) {
        for(Player p : players) {
            if (p.getChannel().equals(c)) {
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
                response = LoginResponsePacket.BAD_VERSION;
            }
            else if (getPlayerByName(name) != null) {
                response = LoginResponsePacket.NAME_IN_USE;
            }
            else if (
            	(name.contains(Constants.bannedSubstring)) ||
            	(name.length() <= 0) ||
            	(name.length() > Constants.MAX_NAME_SIZE))
            {
            	response = LoginResponsePacket.BAD_NAME;
            }
            else if (!Vessel.VESSEL_IDS.containsKey(ship)) {
                response = LoginResponsePacket.BAD_SHIP;
            }

            pl.getPackets().sendLoginResponse(response);

            if (response == LoginResponsePacket.SUCCESS) {
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
	            
	            // log
	            ServerContext.log(
	            	"[player left] De-registered and logged out player \"" + player.getName() + "\", on " +
	            	player.getChannel().remoteAddress() + ". " +
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
	            	player.getChannel().remoteAddress() + ". " +
	            	printPlayers()
	            );
            }
        }
    }

    /**
     * Sends and updates the time of the game, turn for all players
     */
    private void sendTime() {

        for (Player player : listRegisteredPlayers()) {
            player.getPackets().sendTime();
        }
    }

    public void resetSunkShips() {
        for (Player p : listRegisteredPlayers()) {
            if (p.isSunk()) {
                p.giveLife();
            }

            sendMoveBar(p);
            p.getAnimationStructure().reset();
        }
    }

    public void sendAfterTurn() {

        for (Player p : listRegisteredPlayers()) {
            if (p.isNeedsRespawn()) {
                p.respawn();
            }

            p.getAnimationStructure().reset();
            p.setTurnFinished(false);
            p.resetWaitingTicks();
        }

        for (Player p : listRegisteredPlayers()) {
            p.getPackets().sendPositions();
            p.getPackets().sendFlags();
            p.getPackets().sendPlayerFlags();
            if (p.isSunk()) {
                p.giveLife();
            }
            sendMoveBar(p);

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
    	
    	shouldSwitchMap = false;
        pointsAttacker = 0;
        pointsDefender = 0;

        for (Player p : listRegisteredPlayers()) {
        	p.setFirstEntry(true);
        	p.setNeedsRespawn(true);
        	p.getPackets().sendBoard();
        	p.getMoveTokens().setAutomaticSealGeneration(true); // bugfix disparity between client and server
        	p.getPackets().sendFlags();
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
        for(Player player : context.getPlayerManager().listRegisteredPlayers()) {
            player.getPackets().sendReceiveMessage(Constants.serverBroadcast, message);
        }
    }
    
    /**
     * send a message to a single player from the server
     */
    public void serverPrivateMessage(Player pl, String message)
    {
    	ServerContext.log("[chat] " + Constants.serverPrivate + "(to " + pl.getName() + "):" + message);
        pl.getPackets().sendReceiveMessage(Constants.serverPrivate, message);
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
				context.getTimeMachine().stop();
				setPersistTemporarySettings(false); // restart cancels temporary settings
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
					temporaryBannedIPs.add(playerToKick.getIP());
					playerToKick.getChannel().disconnect();
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
        			"The following Cadesim commands are supported: /info"
        	);
		}
    	else
    	{
    		serverPrivateMessage(
        			pl,
        			"The following Cadesim commands are supported: /propose, /vote, /info"
        	);
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
	    		else if (message.equals("/propose nextmap"))
	    		{
	    			handleStartVote(pl, message, "nextmap");
	    		}
	    		else if (message.startsWith("/propose kick"))
	    		{
	    			boolean found = false;
	    			for (Player registeredPlayer : listRegisteredPlayers())
	    			{
	    				if (message.equals("/propose kick " + registeredPlayer.getName()))
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
	    				"    nextmap (restarts round with new map)\n" +
	    				"    kick <player>\n" +
	    				"    set <parameter> <value> (temporarily set value)\n"
	    			);
	    		}
			}
    		else if (message.equals("/info"))
			{
    			// format messages nicely
    			int normalTurnDuration  = ServerConfiguration.getTurnDuration();
    			int normalRoundDuration = ServerConfiguration.getRoundDuration();
    			int normalRespawnDelay  = ServerConfiguration.getRespawnDelay();
    			String normalDisengageBehavior = ServerConfiguration.getDisengageBehavior();
    			String tmpTurnDuration  = (getTurnDuration()  != normalTurnDuration )?("[temporarily " + (getTurnDuration()  / 10) + "s] "    ):"";
    			String tmpRoundDuration = (getRoundDuration() != normalRoundDuration)?("[temporarily " + (getRoundDuration() / 10) + "s] "    ):"";
    			String tmpRespawnDelay  = (getRespawnDelay()  != normalRespawnDelay )?("[temporarily " + getRespawnDelay()         + " turns] "):"";
    			String tmpDisengageBehavior = (!getDisengageBehavior().equals(normalDisengageBehavior))?("[temporarily " + getDisengageBehavior() + "] "):"";
    			serverPrivateMessage(
    					pl,
    					Constants.name + " version " + Constants.VERSION + ", " +
    					"turn length " + tmpTurnDuration + (normalTurnDuration / 10) + "s, " +
    					"round length " + tmpRoundDuration + (normalRoundDuration / 10) + "s, " +
    					"sink penalty " + tmpRespawnDelay + normalRespawnDelay + " turns without moves, " +
    					"disengage behavior " + tmpDisengageBehavior + normalDisengageBehavior + ", " +
    					"map rotation " + ServerConfiguration.getMapRotationPeriod() + " rounds, " +
    					"current map " + ServerConfiguration.getMapName() + ", " +
    					"voting majority " + (ServerConfiguration.isVotingEnabled()?ServerConfiguration.getVotingMajority() + "%, ":"N/A [voting off], ") +
    					"jobbers quality " + ServerConfiguration.getJobbersQualityAsString()
    			);
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
