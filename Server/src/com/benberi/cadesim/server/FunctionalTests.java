package com.benberi.cadesim.server;

import java.util.List;

import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;

/**
 * A light framework for functional collision tests.
 *
 * Usage: construct repeat per turn: loadNextTest <turn plays out> evaluateTest
 * unloadTest
 */
public class FunctionalTests {

    /**
     * Test one ship. Has three parts: starting scenario ending scenario
     * requirements longevity (measure over 1, 2, ... , n turns)
     */
    private class TestShip {
        // scenario
        public String name = null;
        public Vessel vessel = null;
        public Team team = null;

        public float startDamage = -1;
        public int[] startPosition = null;
        public VesselFace startFace = null;

        public MoveType[] moves = { null, null, null, null };
        public boolean[] shots = { false, false, false, false };

        // evaluation
        public float endDamage = -1;
        public int[] endPosition = null;
        public VesselFace endFace = null;

        // longevity
        public int longevity = -1;

        public TestShip(String name, Vessel vessel, Team team, float startDamage, int[] startPosition,
                VesselFace startFace, MoveType[] moves, boolean[] shots, float endDamage, int[] endPosition,
                VesselFace endFace, int longevity) {
            this.name = name;
            this.vessel = vessel;
            this.team = team;
            this.startDamage = startDamage;
            this.startPosition = startPosition;
            this.startFace = startFace;
            this.moves = moves;
            this.shots = shots;
            this.endDamage = endDamage;
            this.endPosition = endPosition;
            this.endFace = endFace;
            this.longevity = longevity;
        }
    }

    public class TestScenario {
        public List<TestShip> testShips;

        public TestScenario(List<TestShip> l) {
            testShips = l;
        }
    }

    private ServerContext context;
    private List<TestShip> listOfScenarios;
    private int scenarioIndex;

    public FunctionalTests(ServerContext context) {
        this.context = context;
        scenarioIndex = 0;

        // TODO #71 create test cases
        // 1. create TestShip objects
        // 2. load into TestScenario e.g. scenario1
        // 3. this.testScenarios.append(scenario1);
        // n. this.testScenarios.append(scenario2);
    }

    public boolean loadNextScenario() {
        if (scenarioIndex >= this.listOfScenarios.size()) {
            return false;
        }

        // TODO #71 prepare the next scenario
        // by loading scenarios from this.testScenarios,
        // current index
        return true;
    }

    public void evaluateScenario() {
        // TODO #71 evaluate the current scenario
        // by looking at this.testScenarios at scenarioIndex
    }

    public void unloadScenario() {
        // TODO #71 unload the current scenario

        scenarioIndex += 1;
    }

}
