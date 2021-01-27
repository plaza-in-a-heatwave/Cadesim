package com.benberi.cadesim.server.model.player.ai.util;

import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public class MoveState {
	public Position position;
	public VesselFace face;
	public MoveType move;
	public byte time;
	public boolean visited = false;
	
	public MoveState(Position position, VesselFace face, MoveType move, byte time) {
		this.position = position;
		this.face = face;
		this.move = move;
		this.time = time;
		visited = false;
	}
	
	public MoveState MoveNone() {
		return new MoveState(position.copy(), face, MoveType.NONE, time);
    }
	
	public MoveState MoveForward() {
		return new MoveState(position.copy().add(Position.Forward(face)), face, MoveType.FORWARD, time);
    }
	
	public MoveState MoveLeft() {
		return new MoveState(position.copy().add(Position.Left(face)), face.getPrev(), MoveType.LEFT, time);
    }
	
	public MoveState TurnLeft() {
		return new MoveState(position.copy(), face.getPrev(), MoveType.LEFT, time);
    }
	
	public MoveState MoveRight() {
		return new MoveState(position.copy().add(Position.Right(face)), face.getNext(), MoveType.RIGHT, time);
    }
	
	public MoveState TurnRight() {
		return new MoveState(position.copy(), face.getNext(), MoveType.RIGHT, time);
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
}
