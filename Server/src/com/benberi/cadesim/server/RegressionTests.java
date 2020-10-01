package com.benberi.cadesim.server;

import java.util.ArrayList;
import java.util.List;

import com.benberi.cadesim.server.model.cade.Team;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;

/**
 * A light framework for regression testing collisions.
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
 * In RegressionTests(...) you must: 1. Define your TestScenario objects (each
 * optionally containing multiple TestShips) 2. populate listOfScenarios
 *
 * testEquals and testAlmostEquals are provided. In future it might help to use
 * a proper testing framework (e.g. junit) for this.
 *
 * The coordinate system for the board is:
 * _______________
 * \         19,39\
 *  \              \
 *   \    NORTH     \
 *    \ WEST_\ _EAST \
 *     \      \       \
 *      \    SOUTH     \
 *       \              \
 *        \0,0           \
 *         '''''''''''''''
 *
 * All damages are in MEDIUM cb.
 */
public class RegressionTests {

    /////////////
    // aliases //
    /////////////

    // team types
    public static final Team    ATTACKER = Team.ATTACKER;
    public static final Team    DEFENDER = Team.DEFENDER;

    // shiptypes
    public static final String SLOOP     = "smsloop";
    public static final String CUTTER    = "lgsloop";
    public static final String DHOW      = "dhow";
    public static final String FANCHUAN  = "fanchuan";
    public static final String LS        = "longship";
    public static final String JUNK      = "junk";
    public static final String BAGHLAH   = "baghlah";
    public static final String MB        = "merchbrig";
    public static final String WB        = "warbrig";
    public static final String XEBEC     = "xebec";
    public static final String MG        = "merchgal";
    public static final String WF        = "warfrig";
    public static final String WG        = "wargal";
    public static final String GF        = "grandfrig";
    public static final String BLACKSHIP = "blackship";

    // moves
    public static final MoveType N = MoveType.NONE;
    public static final MoveType L = MoveType.LEFT;
    public static final MoveType F = MoveType.FORWARD;
    public static final MoveType R = MoveType.RIGHT;

    // faces
    public static final VesselFace NORTH = VesselFace.NORTH;
    public static final VesselFace EAST = VesselFace.EAST;
    public static final VesselFace SOUTH = VesselFace.SOUTH;
    public static final VesselFace WEST = VesselFace.WEST;

    /**
     * Test one ship. Has two parts: starting scenario and ending scenario
     */
    private static class TestShip {
        private static long uid = 0;
        // scenario
        public String name = null;
        public int vesselID = -1;
        public Team team = null;
        public MoveType[] moves = {N, N, N, N};
        public int[][]    shots = {{0,0}, {0,0}, {0,0}, {0,0}};

        // evaluation - item is active iff useItem is true
        private boolean useDamage = false;
        public float[] damage = { 0.0f, -1f }; // startDamage is mandatory

        private boolean usePosition = true;        // mandatory
        public int[][] position = { { -1, -1 }, { -1, -1 } };

        private boolean useFace = true;            // mandatory
        public VesselFace[] face = { null, null };

        private boolean useSunk = false;
        public int sunk = -1;

        // constructor - main parameters
        public TestShip(String vessel, Team team) {
            this.name = "ship" + uid;
            this.vesselID = Vessel.VESSEL_STRINGS.get(vessel);
            this.team = team;

            // increment the static instance counter
            uid += 1;
        }

        // mandatory parameters
        public void setMoves(MoveType m0, MoveType m1, MoveType m2, MoveType m3) {
            moves[0] = m0; moves[1] = m1; moves[2] = m2; moves[3] = m3;
        }
        public void setShots(int L0, int R0, int L1, int R1, int L2, int R2, int L3, int R3) {
            shots[0][0] = L0; shots[0][1] = R0; shots[1][0] = L1; shots[1][1] = R1;
            shots[2][0] = L2; shots[2][1] = R2; shots[3][0] = L3; shots[3][1] = R3;
        }
        public void setInitialDamage(float initial) {
            damage[0] = initial;
        }

        // optional further parameters
        public void expectChangeDamage(float initial, float expected) {
            useDamage = true;
            damage[0] = initial;
            damage[1] = expected;
        }

        public void expectChangePosition(int startX, int startY, int endX, int endY) {
            usePosition = true;
            position[0][0] = startX; position[0][1] = startY;
            position[1][0] = endX;   position[1][1] = endY;
        }

        public void expectChangeFace(VesselFace initial, VesselFace expected) {
            useFace = true;
            face[0] = initial;
            face[1] = expected;
        }

        /**
         * Test that ship sunk during a specific turn.
         * @param expectedTurn 0 <= t <= 3
         */
        public void expectSunkInTurn(int expectedTurn) {
            useSunk = true;
            sunk = expectedTurn;
        }

    }

    public class TestScenario {
        public String name;
        public List<TestShip> testShips;
        public static final double DEFAULT_DAMAGE_PRECISION = 0.1;
        double damagePrecision = DEFAULT_DAMAGE_PRECISION;

        /**
         * Create a new test scenario.
         * 
         * @param l               list of ships and start/end conditions
         * @param damagePrecision damage must match within this amount.
         */
        public TestScenario(String name, List<TestShip> l, double damagePrecision) {
            this.name            = name;
            this.testShips       = l;
            this.damagePrecision = damagePrecision;
        }
        public TestScenario(String name, List<TestShip> l) {
            this.name = name;
            testShips = l;
        }
    }

    private ServerContext context;
    private List<TestScenario> listOfScenarios;
    private int scenarioIndex;
    @SuppressWarnings("unused") //testFails variable
	private int testPasses     = 0, testFails     = 0, testTotal = 0;
    private int scenarioPasses = 0, scenarioFails = 0, scenarioTotal = 0;
    private List<Player> listOfBotsCreated;
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
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
                    .get(i).position[0][1] >= 0)) : "[TestShip]: bad test (" + t.name + "). Start position (x,y) must be defined >= 0.";
            assert (t.testShips
                    .get(i).face[0] != null) : "[TestShip]: bad test (" + t.name + "). Start face (NORTH etc.) must be defined != null.";
            Player p = context.getPlayerManager().createBot(
                    t.testShips.get(i).name,
                    t.testShips.get(i).vesselID,
                    t.testShips.get(i).team,
                    t.testShips.get(i).position[0],
                    t.testShips.get(i).face[0],
                    t.testShips.get(i).damage[0]
            );

            // place the moves & shots
            boolean usedAllSlots = true; // check number of moves is correct for ship
            int unusedSlot = -1;         // find an unused slot we can use for the manuaver piece

            // bugfix: move won't place if manuaver slot is there.
            // we have to find it and move it to a more appropriate place ("unusedSlot")
            // this requires a loop pass over slots to find it
            for (int slot = 0; slot < 4; slot++) {
                if (t.testShips.get(i).moves[slot] == MoveType.NONE) {
                    usedAllSlots = false;
                    unusedSlot = slot;
                }
            }
            if (p.getVessel().has3Moves()) {
                assert (!usedAllSlots) : "[TestShip]: bad test (" + t.name + "). ship can have 3 moves, but you have given it 4 moves.";
                p.getMoves().setManuaverSlot(unusedSlot);
            }

            for (int slot = 0; slot < 4; slot++) {
                // moves
                if ((!p.getVessel().has3Moves()) || (slot != unusedSlot)) {
                    p.placeMove(slot, t.testShips.get(i).moves[slot].getId());
                }

                // shots
                int lefts = t.testShips.get(i).shots[slot][0];
                int rights = t.testShips.get(i).shots[slot][1];
                if ((lefts > 1) || (rights > 1)) {
                    assert p.getVessel().isDualCannon() : "[TestShip]: bad test (" + t.name + "). ship is not dual cannon, but you have given it dual shots.";
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
                testEquals(listOfScenarios.get(this.scenarioIndex).name, "endposition", String.format("[%d,%d]", s.position[1][0], s.position[1][1]),
                        String.format("[%d,%d]", p.getX(), p.getY()));
            }

            if (s.useFace) {
                testEquals(listOfScenarios.get(this.scenarioIndex).name, "endFace", s.face[1].toString(), p.getFace().toString());
            }

            if (s.useDamage) {
                testAlmostEquals(listOfScenarios.get(this.scenarioIndex).name, "endDamage", (double) s.damage[1], (double) p.getVessel().getDamage(), (double) 0.1);
            }

            if (s.useSunk) {
                testEquals(listOfScenarios.get(this.scenarioIndex).name, "endSunkTurn", s.sunk, p.getSunkTurn());
            }
        }

    }

    public void unloadScenario() {
        for (Player p : listOfBotsCreated) {
            context.getPlayerManager().removeBot(p);
        }
        listOfBotsCreated.clear();
        scenarioIndex += 1;

        // reset individual test stats
        if (testPasses == testTotal) {
            scenarioPasses++;
        } else {
            scenarioFails++;
        }
        scenarioTotal++;
        this.clearLastScenarioStats();
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
    public void resetScenarios() {
        scenarioIndex = 0;
        scenarioPasses = 0;
        scenarioFails  = 0;
        scenarioTotal  = 0;
        clearLastScenarioStats();
    }

    /**
     * helper method to clear stats from last scenario
     */
    private void clearLastScenarioStats() {
        testPasses = 0;
        testFails  = 0;
        testTotal  = 0;
    }

    /**
     * Get a summary of total and pass/failed tests.
     */
    public void getSummary() {
        ServerContext.log("[regressiontests]: SUMMARY: " +
            "+" + scenarioPasses + ", -" + scenarioFails + " (total: " + scenarioTotal + ")");

        if (0 == scenarioTotal) {
            ServerContext.log("[regressiontests]:     (did you forget to include some scenarios?");
        }
    }

    /**
     * Helper method to perform an equality test.
     * 
     * @param scenario    describe the scenario
     * @param test        describe the test
     * @param expected    the expected finding
     * @param result      the actual finding
     * 
     *                    Objects must support the equals method (or be converted
     *                    from primitives).
     */
    private void testEquals(String scenario, String test, Object expected, Object result) {
        testTotal++;
        if (expected.equals(result)) {
            pass(scenario, test, expected, result);
        } else {
            fail(scenario, test, expected, result);
        }
    }

    /**
     * Helper method to perform a near-equality floating point test.
     * 
     * @param scenario    describe the scenario
     * @param test        describe the test
     * @param expected    the expected finding
     * @param result      the actual finding
     * @param tolerance   how much error is tolerable (+/-)
     */
    private void testAlmostEquals(String scenario, String test, double expected, double result, double tolerance) {
        testTotal++;
        Double exp = new Double(expected);
        Double res = new Double(result);
        if ((((expected + tolerance) >= result) && (expected - tolerance <= result))
                || (((expected + tolerance) <= result) && (expected - tolerance >= result))) {
            pass(scenario, test, exp, res);
        } else {
            fail(scenario, test, exp, res);
        }
    }

    private void pass(String scenario, String test, Object expected, Object result) {
        testPasses++;
        if (isVerbose()) {
            ServerContext.log("[regressiontests]: PASS \"" + scenario + "\" - \"" + test + "\" (result: " + result.toString() + ")");
        }
    }

    private void fail(String scenario, String test, Object expected, Object result) {
        testFails++;
        ServerContext.log("[regressiontests]: FAIL \"" + scenario + "\" - \"" + test + "\" (expected: " + expected.toString()
                + ", result: " + result.toString() + ")");
    }

    public RegressionTests(ServerContext context, boolean verbose) {
        this.context = context;
        setVerbose(verbose);
        scenarioIndex = 0;
        listOfBotsCreated = new ArrayList<>();
        listOfScenarios = new ArrayList<>();


        //////////////
        // examples //
        //////////////

        // example - test ship. Tests: 4 moves, changeface, wind
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,L,F,F);
            t1.setShots(0,1, 1,0, 1,0, 1,0);
            t1.expectChangePosition(0,0, 2,4);
            t1.expectChangeFace(EAST, NORTH);

            // boilerplate: add ships to scenario, and cue scenario to run.
            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            listOfScenarios.add(new TestScenario("example", s));
        }

        /////////////////
        // basic tests //
        /////////////////

        // two ships equal size, both bump, safe zones, no damage
        {
            // north safe
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,L,N,N);
            t1.expectChangePosition(4,35, 4,35);
            t1.expectChangeFace(EAST, NORTH);
            t1.expectChangeDamage(0, 0);
            t1.setShots(
                    0, 1,
                    0, 1,
                    0, 1,
                    0, 1);
            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(5,35, 5,35);
            t2.expectChangeFace(WEST, WEST);
            t2.expectChangeDamage(0, 0);

            // south safe
            TestShip t3 = new TestShip(SLOOP, Team.DEFENDER);
            t3.setMoves(F,N,N,N);
            t3.expectChangePosition(4,0, 4,0);
            t3.expectChangeFace(EAST, EAST);
            t3.expectChangeDamage(0, 0);
            TestShip t4 = new TestShip(SLOOP, Team.DEFENDER);
            t4.setMoves(F,R,N,N);
            t4.expectChangePosition(5,0, 5,0);
            t4.expectChangeFace(WEST, NORTH);
            t4.expectChangeDamage(0, 0);
            t4.setShots(
                    1, 0,
                    1, 0,
                    1, 0,
                    1, 0);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            s.add(t3);
            s.add(t4);
            listOfScenarios.add(new TestScenario("two sloops, both bump, safe zones, no damage", s));
        }

        // two ships equal size, both bump
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(4,3, 4,3);
            t1.expectChangeFace(EAST, EAST);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(5,3, 5,3);
            t2.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("two sloops, both bump", s));

            // TODO add damage
        }

        // two ships equal size, one bump
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(4,3, 4,3);
            t1.expectChangeFace(EAST, EAST);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(N,N,N,N);
            t2.expectChangePosition(5,3, 6,3);
            t2.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("two sloops, one bump", s));

            // TODO add damage
        }

        // two ships different size, one bump
        {
            TestShip t1 = new TestShip(WF, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(4,3, 5,3);
            t1.expectChangeFace(EAST, EAST);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(N,N,N,N);
            t2.expectChangePosition(5,3, 6,3);
            t2.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("sloop+wf, one bump", s));

            // TODO add damage
        }

        // two ships different size, both bump
        {
            TestShip t1 = new TestShip(WF, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(4,3, 4,3);
            t1.expectChangeFace(EAST, EAST);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(5,3, 5,3);
            t2.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("sloop+wf, both bump", s));

            // TODO add damage
        }

        // two ships same size bump in safe
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(4,0, 4,0);
            t1.expectChangeFace(EAST, EAST);
    
            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(5,0, 5,0);
            t2.expectChangeFace(WEST, WEST);
    
            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("two sloops bump in safe", s));

            // TODO add damage
        }

        // sidewall bump
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(0,3, 0,3);
            t1.expectChangeFace(WEST, WEST);
    
            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            listOfScenarios.add(new TestScenario("sidewall bump", s));

            // TODO add damage
        }
        
        // sloops side pass (no bump)
        {
            //    __
            //      |
            // x--> y
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(8,5, 9,5);
            t1.expectChangeFace(EAST, EAST);
            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(L,N,N,N);
            t2.expectChangePosition(9,5, 8,6);
            t2.expectChangeFace(NORTH, WEST);

            //       ^
            //       |
            // x --> y
            TestShip t3 = new TestShip(SLOOP, Team.DEFENDER);
            t3.setMoves(F,N,N,N);
            t3.expectChangePosition(4,5, 5,5);
            t3.expectChangeFace(EAST, EAST);
            TestShip t4 = new TestShip(SLOOP, Team.DEFENDER);
            t4.setMoves(F,N,N,N);
            t4.expectChangePosition(5,5, 5,6);
            t4.expectChangeFace(NORTH, NORTH);

            //       __
            //      |
            // x--> y
            TestShip t5 = new TestShip(SLOOP, Team.DEFENDER);
            t5.setMoves(F,N,N,N);
            t5.expectChangePosition(12,5, 13,5);
            t5.expectChangeFace(EAST, EAST);
            TestShip t6 = new TestShip(SLOOP, Team.DEFENDER);
            t6.setMoves(R,N,N,N);
            t6.expectChangePosition(13,5, 14,6);
            t6.expectChangeFace(NORTH, EAST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            s.add(t3);
            s.add(t4);
            s.add(t5);
            s.add(t6);
            listOfScenarios.add(new TestScenario("sloops side pass", s));

            // TODO add damage
        }

        // wind, whirlpool movement
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,L,L,R);
            t1.expectChangePosition(11,8, 9,11);
            t1.expectChangeFace(NORTH, SOUTH);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(N,N,N,N);
            t2.expectChangePosition(17,3, 17,7);
            t2.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("wind/whirlpool movement", s));
        }

        // three ships bump, variations
        {
            // x -->   <-- z
            //       ^
            //       y
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(13,4, 13,4);
            t1.expectChangeFace(NORTH, NORTH);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(12,5, 12,5);
            t2.expectChangeFace(EAST, EAST);

            TestShip t3 = new TestShip(SLOOP, Team.DEFENDER);
            t3.setMoves(F,N,N,N);
            t3.expectChangePosition(14,5, 14,5);
            t3.expectChangeFace(WEST, WEST);

            // x --> y <-- z
            //       |
            //       v
            TestShip t4 = new TestShip(SLOOP, Team.DEFENDER);
            t4.setMoves(F,N,N,N);
            t4.expectChangePosition(10,5, 10,5);
            t4.expectChangeFace(WEST, WEST);

            TestShip t5 = new TestShip(SLOOP, Team.DEFENDER);
            t5.setMoves(F,N,N,N);
            t5.expectChangePosition(9,5, 9,4);
            t5.expectChangeFace(SOUTH, SOUTH);

            TestShip t6 = new TestShip(SLOOP, Team.DEFENDER);
            t6.setMoves(F,N,N,N);
            t6.expectChangePosition(8,5, 8,5);
            t6.expectChangeFace(EAST, EAST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            s.add(t3);
            s.add(t4);
            s.add(t5);
            s.add(t6);
            listOfScenarios.add(new TestScenario("3 ship bump, variations", s));
        }

        // three ships bump, one big ship
        // x is WF, claims the space
        {
            // x -->   <-- z
            //       ^
            //       y
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(13,4, 13,4);
            t1.expectChangeFace(NORTH, NORTH);

            TestShip t2 = new TestShip(WF, Team.DEFENDER);
            t2.setMoves(F,N,N,N);
            t2.expectChangePosition(12,5, 13,5);
            t2.expectChangeFace(EAST, EAST);

            TestShip t3 = new TestShip(SLOOP, Team.DEFENDER);
            t3.setMoves(F,N,N,N);
            t3.expectChangePosition(14,5, 14,5);
            t3.expectChangeFace(WEST, WEST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            s.add(t3);
            listOfScenarios.add(new TestScenario("3 ship bump, variations, one big ship", s));

            // TODO test damage
        }

        // wind and rock and ship collisions (repeated for big ship, little ship)
        //
        //      start  desc start       end
        //      1      t1 facing   v
        //      2      t2 facing   v    1       t1 facing v
        //      w      wind facing v    2       t2 facing v
        //      r      rock             r       rock
        {
            TestShip t1 = new TestShip(WF, Team.DEFENDER);
            t1.setMoves(N,N,F,F);
            t1.expectChangePosition(6,31, 6,30);
            t1.expectChangeFace(SOUTH, SOUTH);

            TestShip t2 = new TestShip(WF, Team.DEFENDER);
            t2.setMoves(N,F,N,N);
            t2.expectChangePosition(6,30, 6,29);
            t2.expectChangeFace(SOUTH, SOUTH);

            TestShip t3 = new TestShip(SLOOP, Team.DEFENDER);
            t3.setMoves(N,N,F,F);
            t3.expectChangePosition(7,31, 7,30);
            t3.expectChangeFace(SOUTH, SOUTH);

            TestShip t4 = new TestShip(SLOOP, Team.DEFENDER);
            t4.setMoves(N,F,N,N);
            t4.expectChangePosition(7,30, 7,29);
            t4.expectChangeFace(SOUTH, SOUTH);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            s.add(t3);
            s.add(t4);
            listOfScenarios.add(new TestScenario("wind and rock and ship collisions", s));

            // TODO test damage
        }

        // whirlpool glitch #26(bug 3)
        {
            TestShip t1 = new TestShip(SLOOP, Team.DEFENDER);
            t1.setMoves(N,N,N,F);
            t1.expectChangePosition(18,12, 18,11);
            t1.expectChangeFace(SOUTH, SOUTH);

            TestShip t2 = new TestShip(SLOOP, Team.DEFENDER);
            t2.setMoves(N,N,N,F);
            t2.expectChangePosition(18,8, 18,9);
            t2.expectChangeFace(NORTH, EAST);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("wind and whirl collision #26 (3)", s));

            // TODO test damage
        }

        // add whirlpool glitch #26(bug 4)
        // current sim behaviour is that ships glide over wrecks of other ships as soon as they sink.
        // this is non sinking cade behaviour:
        //     sinking blockades: sinking is like a rock
        //     non sinking:       can move onto it
        {
            TestShip t1 = new TestShip(CUTTER, Team.DEFENDER);
            t1.setMoves(F,N,N,N);
            t1.expectChangePosition(16,8, 15,6);
            t1.expectChangeFace(SOUTH, WEST);
            t1.setInitialDamage(7); // max is 8
            t1.expectSunkInTurn(0);

            TestShip t2 = new TestShip(WF, Team.ATTACKER);
            t2.setMoves(N,R,N,N);
            t2.setShots(
                    0, 2,
                    0, 0,
                    0, 0,
                    0, 0);
            t2.expectChangePosition(14,6, 16,6);
            t2.expectChangeFace(NORTH, SOUTH);

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("wind and whirl collision & sink #26 (4)", s));
        }

        // two ships enter a whirlpool, one sinks (alternative version)
        {
            TestShip t1 = new TestShip(CUTTER, Team.DEFENDER);
            t1.setMoves(R,N,N,N);
            t1.expectChangePosition(15,8, 15,6);
            t1.expectChangeFace(EAST, WEST);
            t1.setInitialDamage(7); // max is 8
            t1.expectSunkInTurn(0);

            TestShip t2 = new TestShip(WF, Team.ATTACKER);
            t2.setMoves(N,L,N,N);
            t2.setShots(
                    2, 0,
                    0, 0,
                    0, 0,
                    0, 0);
            t2.expectChangePosition(15,5, 16,6);
            t2.expectChangeFace(EAST, NORTH); //if ships are on north/south of whirlpool

            List<TestShip> s = new ArrayList<>();
            s.add(t1);
            s.add(t2);
            listOfScenarios.add(new TestScenario("wind and whirl collision & sink #26 (4.1)", s));
        }
    }
}
