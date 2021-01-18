package com.benberi.cadesim.server.model.player.ai.util;
import java.util.*;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
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

	private Comparator<AStarNode> nodeSorter = new Comparator<AStarNode>() {

        @Override
        public int compare(AStarNode n0, AStarNode n1) {
            if(n1.fCost < n0.fCost) return 1;
            if(n1.fCost > n0.fCost) return -1;
            return 0;
        }
        
    };
    
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
            if(current.position.equals(goal)) {
                List<AStarNode> path = new ArrayList<AStarNode>();
                while(current.parent != null) {
                    path.add(current);
                    current = current.parent;
                }
                openList.clear();
                closedList.clear();
                Collections.reverse(path);
                return path;
            }
            openList.remove(current);
            closedList.add(current);
            
            int x = current.position.getX();
            int y = current.position.getY();
            switch (current.face) {
	            case NORTH:
	            	neighbors.add(new AStarNode(new Position(x, y), VesselFace.NORTH, MoveType.NONE,current,0,0));
	            	neighbors.add(new AStarNode(new Position(x, y+1), VesselFace.NORTH, MoveType.FORWARD,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y+1), VesselFace.WEST, MoveType.LEFT,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y+1), VesselFace.EAST, MoveType.RIGHT,current,0,0));
	                break;
	            case EAST:
	            	neighbors.add(new AStarNode(new Position(x, y), VesselFace.EAST, MoveType.NONE,current,0,0));
	            	neighbors.add(new AStarNode(new Position(x+1, y), VesselFace.EAST, MoveType.FORWARD,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y+1), VesselFace.NORTH, MoveType.LEFT,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y-1), VesselFace.SOUTH, MoveType.RIGHT,current,0,0));
	                break;
	            case SOUTH:
	                neighbors.add(new AStarNode(new Position(x, y), VesselFace.SOUTH, MoveType.NONE,current,0,0));
	                neighbors.add(new AStarNode(new Position(x, y-1), VesselFace.SOUTH, MoveType.FORWARD,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y-1), VesselFace.WEST, MoveType.RIGHT,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y-1), VesselFace.EAST, MoveType.LEFT,current,0,0));
	                break;
	            case WEST:
	            	neighbors.add(new AStarNode(new Position(x, y), VesselFace.WEST, MoveType.NONE,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y), VesselFace.WEST, MoveType.FORWARD,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y+1), VesselFace.NORTH, MoveType.RIGHT,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y-1), VesselFace.SOUTH, MoveType.LEFT,current,0,0));
	                break;
            }
            for(AStarNode neighborNode : neighbors) {
            	//skip if out of particular move
            	if((leftAmount == 0 && neighborNode.move == MoveType.LEFT) ||
            			(rightAmount == 0 && neighborNode.move == MoveType.RIGHT) ||
            			(forwardAmount == 0 && neighborNode.move == MoveType.FORWARD)) {
            		continue;
            	}
            	// Compute the cost to get *to* the action tile.
                double costToReach = current.position.distance(neighborNode.position);
            	if(neighborNode.move == MoveType.FORWARD) {
            		costToReach += 0.2;
            	}
                int at = context.getMap().getTile(neighborNode.position.getX(), neighborNode.position.getY());
                if(context.getMap().isWind(at)){ // special action tiles
                	neighborNode.position = context.getMap().getNextActionTilePositionForTile(neighborNode.position, at);
                	costToReach += getActionCost(neighborNode.position, at);
                }
            	if(context.getMap().isWhirlpool(at)) {
            		neighborNode.position = context.getMap().getFinalActionTilePosition(at, neighborNode.position, 0);
            		neighborNode.face = neighborNode.face.getNext();
            		costToReach += getActionCost(neighborNode.position, at);
            	}
                if(at == 1 || at == 2) continue;
                if(context.getPlayerManager().getPlayerByPosition(neighborNode.position.getX(), neighborNode.position.getY()) != null) continue; // skip if player
                double gCost = current.gCost + costToReach;
                double hCost = heuristicDistance(neighborNode.position,goal);
                AStarNode node = new AStarNode(neighborNode.position, neighborNode.face,neighborNode.move, current, gCost, hCost);
                if(positionInList(closedList, neighborNode.position) && gCost >= node.gCost) continue;
                if(!positionInList(openList, neighborNode.position) || gCost < node.gCost) openList.add(node);
            }
        }
        closedList.clear();
        return null;
    }
    /*
     * Returns a double value for cost to use a specific tile
     */
    private double getActionCost(Position node, int currentTile) {
    	if(currentTile > 3 && currentTile < 11) {
    		return 0.2;
    	}else {
        	return 1;	
    	}
    }

    private double heuristicDistance(Position current, Position goal) {
        int xDifference = Math.abs(goal.getX() - current.getX());
        int yDifference = Math.abs(goal.getY() - current.getY());

        int diagonal = Math.min(xDifference, yDifference);
        int orthogonal = xDifference + yDifference - 2 * diagonal;

        return orthogonal * ORTHOGONAL_COST + diagonal * DIAGONAL_COST;
    }
    
    private boolean positionInList(List<AStarNode> list, Position position) {
        for(AStarNode n : list) {
            if(n.position.equals(position)) return true;
        }
        return false;
    }

}