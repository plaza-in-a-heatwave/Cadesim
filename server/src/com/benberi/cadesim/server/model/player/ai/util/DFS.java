package com.benberi.cadesim.server.model.player.ai.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public class DFS {
    private ServerContext context;
    public Hashtable<Position, MoveState> stateList;
    
    public DFS(ServerContext context) {
        this.context = context;
    }
    
    public void search(Player player) {
        Position playerPosition = player.copy();
        List<MoveState> neighbors = new ArrayList<>();
        stateList = new Hashtable<Position, MoveState>();
        MoveState currentState = new MoveState(playerPosition, player.getFace(), MoveType.NONE, 0);
        Stack<MoveState> stack = new  Stack<MoveState>();
        stack.add(currentState);
        currentState.visited = true;
        while (!stack.isEmpty())
        {
            MoveState state = stack.pop();
            if(state.position.getX() < 0 || state.position.getY() < 0) {
            	continue;
            }
            stateList.put(state.position, state);
            if(state.time < 4) {
            	int x = state.position.getX();
            	int y = state.position.getY();
            	switch (state.face) {
	                case NORTH:
	                    neighbors.add(new MoveState(new Position(x,y), VesselFace.NORTH, MoveType.NONE,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x,y+1), VesselFace.NORTH, MoveType.FORWARD,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x-1,y+1), VesselFace.WEST, MoveType.LEFT, state.time + 1));
	                    neighbors.add(new MoveState(new Position(x+1,y+1), VesselFace.EAST, MoveType.RIGHT,state.time + 1));
	                    break;
	                case EAST:
	                    neighbors.add(new MoveState(new Position(x,y), VesselFace.EAST, MoveType.NONE,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x+1,y), VesselFace.EAST, MoveType.FORWARD,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x+1,y+1), VesselFace.NORTH, MoveType.LEFT,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x+1,y-1), VesselFace.SOUTH, MoveType.RIGHT,state.time + 1));
	                    break;
	                case SOUTH:
	                    neighbors.add(new MoveState(new Position(x,y), VesselFace.SOUTH, MoveType.NONE,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x,y-1), VesselFace.SOUTH, MoveType.FORWARD,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x-1,y-1), VesselFace.WEST, MoveType.RIGHT,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x+1,y+1), VesselFace.EAST, MoveType.LEFT,state.time + 1));
	                    break;
	                case WEST:
	                    neighbors.add(new MoveState(new Position(x,y), VesselFace.WEST, MoveType.NONE,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x-1,y), VesselFace.WEST, MoveType.FORWARD,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x-1,y+1), VesselFace.NORTH, MoveType.RIGHT,state.time + 1));
	                    neighbors.add(new MoveState(new Position(x-1,y-1), VesselFace.SOUTH, MoveType.LEFT,state.time + 1));
	                    break;
	            }
            }
            for(MoveState neighborState : neighbors) {
                if(neighborState != null ) {
                	if(stateList.values().contains(neighborState)) {
                		continue;
                	}
                    int at = context.getMap().getTile(neighborState.position.getX(), neighborState.position.getY());
                    if(at == 1 || at == 2) continue; // ignore rocks
                    if(context.getMap().isWind(at)){ // special action tiles
                    	neighborState.position = context.getMap().getNextActionTilePositionForTile(neighborState.position, at);
                    }
                	if(context.getMap().isWhirlpool(at)) {
                		neighborState.position = context.getMap().getFinalActionTilePosition(at, neighborState.position, 0);
                		neighborState.face = neighborState.face.getNext();
                	}
                    if(!neighborState.visited)
                    {
                        stack.add(neighborState);
                        neighborState.visited = true;
                    }
                }
            }
        }
    }
}
