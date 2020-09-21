package com.benberi.cadesim.server;

import java.util.ArrayList;
import java.util.List;

import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;

/**
 * A light framework for functional collision tests.
 *
 * Usage: every start of turn call loadNextScenario (~ setUp) <the turn (the
 * sequence of 4 move slots) plays out> after the turn ends, call
 * evaluateScenario, unloadScenario (~ setDown) call printSummary
 * 
 * Info: a TestScenario is something that you can test over a single turn. e.g.
 * the damage, position, orientation, sink states of a collection of ships.
 * 
 * a TestShip is one ship within a TestScenario, they can be moved, collided
 * etc. * expectDamage(...) * expectFace(...) * expectSunk(...) *
 * expectPosition(...)
 * 
 * In FunctionalTests(...) you must: 1. Define your TestScenario objects (each
 * optionally containing multiple TestShips) 2. populate listOfScenarios
 *
 * testEquals and testAlmostEquals are provided. In future it might help to use
 * a proper testing framework (e.g. junit) for this.
 */
public class FunctionalTests {

    /**
     * Test one ship. Has two parts: starting scenario and ending scenario
     */
    private class TestShip {
        // scenario
        public String name = null;
        public int vesselID = -1;
        public Team team = null;
        public MoveType[] moves;
        public int[][] shots;

        // evaluation - what to compare
        private boolean useDamage = false;
        private boolean usePosition = true; // mandatory
        private boolean useFace = true; //
        private boolean useSunk = false;

        // evaluation - values
        public float[] damage = { -1, -1 };
        public int[][] position = { { -1, -1 }, { -1, -1 } };
        public VesselFace[] face = { null, null };
        public int sunk = -1;

        // constructor - main parameters
        public TestShip(String name, int vesselID, Team team, MoveType[] moves, int[][] shots) {
            this.name = name;
            this.vesselID = vesselID;
            this.team = team;
            this.moves = moves;
            this.shots = shots;
        }

        // optional further parameters
        public void expectDamage(float initial, float expected) {
            useDamage = true;
            damage[0] = initial;
            damage[1] = expected;
        }

        public void expectPosition(int[] initial, int[] expected) {
            usePosition = true;
            position[0] = initial;
            position[1] = expected;
        }

        public void expectFace(VesselFace initial, VesselFace expected) {
            useFace = true;
            face[0] = initial;
            face[1] = expected;
        }

        public void expectSunk(int expectedTurn) {
            useSunk = true;
            sunk = expectedTurn;
        }

    }

    public class TestScenario {
        public List<TestShip> testShips;
        double damagePrecision;

        /**
         * Create a new test scenario.
         * 
         * @param l               list of ships and start/end conditions
         * @param damagePrecision damage must match within this amount.
         */
        public TestScenario(List<TestShip> l, double damagePrecision) {
            testShips = l;
            this.damagePrecision = damagePrecision;
        }
    }

    private ServerContext context;
    private List<TestScenario> listOfScenarios;
    private int scenarioIndex;
    private int passes = 0, fails = 0, total = 0;
    private List<Player> listOfBotsCreated;

    public FunctionalTests(ServerContext context) {
        this.context = context;
        scenarioIndex = 0;
        listOfBotsCreated = new ArrayList<>();
        listOfScenarios = new ArrayList<>();

        // TODO #71 create initial set of test cases

        // example - test ship
        int[] t1_startPosition = { 0, 0 };
        int[] t1_endPosition = { 0, 0 };
        MoveType[] t1_moves = { MoveType.FORWARD, MoveType.LEFT, MoveType.RIGHT, MoveType.NONE };
        int[][] t1_shots = { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 1, 0 } };
        TestShip t1 = new TestShip("ship 1", Vessel.VESSEL_STRINGS.get("smsloop"), Team.DEFENDER,

                t1_moves, t1_shots);
        t1.expectPosition(t1_startPosition, t1_endPosition);
        t1.expectFace(VesselFace.EAST, VesselFace.WEST);

        // example - add this ship to our list of ships
        List<TestShip> s = new ArrayList<>();
        s.add(t1);

        // example - make a scenario using this ship list
        TestScenario scenario = new TestScenario(s, 0.1);

        // example - cue this scenario to run
        listOfScenarios.add(scenario);
    }

    /**
     * prepare the next scenario by spawning each ship & setting the conditions.
     * 
     * @return
     */
    public boolean loadNextScenario() {
        if (scenarioIndex >= this.listOfScenarios.size()) {
            return false;
        }

        // spawn each ship, set the start conditions
        TestScenario t = listOfScenarios.get(scenarioIndex);
        for (int i = 0; i < t.testShips.size(); i++) {
            // spawn
            assert ((t.testShips.get(i).position[0][0] >= 0) && (t.testShips
                    .get(i).position[0][1] >= 0)) : "[TestShip]: start position (x,y) must be defined >= 0.";
            assert (t.testShips
                    .get(i).face[0] != null) : "[TestShip]: start face (NORTH etc.) must be defined != null.";
            Player p = context.getPlayerManager().createBot(t.testShips.get(i).name, t.testShips.get(i).vesselID,
                    t.testShips.get(i).team, t.testShips.get(i).position[0], t.testShips.get(i).face[0],
                    t.testShips.get(i).useDamage ? t.testShips.get(i).damage[0] : null);

            // place the moves & shots
            for (int slot = 0; slot < 4; slot++) {
                // moves
                p.placeMove(slot, t.testShips.get(i).moves[slot].getId());

                // shots
                int lefts = t.testShips.get(i).shots[slot][0];
                int rights = t.testShips.get(i).shots[slot][1];
                if ((lefts > 1) || (rights > 1)) {
                    assert p.getVessel().isDualCannon() : "[TestShip]: bad test, ship must be dual cannon";
                }
                for (int shots = 0; shots < lefts; shots++) {
                    p.placeCannon(slot, 0);
                }
                for (int shots = 0; shots < rights; shots++) {
                    p.placeCannon(slot, 1);
                }
            }

            // add it to our list of current test scenario bots
            listOfBotsCreated.add(p);
        }
        return true;
    }

    /**
     * evaluate the current scenario by comparing expected with actual results.
     */
    public void evaluateScenario() {
        for (Player p : listOfBotsCreated) {
            TestShip s = findTestShipFromPlayer(p);

            if (s.usePosition) {
                testEquals("endposition", String.format("[%d,%d]", s.position[1][0], s.position[1][1]),
                        String.format("[%d,%d]", p.getX(), p.getY()));
            }

            if (s.useFace) {
                testEquals("endFace", s.face[1].toString(), p.getFace().toString());
            }

            if (s.useDamage) {
                testAlmostEquals("endDamage", (double) s.damage[1], (double) p.getVessel().getDamage(), (double) 0.1);
            }

            if (s.useSunk) {
                testEquals("endSunkTurn", s.sunk, p.getSunkTurn());
            }
        }

    }

    public void unloadScenario() {
        for (Player p : listOfBotsCreated) {
            context.getPlayerManager().removeBot(p);
        }
        listOfBotsCreated.clear();
        scenarioIndex += 1;
    }

    /**
     * Find a TestShip given a Player, so it can be evaluated.
     * 
     * @param p the player to find
     * @return
     */
    private TestShip findTestShipFromPlayer(Player p) {
        TestScenario t = listOfScenarios.get(scenarioIndex);
        TestShip testShip = null;
        for (int i = 0; i < t.testShips.size(); i++) {
            if (t.testShips.get(i).name.equals(p.getName())) {
                testShip = t.testShips.get(i);
                break;
            }
        }

        assert testShip != null : "[TestShip]: couldn't match bot in our list to a TestShip.";
        return testShip;
    }

    /**
     * Reset the tests so they will run again. You probably don't need to call this,
     * but it is provided in case.
     */
    public void resetTests() {
        scenarioIndex = 0;
        passes = 0;
        fails = 0;
        total = 0;
    }

    /**
     * Get a summary of total and pass/failed tests.
     */
    public void getSummary() {
        ServerContext.log("[FunctionalTests]: SUMMARY: " + total + "," + passes + "," + fails + "(total, pass, fail)");
    }

    /**
     * Helper method to perform an equality test.
     * 
     * @param description describe the test
     * @param expected    the expected finding
     * @param result      the actual finding
     * 
     *                    Objects must support the equals method (or be converted
     *                    from primitives).
     */
    private void testEquals(String description, Object expected, Object result) {
        total++;
        if (expected.equals(result)) {
            pass(description, expected, result);
        } else {
            fail(description, expected, result);
        }
    }

    private void pass(String description, Object expected, Object result) {
        passes++;
        ServerContext.log("[FunctionalTests]: PASS \"" + description + "\" (result: " + result.toString() + ")");
    }

    private void fail(String description, Object expected, Object result) {
        fails++;
        ServerContext.log("[FunctionalTests]: FAIL \"" + description + "\" (expected: " + expected.toString()
                + ", result: " + result.toString() + ")");
    }

    /**
     * Helper method to perform a near-equality floating point test.
     * 
     * @param description describe the test
     * @param expected    the expected finding
     * @param result      the actual finding
     * @param tolerance   how much error is tolerable (+/-)
     */
    private void testAlmostEquals(String description, double expected, double result, double tolerance) {
        total++;
        Double exp = new Double(expected);
        Double res = new Double(result);
        if ((((expected + tolerance) >= result) && (expected - tolerance <= result))
                || (((expected + tolerance) <= result) && (expected - tolerance >= result))) {
            pass(description, exp, res);
        } else {
            fail(description, exp, res);
        }
    }

}
