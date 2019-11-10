package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.CadeServer;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;

import com.benberi.cadesim.server.util.RandomUtils;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Obsidio game server bootstrap
 *
 * @author Ben Beri <benberi545@gmail.com>
 *                  <https://github.com/benberi>
 */
public class GameServerBootstrap {

    /**
     * The service executor
     */
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2); // 2 threads, 1 for netty, 1 for game logic

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The start time of the server
     */
    private long start;
    public GameServerBootstrap() {
        context = new ServerContext();
    }

    /**
     * Start the server
     * @throws InterruptedException
     */
    private void startServer() throws InterruptedException {
        start = System.currentTimeMillis();

        ServerContext.log("Using config: " + ServerConfiguration.getConfig());
        ServerContext.log("Starting up the host server....");
        CadeServer server = new CadeServer(context, this); // to notify back its done
        executorService.execute(server);
    }

    /**
     * Start the services
     */
    public void startServices() {

        ServerContext.log("Starting up the game service....");
        GameService service = new GameService(context);
        executorService.scheduleAtFixedRate(service, 0, Constants.SERVICE_LOOP_DELAY, TimeUnit.MILLISECONDS);

        long time = System.currentTimeMillis() - start;

        ServerContext.log("Game Server loaded successfully in " + (int)time + " ms.");
    }
    
    private static void help(Options options) {
        // This prints out some help for the cli
    	// And then exits
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Obsidio-Server", options);
        System.exit(Constants.EXIT_ERROR_BAD_CONFIG);
    }

    /**
     * Main method
     * @param args The arguments  for the simulator server
     * @throws InterruptedException 
     * @throws NumberFormatException 
     */
    public static void main(String[] args) throws NumberFormatException, InterruptedException{
        Options options = new Options();
        
        ServerContext.log("Welcome to " + Constants.name + " (version " + Constants.VERSION + ")" + ".");

        options.addOption("a", "max-players", true, "Set max players allowed (default: " + ServerConfiguration.getPlayerLimit() + ")");
        options.addOption("b", "disengage-behavior", true, "disengage button behavior (\"off\", \"simple\", \"realistic\") (default: " + ServerConfiguration.getDisengageBehavior() + ")");
        options.addOption("c", "auth-code", true, "provide a text authcode to limit access. This is NOT a password, it WILL be written to logs etc. (default: \"" + ServerConfiguration.getAuthCode() + "\")");
        options.addOption("d", "respawn-delay", true, "respawn delay (in turns) after sinking (default: " + ServerConfiguration.getRespawnDelay() + ")");
        options.addOption("e", "token-expiry-turns", true, "set token expiry, or -1 for never. Do not set to 0. (default: " + ServerConfiguration.getTokenExpiry() + ")");
        options.addOption("f", "enable-breaks", true, "two tuple (duration sec, interval sec) e.g. 60,600 is 1 min break every 10 min. minimum break is 10, minimum interval is 60. (default: not enabled " + ServerConfiguration.getBreak() + ")");
        options.addOption("g", "permit-multiclient", true, "enable players to login with more than 1 client at a time, (on or off) (default: " + ServerConfiguration.getMultiClientMode() + ")");
        options.addOption("h", "help", false, "Show help");
        options.addOption("i", "enable-power-saving", true, "enable power saving, (on or off) (default: " + ServerConfiguration.getPowerSavingMode() + ")");
        options.addOption("m", "map", true, "Set map name or leave blank for random (default: " + ServerConfiguration.getMapName() + ")");
        options.addOption("n", "team-names", true, "names for the attacker and defender, comma separated, " + Constants.MAX_TEAMNAME_SIZE + " characters max (default: " + ServerConfiguration.getAttackerName() + "," + ServerConfiguration.getDefenderName() + ")");
        options.addOption("o", "map-rotation", true, "randomly rotate map every n turns, or -1 for never. Do not set to 0. (default: " + ServerConfiguration.getMapRotationPeriod() + ")");
        options.addOption("p", "port", true, "Local port to bind (default: " + ServerConfiguration.getPort() + ")");
        options.addOption("q", "jobbers-quality", true, "quality of jobbers (\"basic\", \"elite\") (default: " + ServerConfiguration.getJobbersQuality() + ")");
        options.addOption("r", "round-duration", true, "round duration seconds, minimum " + Constants.MIN_ROUND_DURATION + ", must be >= turn duration, (default: " + ServerConfiguration.getRoundDuration() / 10 + ")");
        options.addOption("s", "server-name", true, "provide a name for the server, " + Constants.MAX_SERVER_NAME_SIZE + " characters max (default: " + ServerConfiguration.getServerName() + ")");
        options.addOption("t", "turn-duration", true, "turn duration seconds, minimum " + Constants.MIN_TURN_DURATION + ", (default: " + ServerConfiguration.getTurnDuration() / 10 + ")");
        options.addOption("v", "voting-majority", true, "voting majority percent (0 to 100 inclusive), or -1 to disable (default: " + ServerConfiguration.getVotingMajority() + ")");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            // check independent options
            if (cmd.hasOption("h")) {
                help(options);
            }
            if (cmd.hasOption("a")) {
            	ServerConfiguration.setPlayerLimit(Integer.parseInt(cmd.getOptionValue("a")));
            }
            if (cmd.hasOption("p")) {
            	ServerConfiguration.setPort(Integer.parseInt(cmd.getOptionValue("p")));
            }
            if (cmd.hasOption("t"))
            {
            	int turnDuration = Integer.parseInt(cmd.getOptionValue("t"));
            	if (turnDuration >= Constants.MIN_TURN_DURATION)
            	{
            		ServerConfiguration.setTurnDuration(10 * turnDuration);
            	}
            	else
            	{
            		help(options);
            	}
            }
            if (cmd.hasOption("r"))
            {
            	int roundDuration = Integer.parseInt(cmd.getOptionValue("r"));
            	if (roundDuration >= Constants.MIN_ROUND_DURATION)
            	{
            		ServerConfiguration.setRoundDuration(10 * roundDuration);
            	}
            	else
            	{
            		help(options);
            	}
            }
            if (cmd.hasOption("d"))
            {
            	ServerConfiguration.setRespawnDelay(Integer.parseInt(cmd.getOptionValue("d")));
            }
            if (cmd.hasOption("b"))
            {
            	ServerConfiguration.setDisengageBehavior(cmd.getOptionValue("b"));
            }
            if (cmd.hasOption("v"))
            {
            	int votingMajority = Integer.parseInt(cmd.getOptionValue("v"));
            	if (votingMajority >= -1 && votingMajority <= 100)
            	{
            		ServerConfiguration.setVotingMajority(votingMajority);
            	}
            	else
            	{
            		help(options);
            	}
            }
            if (cmd.hasOption("v"))
            {
            	ServerConfiguration.setVotingMajority(Integer.parseInt(cmd.getOptionValue("v")));
            }
            if (cmd.hasOption("q"))
            {
            	ServerConfiguration.setJobbersQuality(cmd.getOptionValue("q"));
            }
            if (cmd.hasOption("n"))
            {
            	try {
            		String[] names = cmd.getOptionValue("n").split(",");
            		if (
            			(names[0].length() > Constants.MAX_TEAMNAME_SIZE) ||
            			(names[1].length() > Constants.MAX_TEAMNAME_SIZE)
            		)
            		{
            			help(options);
            		}
            		ServerConfiguration.setAttackerName(names[0]);
            		ServerConfiguration.setDefenderName(names[1]);
            	}
            	catch (Exception e)
            	{
            		help(options);
            	}
            }
            if (cmd.hasOption("c"))
            {
            	String authCode = cmd.getOptionValue("c");
            	if (authCode.length() > Constants.MAX_CODE_SIZE)
            	{
            		help(options);
            	}
            	else
            	{
            		ServerConfiguration.setAuthCode(authCode);
            	}
            }
            if (cmd.hasOption("s"))
            {
            	String serverName = cmd.getOptionValue("s");
            	if (serverName.length() > Constants.MAX_SERVER_NAME_SIZE)
            	{
            		help(options);
            	}
            	else
            	{
            		ServerConfiguration.setServerName(serverName);
            	}
            }
            if (cmd.hasOption("e"))
            {
            	ServerConfiguration.setTokenExpiry((Integer.parseInt(cmd.getOptionValue("e"))));
            	if (ServerConfiguration.getTokenExpiry() == 0) {
            		help(options);
            	}
            }
            if (cmd.hasOption("f"))
            {
                try {
                    String[] b = cmd.getOptionValue("f").split(",");
                    int[] i = ServerConfiguration.getBreak();
                    i[0] = Integer.parseInt(b[0]);
                    i[1] = Integer.parseInt(b[1]);
                    System.out.println("i:" + i[0] + ":" + i[1]);
                    if (
                        (i[0] < Constants.MIN_BREAK_DURATION) ||
                        (i[1] < Constants.MIN_BREAK_INTERVAL)
                    )
                    {
                        help(options);
                    }
                    ServerConfiguration.setBreak(i[0], i[1]);
                    System.out.println("done it!");
                }
                catch (Exception e)
                {
                    help(options);
                }
            }
            if (cmd.hasOption("o"))
            {
            	ServerConfiguration.setMapRotationPeriod(Integer.parseInt(cmd.getOptionValue("o")));
            	if (ServerConfiguration.getMapRotationPeriod() == 0) {
            		help(options);
            	}
            }
            if (cmd.hasOption("i"))
            {
            	String v = cmd.getOptionValue("i").toLowerCase();
            	if (v.equals("on"))
            	{
            		ServerConfiguration.setPowerSavingMode(true);
            	}
            	else if (v.equals("off"))
            	{
            		ServerConfiguration.setPowerSavingMode(false);
            	}
            	else
            	{
            		help(options);
            	}
            }
            if (cmd.hasOption("g"))
            {
                String v = cmd.getOptionValue("g").toLowerCase();
                if (v.equals("on"))
                {
                    ServerConfiguration.setMultiClientMode(true);
                }
                else if (v.equals("off"))
                {
                    ServerConfiguration.setMultiClientMode(false);
                }
                else
                {
                    help(options);
                }
            }
            if (!cmd.hasOption("m")) { // Chooses random map if no map chosen
                try {
                	ServerConfiguration.setMapName(
                		RandomUtils.getRandomMapName(
                			Constants.mapDirectory
                		)
                	);
                    ServerContext.log("No map specified, automatically chose random map: " + ServerConfiguration.getMapName());
                } catch(NullPointerException e) {
                	ServerContext.log("Failed to find random map folder. create a folder called \"maps\" in the same directory.");
                    System.exit(Constants.EXIT_ERROR_CANT_FIND_MAPS);
                }
            }
            else {
            	ServerConfiguration.setMapName(cmd.getOptionValue("m"));
                ServerContext.log("Using user specified map:" + ServerConfiguration.getMapName());
            }
            
            // check co-dependent arguments e.g. turn/round time
            if (ServerConfiguration.getRoundDuration() < ServerConfiguration.getTurnDuration())
            {
            	help(options);
            }

            GameServerBootstrap bootstrap = new GameServerBootstrap();
            bootstrap.startServer();

        } catch (ParseException | NumberFormatException e) {
            ServerContext.log("Failed to parse comand line properties" + e.toString());
            help(options);
        }
    }
}
