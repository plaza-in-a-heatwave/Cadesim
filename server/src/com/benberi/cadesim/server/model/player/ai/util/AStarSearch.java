package com.benberi.cadesim.server.model.player.ai.util;
import java.util.*;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.util.Position;

/**
  The AStarSearch class, along with the AStarNode class,
  implements a generic A* search algorithm. The AStarNode
  class should be subclassed to provide searching capability.
*/
public class AStarSearch {
	
	private ServerContext context;
	private List<AStarNode> openList;
	private List<AStarNode> closedList;
    
	private double ORTHOGONAL_COST = 1.0;
	private double DIAGONAL_COST = ORTHOGONAL_COST * Math.sqrt(2.0);
	
	public AStarSearch(ServerContext context) {
		this.context = context;
	}
	/*
	 * Comparator to sort list of nodes based on fCost; weighs different options
	 */
	private Comparator<AStarNode> nodeSorter = new Comparator<AStarNode>() {

        @Override
        public int compare(AStarNode n0, AStarNode n1) {
            if(n1.fCost < n0.fCost) return 1;
            if(n1.fCost > n0.fCost) return -1;
            return 0;
        }
        
    };
	/*
	 * Finds path from current bots location to goal position
	 */
    public List<AStarNode> findPath(Player bot, Position goal){
        openList = new ArrayList<AStarNode>();
        closedList = new ArrayList<AStarNode>();
        int leftAmount = bot.getMoveTokens().countLeftMoves();
        int forwardAmount = bot.getMoveTokens().countForwardMoves();
        int rightAmount = bot.getMoveTokens().countRightMoves();
        AStarNode current = new AStarNode(bot, bot.getFace(), MoveType.NONE, null, 0, bot.distance(goal), 0);
        openList.add(current);
        while(!openList.isEmpty()) {
            Collections.sort(openList, nodeSorter);
            current = openList.get(0);
            if(current.position.equals(goal)) { // if goal node found
                List<AStarNode> path = new ArrayList<AStarNode>();
                while(current.parent != null) { // retrace steps
                    path.add(current);
                    current = current.parent;
                }
                openList.clear();
                closedList.clear();
                Collections.reverse(path);
                return path;
            }
            if(current.step > 200)break;
            openList.remove(current);
            closedList.add(current);
            //add specific neighbors
            current.addNeighbors(context, bot);
            for(AStarNode neighbor : current.getNeighbors()) {
            	if(context.getMap().isOutOfBounds(neighbor.position.getX(), neighbor.position.getY()))continue;
            	//skip if out of particular move
            	if((leftAmount == 0 && neighbor.move == MoveType.LEFT) ||
            			(rightAmount == 0 && neighbor.move == MoveType.RIGHT) ||
            			(forwardAmount == 0 && neighbor.move == MoveType.FORWARD)) {
            		continue;
            	}
            	// Compute the cost to get *to* the action tile.
                double costToReach = current.position.distance(neighbor.position);
            	
            	if(neighbor.move == MoveType.FORWARD) costToReach += 0.5;
            	if(current.position.equals(neighbor.position) && neighbor.move != MoveType.NONE) costToReach += 0.8; // when ship is trapped by rock
                int at = context.getMap().getTile(neighbor.position.getX(), neighbor.position.getY());
                if(context.getMap().isWind(at)){ // special action tiles
                	neighbor.position = context.getMap().getNextActionTilePositionForTile(neighbor.position, at);
                	costToReach += getActionCost(neighbor, at);
                }
            	if(context.getMap().isWhirlpool(at)) {
            		neighbor.position = context.getMap().getFinalActionTilePosition(at, neighbor.position, 0);
            		neighbor.face = neighbor.face.getNext();
            		costToReach += getActionCost(neighbor, at);
            	}
                AStarNode node = new AStarNode(neighbor.position, neighbor.face,neighbor.move, current, current.gCost + costToReach, heuristicDistance(neighbor.position,goal), neighbor.step);
                if(positionInList(closedList, neighbor.position) && current.gCost >= node.gCost)continue;
                if(!positionInList(openList, neighbor.position) || current.gCost < node.gCost) openList.add(node);
            }
        }
        closedList.clear();
        return null;
    }
    /*
     * Returns a double value for cost to use a specific tile
     */
    private double getActionCost(AStarNode node, int currentTile) {
    	if(currentTile > 3 && currentTile < 11 && node.move == MoveType.NONE) {
    		return -0.8;
    	}else {
        	return 0.4;	
    	}
    }
	/*
	 * provides heuristic distance from current to goal positions
	 */
    private double heuristicDistance(Position current, Position goal) {
        int xDifference = Math.abs(goal.getX() - current.getX());
        int yDifference = Math.abs(goal.getY() - current.getY());

        int diagonal = Math.min(xDifference, yDifference);
        int orthogonal = xDifference + yDifference - 2 * diagonal;

        return orthogonal * ORTHOGONAL_COST + diagonal * DIAGONAL_COST;
    }
	/*
	 * Checks if position is in specified list
	 */
    private boolean positionInList(List<AStarNode> list, Position position) {
        for(AStarNode n : list) {
            if(n.position.equals(position)) return true;
        }
        return false;
    }

}