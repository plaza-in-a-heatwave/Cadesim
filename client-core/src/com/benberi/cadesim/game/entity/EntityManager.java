package com.benberi.cadesim.game.entity;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.move.MovePhase;
import com.benberi.cadesim.util.Team;

import java.util.*;

/**
 * Containing all kind of entities in the game
 */
public class EntityManager {

    private GameContext context;

    /**
     * List of vessel entities in the game
     */
    private List<Vessel> vessels = new ArrayList<Vessel>();

    public EntityManager(GameContext context) {
        this.context = context;
    }

    /**
     * Adds an entity
     */
    public void addEntity(String name, int x, int y, int face, int ship, int team) {
        Vessel vessel = Vessel.createVesselByType(context, name, x, y, ship);
        vessel.setTeam(Team.forId(team));
        vessel.create();
        vessel.setRotationIndex(face);
        vessels.add(vessel);
    }

    public Vessel getVessel(int index) {
        return vessels.get(index);
    }

    public Vessel getVesselByPosition(float x, float y) {
        clearNulls();
        for (Vessel vessel : vessels) {
            if (Math.round(vessel.getX()) == x && Math.round(vessel.getY()) == y) {
                return vessel;
            }
        }
        return null;
    }

    public void clearNulls() {
        vessels.removeIf(Objects::isNull);
    }

    /**
     * Lists all vessels
     * @return  The list of vessels
     */
    public List<Vessel> listVesselEntities() {
        return vessels;
    }

    public int countVesselsByPhase(MovePhase phase) {
        int count = 0;
        for (Vessel vessel : vessels) {
            if (vessel.isSinking()) {
                continue;
            }
            if (vessel.getMovePhase() == phase) {
                count++;
            }
        }

        return count;
    }

    public boolean hasDelayedVessels() {
        for (Vessel v : vessels) {
            if (v.hasDelay()) {
                return true;
            }
        }
        return false;
    }

    public List<Vessel> listCoordinateSortedVessels() {
        List<Vessel> v = new ArrayList<>();
        v.addAll(vessels);
        v.sort((o1, o2) -> {
            int result = Float.compare(o1.getX(), o2.getX());
            if (result == 0) {
                return Float.compare(o1.getY(), o2.getY());
            }
            return result;
        });

        Collections.reverse(v);
        return v;
    }

    /**
     * Gets a vessel by name
     * @param name  The name
     * @return The vessel instance
     */
    public Vessel getVesselByName(String name) {
        for (Vessel vessel : vessels) {
            if (vessel.getName().equalsIgnoreCase(name)) {
                return vessel;
            }
        }

        return null;
    }

    /**
     * Counts vessels
     * @return  The number of vessels
     */
    public int count() {
        return vessels.size();
    }

    public void dispose() {
        vessels.clear();
    }

    public int countNonSinking() {
        return (int) vessels.stream().filter(v -> !v.isSinking()).count();
    }
    
    public int countSinking() {
        return (int) vessels.stream().filter(v -> v.isSinking()).count();
    }

    public void remove(Vessel vessel) {
        vessels.removeIf(vessel1 -> vessel1 == vessel);
    }

    public void clearFlagSymbols() {
        for (Vessel v : vessels) {
            v.clearFlags();
        }
    }

    public void updatePlayerScoreAffects() {
        for (Vessel vessel : listVesselEntities()) {
            if (vessel.getFlags().size() > 0) {
                vessel.setScoreDisplayMovement();
            }
        }
    }
}
