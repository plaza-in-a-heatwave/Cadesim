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
