package com.benberi.cadesim.server.model.player.ai.util;
import java.util.*;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
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

    public List<AStarNode> findPath(VesselFace startFace, Position start, Position goal){
        openList = new ArrayList<AStarNode>();
        closedList = new ArrayList<AStarNode>();
        List<AStarNode> neighbors = new ArrayList<AStarNode>();
        AStarNode current = new AStarNode(start, startFace, null, 0, start.distance(goal));
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
                return path;
            }
            openList.remove(current);
            closedList.add(current);
            
            int x = current.position.getX();
            int y = current.position.getY();
            switch (current.face) {
	            case NORTH:
	                neighbors.add(new AStarNode(new Position(x-1, y+1), VesselFace.WEST,current,0,0));
	                neighbors.add(new AStarNode(new Position(x, y+1), VesselFace.NORTH,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y+1), VesselFace.EAST,current,0,0));
	                break;
	            case EAST:
	                neighbors.add(new AStarNode(new Position(x+1, y+1), VesselFace.NORTH,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y), VesselFace.EAST,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y-1), VesselFace.SOUTH,current,0,0));
	                break;
	            case SOUTH:
	                neighbors.add(new AStarNode(new Position(x-1, y-1), VesselFace.WEST,current,0,0));
	                neighbors.add(new AStarNode(new Position(x, y-1), VesselFace.SOUTH,current,0,0));
	                neighbors.add(new AStarNode(new Position(x+1, y-1), VesselFace.EAST,current,0,0));
	                break;
	            case WEST:
	                neighbors.add(new AStarNode(new Position(x-1, y+1), VesselFace.NORTH,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y), VesselFace.WEST,current,0,0));
	                neighbors.add(new AStarNode(new Position(x-1, y-1), VesselFace.SOUTH,current,0,0));
	                break;
            }
            for(AStarNode neighborNode : neighbors) {
                int at = context.getMap().getTile(neighborNode.position.getX(), neighborNode.position.getY());
                if(at > 2){ // special action tiles
                	//TODO - allow ship to navigate through action tiles by looking ahead
//                	neighborNode.position = context.getMap().getNextActionTilePositionForTile(neighborNode.position, at);
                }
                if(at == 1 || at == 2) continue; // ignore rocks
                double gCost = current.gCost + current.position.distance(neighborNode.position);
                double hCost = neighborNode.position.distance(goal);
                AStarNode node = new AStarNode(neighborNode.position, neighborNode.face, current, gCost, hCost);
                if(positionInList(closedList, neighborNode.position) && gCost >= node.gCost) continue;
                if(!positionInList(openList, neighborNode.position) || gCost < node.gCost) openList.add(node);
            }
        }
        closedList.clear();
        return null;
    }
    
    private boolean positionInList(List<AStarNode> list, Position position) {
        for(AStarNode n : list) {
            if(n.position.equals(position)) return true;
        }
        return false;
    }

}