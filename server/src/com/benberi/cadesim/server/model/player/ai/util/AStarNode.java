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
  
}  