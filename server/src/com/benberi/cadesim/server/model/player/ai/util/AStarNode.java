package com.benberi.cadesim.server.model.player.ai.util;

import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public class AStarNode {

	public Position position;
	public VesselFace face;
	public MoveType move;
	public AStarNode parent;
	public double fCost, gCost, hCost;
	public int time;
	
	public AStarNode(Position position, VesselFace face, MoveType move, AStarNode parent, double gCost, double hCost) {
		this.position = position;
		this.face = face;
		this.move = move;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = this.gCost + this.hCost;
	}
	
	public AStarNode MoveNone() {
		return new AStarNode(position.copy(), face, MoveType.NONE, this, 0, 0);
    }
	
	public AStarNode MoveForward() {
		return new AStarNode(position.copy().add(Position.Forward(face)), face, MoveType.FORWARD, this, 0, 0);
    }
	
	public AStarNode MoveLeft() {
		return new AStarNode(position.copy().add(Position.Left(face)), face.getPrev(), MoveType.LEFT, this, 0, 0);
    }
	
	public AStarNode TurnLeft() {
		return new AStarNode(position.copy(), face.getPrev(), MoveType.LEFT, this, 0, 0);
    }
	
	public AStarNode MoveRight() {
		return new AStarNode(position.copy().add(Position.Right(face)), face.getNext(), MoveType.RIGHT, this, 0, 0);
    }
	
	public AStarNode TurnRight() {
		return new AStarNode(position.copy(), face.getNext(), MoveType.RIGHT, this, 0, 0);
    }
  
}  