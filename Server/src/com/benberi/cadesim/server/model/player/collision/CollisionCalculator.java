package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Collision Mechanism
 * @author Ben | Jony benberi545@gmail.com
 *
 * https://github.com/BenBeri/Obsidio-Server/
 */
public class CollisionCalculator {
    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The player manager
     */
    private PlayerManager players;

    public CollisionCalculator(ServerContext ctx, PlayerManager players) {
        this.context = ctx;
        this.players = players;
    }

    /**
     * Gets all players that are trying to claim the given position
     * @param pl        The player to check for
     * @param target    The target position to check
     * @param turn      The turn
     * @param phase     The move-phase step
     * @return A list of players that collided there
     */
    public List<Player> getPlayersTryingToClaim(Player pl, Position target, int turn, int phase) {
    	List<Player> collided = new ArrayList<>();
        for (Player p : players.listRegisteredPlayers()) {
        	MoveType move = p.getMoves().getMove(turn);
            Position next = p;
            Position previous = p;
            Position finalPosition = p;
            if (p == pl) {
            	continue;
            }
            
            if (!p.getCollisionStorage().isPositionChanged()) {
        		next = move.getNextPositionWithPhase(p, p.getFace(),phase);
        		previous = move.getNextPositionWithPhase(p, p.getFace(),0);
        		finalPosition = move.getFinalPosition(p, p.getFace());
            }
            if(phase == 0) {
	            if(next.equals(target)) {
	            	collided.add(p);
	            }
            }
            if(finalPosition.equals(target)) {
            	if(phase == 1 && p.getCollisionStorage().isCollided(turn, 0) &&
            			(pl.getMoves().getMove(turn) == MoveType.LEFT || pl.getMoves().getMove(turn) == MoveType.RIGHT)) {
            		collided.add(p);
            	}
            }else if(previous.equals(target)){
            	if(phase == 1) {
            		collided.add(p);
            	}
            }else if(phase == 1 && p.getCollisionStorage().isCollided(turn, 0)) {
            	continue;
            }else {
	            if(next.equals(target)) {
	            	collided.add(p);
	            }
            }
        }
        
        return collided;
    }

    public List<Player> getPlayersTryingToClaimByAction(Player pl, Position target, int turn, int phase) {
        List<Player> collided = new ArrayList<>();
        for (Player p : players.listRegisteredPlayers()) {
        	if(p == pl) {
        		continue;
        	}
            Position next = p;
            if(p.isSunk()) {
            	next = p;
            }else {
                if (!p.getCollisionStorage().isPositionChanged()) {
                	next = context.getMap().getNextActionTilePosition(p.getCollisionStorage().isOnAction() ? p.getCollisionStorage().getActionTile() : -1, p, phase);
                }
            }
            if(next.equals(target)) {
            	collided.add(p);
            }
        }
        return collided;
    }

    /**
     * Checks if a player has a collision according to his move, in the given turn and move-phase
     * @param p             The player to check
     * @param turn          The turn
     * @param phase         The move-phase step
     * @param setPosition   If to set the next position or not on non-collided result
     * @return  <code>TRUE</code> If the player was collided, <code>FALSE</code> if not.
     */
    public boolean checkCollision(Player p, int turn, int phase, boolean setPosition) {

        if (!setPosition && p.getCollisionStorage().isRecursionStarter()) {
            return false;
        }
        // The current selected move of the player
        MoveType move =  p.getMoves().getMove(turn);

        // If this player was bumped, and a move was not selected, we want to process the bump animation
        // But we have to check if the position to be bumped is available to be claimed
        if (move == MoveType.NONE && p.getCollisionStorage().isBumped()) {
            Position pos = p.getCollisionStorage().getBumpAnimation().getPositionForAnimation(p);
            Player claimed = players.getPlayerByPosition(pos.getX(), pos.getY());
            // Claiming checking for the new position for bump
            return claimed != null && (claimed.getMoves().getMove(turn) == MoveType.NONE || checkCollision(claimed, turn, phase, false));
        }
        // Use the current position as default.txt, imply we have already set it
        Position position = p;
        // If not set by default.txt on previous loops, gets the next position on the map for the given phase on the given move
        if (!p.getCollisionStorage().isPositionChanged()) {
            position = move.getNextPositionWithPhase(p, p.getFace(), phase);
        }
        // If the player has moved since his last position
        if (!position.equals(p)) {
            // Check for bounds collision with the border and increases damage if true
            if (checkBoundCollision(p, turn, phase) || checkRockCollision(p, turn, phase)) {
                p.getVessel().appendDamage(p.getVessel().getRockDamage(), Team.NEUTRAL);
                return true;
            }
            // Check if the next position is claimed by another player, null result if not
            Player claimed = players.getPlayerByPosition(position.getX(), position.getY());
            // If the result is not null, the position is claimed
            if (claimed != null) {
                if (claimed.getCollisionStorage().isCollided(turn)) {
                    collide(p, claimed, turn, phase);
                    return true;
                }

                Position claimedNextPos = claimed;
                if (!claimed.getCollisionStorage().isPositionChanged()) {
                    claimedNextPos = claimed.getMoves().getMove(turn).getNextPositionWithPhase(claimed, claimed.getFace(), phase);
                }

               // Check if the claimed position doesn't move away
                if (claimed.getMoves().getMove(turn) == MoveType.NONE || claimedNextPos.equals(claimed)) {
                    if (move != MoveType.FORWARD || claimed.getVessel().getSize() >= p.getVessel().getSize()) {
                        collide(p, claimed, turn, phase);
                    }

                    if (move == MoveType.FORWARD && canBumpPlayer(p, claimed, turn, phase)) {

                        if (canBumpPlayer(p, claimed, turn, phase)) {
                            bumpPlayer(claimed, p, turn, phase);
                        }
                        else {
                            p.getCollisionStorage().setCollided(turn, phase);
                            return true;
                        }


                        if (p.getVessel().getSize() > claimed.getVessel().getSize()) {
                        	p.set(position);
                            p.getCollisionStorage().setPositionChanged(true);
                        }
                    }
                    else if(claimed.isSunk()) {
                        collide(p, claimed, turn, phase);
                        return true;
                    }
                    else if(move == MoveType.FORWARD && outOfBump(p, claimed, turn, phase)) {
                        collide(p, claimed, turn, phase);
                    }

                    claimed.getVessel().appendDamage(p.getVessel().getRamDamage(), p.getTeam());

                    return true;
                }
                else if (claimedNextPos.equals(p)) { // If they switched positions (e.g nose to nose, F, F move)
                    collide(p, claimed, turn, phase);
                    collide(claimed, p, turn, phase);
                    return true;
                }
                else {
                    // Make sure that the claimed position moves away successfully
                    if (!checkCollision(claimed, turn, phase, false)) {
                    	return performNormalCollision(p,position,turn,phase,setPosition);
                    }
                    else {
                        // did not move successfully, collide
                        collide(p, claimed, turn, phase);
                        collide(claimed, p, turn, phase);
                        return true;
                    }
                }
            } else {
            	return performNormalCollision(p,position,turn,phase,setPosition);
            }
        }

        return false;
    }

    /**
     * Helper method to reduce duplicate coding
     * @param p        The current player
     * @param position The current position
     * @param turn     The current turn
     * @param phase    The current phase
     * @param setPosition Boolean that sets whether we want to change position or not
     * @return boolean if collides with another player, false if no collision
     */
    public boolean performNormalCollision(Player p, Position position, int turn, int phase, boolean setPosition) {
    	List<Player> collisions = getPlayersTryingToClaim(p, position, turn, phase);
    	if (collisions.size() > 0) { // Collision has happened
    		collisions.add(p);
            Player largest = getLargestSize(collisions);
            if (countPlayersForSize(collisions, largest.getVessel().getSize()) > 1) {
            	//Stop players from movement
                for (Player pl : collisions) {
                	MoveType move = pl.getMoves().getMove(turn);
                    if(phase == 0) {
                     	if(pl == p) {
                     		collide(pl, p, turn, phase);	
                    	}
                     	collide(pl, p, turn, phase);
                    }
                    if(phase == 1) {
                    	if(move == MoveType.FORWARD) {
                         	if(pl == p) {
                         		collide(pl, p, turn, 0);	
                        	}
                         	collide(pl, p, turn, 0);
                    	}else {
                    		if(pl.getCollisionStorage().isCollided(turn, 0)) {
                              	if(pl == p) {
                             		collide(pl, p, turn, 0);	
                            	}
                             	collide(pl, p, turn, 0);
                    		}else {
                             	if(pl == p) {
                             		collide(pl, p, turn, phase);	
                            	}
                             	collide(pl, p, turn, phase);
                    		}
                    	}
                    }
                }
            }
            else { //for collisions with different size ships
                for (Player pl : collisions) {
                	MoveType smallestMove = pl.getMoves().getMove(turn);
                    if (pl == largest) {
                        continue;
                    }
                    if(phase == 0) {
                     	if(pl == p) {
                     		collide(pl, largest, turn, phase);	
                    	}
                     	collide(pl, largest, turn, phase);	
                	}else {//phase 1 collisions
                		if(smallestMove == MoveType.FORWARD) {//fixes animation glitches for forwards
                			if(pl == p) {
                        		collide(pl, largest, turn, 0);	
                        	}
                            collide(pl, largest, turn, 0);
                		}else {
                			if(pl.getCollisionStorage().isCollided(turn, 0)) {
		                     	if(pl == p) {
		                    		collide(pl, largest, turn, 0);	
		                    	}
		                        collide(pl, largest, turn, 0);
                			}else {
		                     	if(pl == p) {
		                    		collide(pl, largest, turn, phase);	
		                    	}
		                        collide(pl, largest, turn, phase);
                			}
                		}
                	}
                }
                if (!largest.getCollisionStorage().isPositionChanged()) {
                    largest.set(position);
                    largest.getCollisionStorage().setPositionChanged(true);
                }
            }
            return true;
        } else {
            	if(setPosition) {
      				p.set(position);
                    p.getCollisionStorage().setPositionChanged(true);
            }
    	}
    	return false;
    }
    
    public boolean checkActionCollision(Player player, Position target, int turn, int phase, boolean setPosition) {
    	if (!setPosition && player.getCollisionStorage().isRecursionStarter()) {
            return false;
        }
    	
        if (player.equals(target)) {
            return false;
        }
        
        if (context.getMap().isRock(target.getX(), target.getY(), player) || isOutOfBounds(target)) {
        	player.getVessel().appendDamage(player.getVessel().getRockDamage(), Team.NEUTRAL);
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        
        Player claimed = players.getPlayerByPosition(target.getX(), target.getY());
        if (claimed != null) {
            Position next = claimed;
            if (!claimed.getCollisionStorage().isPositionChanged()) {
                next = context.getMap().getNextActionTilePosition(claimed.getCollisionStorage().isOnAction() ? claimed.getCollisionStorage().getActionTile() : -1, claimed, phase);
            }
            
            if (next.equals(player)) {
            	collide(player, claimed, turn, phase);
            	collide(claimed, player, turn, phase);
                return true;
            }
            else if (next.equals(claimed)) {
                player.getVessel().appendDamage(claimed.getVessel().getRamDamage(), claimed.getTeam());
                claimed.getVessel().appendDamage(player.getVessel().getRamDamage(), player.getTeam()); //needed if claimed is by a rock 
                Position bumpPos = context.getMap().getNextActionTilePositionForTile(claimed, context.getMap().getTile(player.getX(), player.getY()));
                if (players.getPlayerByPosition(bumpPos.getX(), bumpPos.getY()) == null && !isOutOfBounds(bumpPos)
                        && !context.getMap().isRock(bumpPos.getX(), bumpPos.getY(), player) && player.getVessel().getSize() >= claimed.getVessel().getSize()) {
                	claimed.set(bumpPos);
                    claimed.getVessel().appendDamage(player.getVessel().getRamDamage(), player.getTeam());
                    claimed.getCollisionStorage().setPositionChanged(true);
                    claimed.getCollisionStorage().setBumped(true);
                    claimed.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(context.getMap().getTile(player.getX(), player.getY())));
                }

                if (player.getVessel().getSize() <= claimed.getVessel().getSize()) {
                    player.getCollisionStorage().setCollided(turn, phase);
                    return true;
                }
            }
            if(checkActionCollision(claimed, next, turn, phase, false)) {
            	collide(player, claimed, turn, phase);
            	return true;
            }
        }
        List<Player> collided = getPlayersTryingToClaimByAction(player, target, turn, phase);
        if (collided.size() > 0) {
        	collide(player, collided.get(0), turn, phase); //get correct damage instead itself
        	int playerTile = context.getMap().getTile(player.getX(), player.getY());
            for (Player p : collided) {
            	int tile = context.getMap().getTile(p.getX(), p.getY());
            	//bug: face isn't set each turn; p may not always change face?
            	if(context.getMap().isWhirlpool(tile)) {
            		if(context.getMap().isWind(playerTile)) {
            			p.setFace(p.getFace().getNext());
            			collide(p, player, turn, phase);
            		}else {
            			collide(p, player, turn, phase);
            		}
            	}else {
                	collide(p, player, turn, phase);
            	}
            }
            return true;
        }
        if(!player.getCollisionStorage().isCollided(turn)) {
            if (setPosition) {
                player.set(target);
                player.getCollisionStorage().setPositionChanged(true);
            }
        }
        return false;
    }


    private boolean checkRockCollision(Player player, int turn, int phase) {
        MoveType move = player.getMoves().getMove(turn);
        Position pos = player;
        if (!player.getCollisionStorage().isPositionChanged()) {
            pos = move.getNextPositionWithPhase(player, player.getFace(), phase);
        }
        if (context.getMap().isRock(pos.getX(), pos.getY(), player)) {
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        return false;
    }

    /**
     * Checks if player's shoot by direction and face hits another player
     * @param source        The shooting player
     * @param direction     The shooting direction
     * @return The player instance if it hits any player, null if no hit
     */
    public Player getVesselForCannonCollide(Player source, Direction direction) {
        // calculate direction multiplier
        int dM = (direction == Direction.LEFT)?1:-1; // direction (-1 or 1)
        // calculate x,y offsets
        int xO = 0;
        int yO = 0;
        switch (source.getFace()) {
        case EAST:  yO =  1; break;
        case SOUTH: xO =  1; break;
        case WEST:  yO = -1; break;
        case NORTH: xO = -1; break;
        }

        // return the player
        for (int i = 1; i < 4; i++) { // i is basically # of spots away
    		int x = source.getX() + (xO * dM * i);
        	int y = source.getY() + (yO * dM * i);

            if (context.getMap().isBigRock(x, y)) {
                return null;
            }

            Player player = players.getPlayerByPosition(x, y);
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    /**
     * Counts players for given size in given list
     * @param players   The players
     * @param size      The size to count
     * @return The number of players with given size in given list
     */
    private int countPlayersForSize(List<Player> players, int size) {
        int count = 0;
        for (Player p : players) {
            if (p.getVessel().getSize() == size) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the largest ship class size out of list of players
     * @param players   The players list
     * @return The biggest class player, or null if no players.
     */
    private Player getLargestSize(List<Player> players) {
        int max = -1; // bugfix #45 three way collision between sloops.
        Player player = null;
        for (Player p : players) {
            if (p.getVessel().getSize() > max) {
                max = p.getVessel().getSize();
                player = p;
            }
        }

        return player;
    }

    /**
     * Bump a player by player
     * @param bumped   The bumped player
     * @param bumper   The bumper
     */
    private void bumpPlayer(Player bumped, Player bumper, int turn, int phase) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        bumped.getCollisionStorage().setBumpAnimation(anim);
        if (checkCollision(bumped, turn, phase, false)) {
            bumped.getCollisionStorage().setBumpAnimation(VesselMovementAnimation.NO_ANIMATION);
        }
    }

    /**
     * Checks if a player can bump the other player
     * @param bumper    The bumping player
     * @param bumped    The bumped player
     * @return  If the bump can happen
     */
    private boolean canBumpPlayer(Player bumper, Player bumped, int turn, int phase) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        Position bumpPosition = anim.getPositionForAnimation(bumped);
        return bumper.getVessel().getSize() >= bumped.getVessel().getSize() && !bumped.isSunk() && // TODO #26 the !bumped.isSunk() might cause a bug if we sink in one turn and try to move there
                !isOutOfBounds(bumpPosition) && getPlayersTryingToClaim(bumped, bumpPosition, turn, phase).size() == 0 &&
                !context.getMap().isRock(bumpPosition.getX(), bumpPosition.getY(), bumped);
    }
    
    /**
     * Checks whether a bump would push the other person out of bounds, but is still a legal bump
     * @param bumper    The bumping player
     * @param bumped    The bumped player
     * @return  If the bump can happen
     */
    private boolean outOfBump(Player bumper, Player bumped, int turn, int phase) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        Position bumpPosition = anim.getPositionForAnimation(bumped);
        return (bumper.getVessel().getSize() >= bumped.getVessel().getSize() && !bumped.isSunk() // TODO #26 the !bumped.isSunk() might be source of a bug
                && getPlayersTryingToClaim(bumped, bumpPosition, turn, phase).size() == 0)
                && (context.getMap().isRock(bumpPosition.getX(), bumpPosition.getY(), bumped)
                || isOutOfBounds(bumpPosition));
    }
 
    /**
     * Collides a player, and damages him
     * @param player    The player that collided
     * @param other     The other player collided with, OR self
     * @param turn      The turn it happened at
     * @param phase     The phase-step it happened at
     */
    private void collide(Player player, Player other, int turn, int phase) {
    	player.getCollisionStorage().setCollided(turn, phase);
        player.getVessel().appendDamage(other.getVessel().getRamDamage(), other.getTeam());
    }


    /**
     * Checks out of bounds collision
     * @param player    The player to check
     * @param turn      The turn
     * @param phase     The move-phase step
     * @return TRUE if boudns collided FALSE if not
     */
    private boolean checkBoundCollision(Player player, int turn, int phase) {
        MoveType move = player.getMoves().getMove(turn);
        Position pos = player;
        if (!player.getCollisionStorage().isPositionChanged()) {
            pos = move.getNextPositionWithPhase(player, player.getFace(), phase);
        }
        if (isOutOfBounds(pos)) {
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        return false;
    }

    /**
     * Checks if a position is out of bounds of the map
     * @param pos   The position to check
     * @return TRUE if out of bounds FALSE if not
     */
    private boolean isOutOfBounds(Position pos) {
        return pos.getX() < 0 || pos.getX() > 19 || pos.getY() < 0 || pos.getY() > 35;
    }
}