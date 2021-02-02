package com.benberi.cadesim.server.model.player.ai.util;

import java.util.ArrayList;
import java.util.List;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

/*
 * Class used as a state/nodes in A* algorithm
 */
public class AStarNode {
	List<AStarNode> neighbors = new ArrayList<AStarNode>();
	public Position position;
	public VesselFace face;
	public MoveType move;
	public AStarNode parent;
	public double fCost, gCost, hCost;
	public int step;
	
	public AStarNode(Position position, VesselFace face, MoveType move, AStarNode parent, double gCost, double hCost, int step) {
		this.position = position;
		this.face = face;
		this.move = move;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
		this.step = step;
	}
	/*
	 * Node if no move is placed
	 */
	public AStarNode MoveNone() {
		return new AStarNode(position.copy(), face, MoveType.NONE, this, 0, 0, step + 1);
    }
	/*
	 * Node if move forward from current position
	 */
	public AStarNode MoveForward() {
		return new AStarNode(position.copy().add(Position.Forward(face)), face, MoveType.FORWARD, this, 0, 0, step + 1);
    }
	/*
	 * Node if move left from current position
	 */
	public AStarNode MoveLeft() {
		return new AStarNode(position.copy().add(Position.Left(face)), face.getPrev(), MoveType.LEFT, this, 0, 0, step + 1);
    }
	/*
	 * Node if turn in place (left of current position) - used for obstacles
	 */
	public AStarNode TurnLeft() {
		return new AStarNode(position.copy(), face.getPrev(), MoveType.LEFT, this, 0, 0, step + 1);
    }
	/*
	 * Node if move right from current position
	 */
	public AStarNode MoveRight() {
		return new AStarNode(position.copy().add(Position.Right(face)), face.getNext(), MoveType.RIGHT, this, 0, 0, step + 1);
    }
	/*
	 * Node if turn in place (right of current position) - used for obstacles
	 */
	public AStarNode TurnRight() {
		return new AStarNode(position.copy(), face.getNext(), MoveType.RIGHT, this, 0, 0, step + 1);
    }
	
	public void addNeighbors(ServerContext context, Player start) {
    	neighbors.add(this.MoveNone());
    	if(context.getMap().isRock(this.MoveForward().position.getX(), this.MoveForward().position.getY(), start)) {
    		neighbors.add(this.TurnLeft()); // turn in place
        	neighbors.add(this.TurnRight());
    	}else {
        	neighbors.add(this.MoveForward());
        	if(context.getMap().isRock(this.MoveLeft().position.getX(), this.MoveLeft().position.getY(), start)) {
        		neighbors.add(this.TurnLeft()); // turn in place
        		neighbors.add(this.TurnRight());
        	}else {
        		neighbors.add(this.MoveLeft());
        	}
        	if(context.getMap().isRock(this.MoveRight().position.getX(), this.MoveRight().position.getY(), start)) {
        		neighbors.add(this.TurnLeft()); // turn in place
        		neighbors.add(this.TurnRight());
        	}else {
        		neighbors.add(this.MoveRight());
        	}
    	}
    }
    
    public List<AStarNode> getNeighbors(){
    	return this.neighbors;
    }
  
}  