package com.benberi.cadesim.server.model.player.ai;

import java.util.List;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.ai.util.AStarNode;
import com.benberi.cadesim.server.model.player.ai.util.NPC_Type;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

import io.netty.channel.Channel;

/*
 * AI Logic - chaser type (goes after player)
 * Priority - shooting, flag points
 */
public class NPC_Type4 extends Player {
    @SuppressWarnings("unused")
	private ServerContext context;
    private List<AStarNode> path = null;

    public NPC_Type4(ServerContext ctx, Channel c) {
    	super(ctx,c);
    	context = ctx;
        super.setBot(true);
        super.set(-1, -1); // not spawned
        super.setType(NPC_Type.TYPE4);
    }
    
    @Override
    public void calculateRoute() {
    	Position destination = new Position(3,3); // replace with cluster
    	if(this.equals(destination)) {
    		return;
    	}
    	path = context.getPlayerManager().getAlgorithm().findPath(this, destination);
    	VesselFace face = getFace();
    	if(path != null) {
    		if(path.size() > 0) {
    			int numberOfMoves = getVessel().has3Moves() ? 3 : 4;
    			Position currentPosition = this.copy();
    			for(int slot = 0; slot <= numberOfMoves; slot++) { //moves to enter
    				int positionIndex = (path.size() - 1) - (slot); //subtract slot to allow multiple moves
    				if(positionIndex < 0 || path.size() < slot) { // make sure it doesn't count too far
    					return;
    				}
					Position pos = path.get(positionIndex).position;
					Position left = MoveType.LEFT.getFinalPosition(currentPosition, face);
					Position right = MoveType.RIGHT.getFinalPosition(currentPosition, face);
					Position forward = MoveType.FORWARD.getFinalPosition(currentPosition, face);
					if(left.equals(pos)) {
						currentPosition.add(left.getX() - getX(), left.getY() - getY());
						getMoves().setMove(slot, MoveType.LEFT);
						switch(face) {
							case NORTH:
								face = VesselFace.WEST;
								break;
							case SOUTH:
								face = VesselFace.EAST;
								break;
							case WEST:
								face = VesselFace.SOUTH;
								break;
							case EAST:
								face = VesselFace.NORTH;
								break;
						}
					}else if(right.equals(pos)) {
						currentPosition.add(right.getX() - getX(), right.getY() - getY());
						getMoves().setMove(slot, MoveType.RIGHT);
						switch(face) {
							case NORTH:
								face = VesselFace.EAST;
								break;
							case SOUTH:
								face = VesselFace.WEST;
								break;
							case WEST:
								face = VesselFace.NORTH;
								break;
							case EAST:
								face = VesselFace.SOUTH;
								break;
						}
					}else if(forward.equals(pos)){
						currentPosition.add(forward.getX() - getX(), forward.getY() - getY());
						getMoves().setMove(slot, MoveType.FORWARD);
						switch(face) {
							case NORTH:
								face = VesselFace.NORTH;
								break;
							case SOUTH:
								face = VesselFace.SOUTH;
								break;
							case WEST:
								face = VesselFace.WEST;
								break;
							case EAST:
								face = VesselFace.EAST;
								break;
						}
					}
    			}
    		}
    	}
    }
    
    @Override
    public void performLogic() {
    	calculateRoute();
    }
    
	@Override
    public NPC_Type getType() {
		return type;
    }
}
