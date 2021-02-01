package com.benberi.cadesim.server.model.player.ai.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.util.Position;

public class Dijsktra {
	private ServerContext context;
	private double ORTHOGONAL_COST = 1.0;
	private double DIAGONAL_COST = ORTHOGONAL_COST * Math.sqrt(2.0);
	public Dijsktra(ServerContext context) {
		this.context = context;
	}
	
	/*
	 * Comparator to sort list of nodes based on fCost; weighs different options
	 */
	private Comparator<MoveState> nodeSorter = new Comparator<MoveState>() {

        @Override
        public int compare(MoveState n0, MoveState n1) {
            if(n1.cost < n0.cost) return 1;
            if(n1.cost > n0.cost) return -1;
            return 0;
        }
        
    };
	
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

	public List<MoveState> findPath(Player start, Position goal) {
		Queue<MoveState> frontier = new PriorityQueue<MoveState>(nodeSorter);
		List<MoveState> end = new ArrayList<MoveState>();
		List<MoveState> path = new ArrayList<MoveState>();
        int leftAmount = start.leftTokens();
        int forwardAmount = start.forwardTokens();
        int rightAmount = start.rightTokens();
		MoveState startState = new MoveState(start.get(), start.getFace(), MoveType.NONE, null, 0, (byte) 0);
		frontier.add(startState);
		while(!frontier.isEmpty()) {
			MoveState current = frontier.poll();
			if(current.position.equals(goal)) {
				end.add(current);
				break;
			}
			if(current.time == (start.getVessel().has3Moves() ? 3 :4)) end.add(current);
			if(current.time < (start.getVessel().has3Moves() ? 3 : 4)) current.addNeighbors(context, start); //look ahead twice number of moves
			//TO-DO 3 moves ahead causes bot to run into walls
			for(MoveState neighbor : current.getNeighbors()) {
				if(neighbor == null) continue;
				if(context.getMap().isOutOfBounds(neighbor.position.getX(), neighbor.position.getY())) continue;
            	//skip if out of particular move
            	if((leftAmount == 0 && neighbor.move == MoveType.LEFT) || (rightAmount == 0 && neighbor.move == MoveType.RIGHT) ||
            			(forwardAmount == 0 && neighbor.move == MoveType.FORWARD)) continue;
            	double costToReach = heuristicDistance(current.position, neighbor.position);
        		if(neighbor.move == MoveType.FORWARD) costToReach += 0.1;
        		if(current.position.equals(neighbor.position) && neighbor.move == MoveType.NONE) continue;
            	if(current.position.equals(neighbor.position) && neighbor.move != MoveType.NONE) costToReach += 0.5; // when ship is trapped by rock
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
            	double newCost = current.cost + costToReach;
            	if(!positionInList(end, neighbor.position) || newCost < neighbor.cost) {
            		neighbor.cost = newCost;
            		frontier.add(neighbor);
            	}
			}
		}
		MoveState min = null;
		if(!end.isEmpty()) { 
			min = end.stream()
					.filter(state -> !state.position.equals(startState.position) && state.move != MoveType.NONE )
					.min(Comparator.comparingDouble(state -> state.position.distance(goal))).get();
			while(min.parent != null) { // retrace steps
	            path.add(min);
	            min = min.parent;
	        }
	        Collections.reverse(path);
	        return path;
		}
		return null;
	}
	
    /*
     * Returns a double value for cost to use a specific tile
     */
    private double getActionCost(MoveState node, int currentTile) {
    	if(currentTile > 3 && currentTile < 11 && node.move == MoveType.NONE) {
    		return -0.8;
    	}else {
        	return 0.4;	
    	}
    }
    
	/*
	 * Checks if position is in specified list
	 */
    private boolean positionInList(List<MoveState> list, Position position) {
        for(MoveState n : list) {
            if(n.position.equals(position)) return true;
        }
        return false;
    }
}
