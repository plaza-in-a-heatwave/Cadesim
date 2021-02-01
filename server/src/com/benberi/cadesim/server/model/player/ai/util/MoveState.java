package com.benberi.cadesim.server.model.player.ai.util;

import java.util.ArrayList;
import java.util.List;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public class MoveState {
	List<MoveState> neighbors = new ArrayList<MoveState>();
	public Position position;
	public VesselFace face;
	public MoveType move;
	public byte time;
	public boolean visited = false;
	public double cost = 0;
	public MoveState parent;
	
	public MoveState(Position position, VesselFace face, MoveType move, MoveState parent, double cost, byte time) {
		this.position = position;
		this.face = face;
		this.move = move;
		this.time = time;
		this.cost = cost;
		this.parent = parent;
		visited = false;
	}
	
	public MoveState MoveNone() {
		return new MoveState(position.copy(), face, MoveType.NONE,this , 0 ,(byte)(time + 1));
    }
	
	public MoveState MoveForward() {
		return new MoveState(position.copy().add(Position.Forward(face)), face, MoveType.FORWARD,this, 0.1 ,(byte)(time + 1));
    }
	
	public MoveState MoveLeft() {
		return new MoveState(position.copy().add(Position.Left(face)), face.getPrev(), MoveType.LEFT,this, 0.1 ,(byte)(time + 1));
    }
	
	public MoveState TurnLeft() {
		return new MoveState(position.copy(), face.getPrev(), MoveType.LEFT,this,0.1 , (byte)(time + 1));
    }
	
	public MoveState MoveRight() {
		return new MoveState(position.copy().add(Position.Right(face)), face.getNext(), MoveType.RIGHT,this, 0.1 ,(byte)(time + 1));
    }
	
	public MoveState TurnRight() {
		return new MoveState(position.copy(), face.getNext(), MoveType.RIGHT,this, 0.1 ,(byte)(time + 1));
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof MoveState) {
            MoveState other = (MoveState) o;
            return o == this || this.position.equals(other.position) && this.move == other.move && 
            		this.time == other.time && this.face == other.face;
        }
        return super.equals(o);
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
    
    public List<MoveState> getNeighbors(){
    	return this.neighbors;
    }
}
