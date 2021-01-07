package com.benberi.cadesim.server.model.player.ai.util;
import java.util.*;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.util.Position;

/**
  The AStarSearch class, along with the AStarNode class,
  implements a generic A* search algorithm. The AStarNode
  class should be subclassed to provide searching capability.
*/
public class AStarSearch {
	ServerContext context;
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

    public List<AStarNode> findPath(Position start, Position goal){
        List<AStarNode> openList = new ArrayList<AStarNode>();
        List<AStarNode> closedList = new ArrayList<AStarNode>();
        AStarNode current = new AStarNode(start, null, 0, start.distance(goal));
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
            for(int i = 0; i < 9; i++) {
                if (i == 4)continue;
                int x = current.position.getX();
                int y = current.position.getY();
                int xi = (i % 3) - 1;
                int yi = (i / 3) - 1;
                int at = context.getMap().getTile(x + xi, y + yi);
                if(at == 1 || at == 2) continue; // ignore rocks
                Position a = new Position(x + xi, y + yi);
                double gCost = current.gCost + current.position.distance(a);
                double hCost = a.distance(goal);
                AStarNode node = new AStarNode(a, current, gCost, hCost);
                if(positionInList(closedList, a) && gCost >= node.gCost) continue;
                if(!positionInList(openList, a) || gCost < node.gCost) openList.add(node);
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