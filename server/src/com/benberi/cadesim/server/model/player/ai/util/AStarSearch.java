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
        List<AStarNode> neighbors = new ArrayList<AStarNode>();
        int leftAmount = bot.getMoveTokens().countLeftMoves();
        int forwardAmount = bot.getMoveTokens().countForwardMoves();
        int rightAmount = bot.getMoveTokens().countRightMoves();
        AStarNode current = new AStarNode(bot, bot.getFace(), MoveType.NONE, null, 0, bot.distance(goal));
        openList.add(current);
        while(openList.size() > 0) {
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
                if(path.size() > 3 && bot.getVessel().has3Moves()) {
                	path = path.subList(0, 3);
                }else if(path.size() > 4 && !(bot.getVessel().has3Moves())) {
                	path = path.subList(0, 4);
                }
                return path;
            }
            openList.remove(current);
            closedList.add(current);
            //add specific neighbors
        	neighbors.add(current.MoveNone());
        	if(context.getMap().isRock(current.MoveForward().position.getX(), current.MoveForward().position.getY(), bot)) {
        		neighbors.add(current.TurnLeft()); // turn in place
            	neighbors.add(current.TurnRight());
        	}else {
            	neighbors.add(current.MoveForward());
            	if(context.getMap().isRock(current.MoveLeft().position.getX(), current.MoveLeft().position.getY(), bot)) {
            		neighbors.add(current.TurnLeft()); // turn in place
            		neighbors.add(current.TurnRight());
            	}else {
            		neighbors.add(current.MoveLeft());
            	}
            	if(context.getMap().isRock(current.MoveRight().position.getX(), current.MoveRight().position.getY(), bot)) {
            		neighbors.add(current.TurnLeft()); // turn in place
            		neighbors.add(current.TurnRight());
            	}else {
            		neighbors.add(current.MoveRight());
            	}
        	}
            for(AStarNode neighborNode : neighbors) {
            	if(context.getMap().isOutOfBounds(neighborNode.position.getX(), neighborNode.position.getY()))continue;
            	//skip if out of particular move
            	if((leftAmount == 0 && neighborNode.move == MoveType.LEFT) ||
            			(rightAmount == 0 && neighborNode.move == MoveType.RIGHT) ||
            			(forwardAmount == 0 && neighborNode.move == MoveType.FORWARD)) {
            		continue;
            	}
            	// Compute the cost to get *to* the action tile.
                double costToReach = current.position.distance(neighborNode.position);
            	
            	if(neighborNode.move == MoveType.FORWARD) costToReach += 0.2;
            	if(current.position.equals(neighborNode.position) && neighborNode.move != MoveType.NONE) costToReach += 0.8; // when ship is trapped by rock
                int at = context.getMap().getTile(neighborNode.position.getX(), neighborNode.position.getY());
                if(context.getMap().isWind(at)){ // special action tiles
                	neighborNode.position = context.getMap().getNextActionTilePositionForTile(neighborNode.position, at);
                	costToReach += getActionCost(neighborNode, at);
                }
            	if(context.getMap().isWhirlpool(at)) {
            		neighborNode.position = context.getMap().getFinalActionTilePosition(at, neighborNode.position, 0);
            		neighborNode.face = neighborNode.face.getNext();
            		costToReach += getActionCost(neighborNode, at);
            	}
                AStarNode node = new AStarNode(neighborNode.position, neighborNode.face,neighborNode.move, current, current.gCost + costToReach, heuristicDistance(neighborNode.position,goal));
                if(positionInList(closedList, neighborNode.position) && current.gCost >= node.gCost)continue;
                if(!positionInList(openList, neighborNode.position) || current.gCost < node.gCost) openList.add(node);
            }
            neighbors.clear(); // clear neighbors after iterating.
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