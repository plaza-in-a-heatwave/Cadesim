package com.benberi.cadesim.server.config;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.benberi.cadesim.server.model.player.domain.JobbersQuality;
import com.benberi.cadesim.server.util.Utils;

public class ServerConfiguration {
	/**
	 * Store/retrieve/overwrite server gameplay defaults.
	 */

    /**
     * Server defaults. Overridden by CLI during initialisation.
     */
    private static int playerLimit = 5;
    private static int port = 4970;
    private static int turnDuration  = 300;        // "deciseconds"
    private static int roundDuration = 18000;      // "deciseconds"
    private static int respawnDelay  = 2;          // turns
    private static int mapRotationPeriod = 5;      // turns
    private static String mapName = "default.map";
    private static String mapFilter = ".txt";
	private static String disengageBehavior = "simple";
	private static int votingMajority = 75;        // percent
	private static JobbersQuality jobbersQuality = JobbersQuality.ELITE;
	private static String attackerName = "attacker";
	private static String defenderName = "defender";
	private static String authCode = ""; // by default no auth code
	private static String serverName = Constants.name;
	private static int tokenExpiry = 4;
	private static boolean runContinuousMode = true;
    private static boolean multiClientMode = true;
    private static int[] breakInfo = {-1, -1}; // seconds

    // uninitializable defaults
    private static String nextMapName = null; // the next map in the rotation. cannot be initialized by CLI.
    private static ArrayList<String> mapList; // store all possible maps, load from file once at the start. restart server to apply change.

    public static int getPlayerLimit() {
        return ServerConfiguration.playerLimit;
    }

    public static void setPlayerLimit(int playerLimit) {
        ServerConfiguration.playerLimit = playerLimit;
    }

    public static int getPort() {
        return ServerConfiguration.port;
    }

    public static void setPort(int port) {
        ServerConfiguration.port = port;
    }

    public static int getTurnDuration() {
    	return ServerConfiguration.turnDuration;
    }

    public static void setTurnDuration(int turnDurationInDeciseconds) {
    	ServerConfiguration.turnDuration = turnDurationInDeciseconds;
    }

    public static int getRoundDuration() {
		return ServerConfiguration.roundDuration;
	}

	public static void setRoundDuration(int roundDurationInDeciseconds) {
		ServerConfiguration.roundDuration = roundDurationInDeciseconds;
	}

    public static int getRespawnDelay() {
    	return ServerConfiguration.respawnDelay;
    }

    public static void setRespawnDelay(int respawnDelay) {
    	ServerConfiguration.respawnDelay = respawnDelay;
    }

    /**
	 * gets a printable config report.
	 * Note that the turnDuration/roundDuration are returned
	 * in **seconds** rather than their internal representation as
	 * deciseconds.
	 */
    public static String getConfig() {
        return "\n[\n" +
                "    Cadesim server version:" + Constants.VERSION + ",\n" +
                "    Player limit:" + getPlayerLimit() + ",\n" +
                "    Map Name:" + getMapName() + ",\n" +
                "    Port:" + getPort() + ",\n" +
                "    Turn duration:" + getTurnDuration() / 10 + "s,\n" +
                "    Round duration:" + getRoundDuration() / 10 + "s,\n" +
                "    Sink delay:" + getRespawnDelay() + " turns,\n" +
                "    Map rotation period:" + ((getMapRotationPeriod()== -1)?"[rotation off] -1":getMapRotationPeriod() + " turns") + ",\n" +
                "    Disengage behavior:" + getDisengageBehavior() + ",\n" +
                "    Vote majority percentage: " +
                (isVotingEnabled()?("[voting on] " + getVotingMajority() + "%"):"[voting off]") + ",\n" +
                "    Jobbers quality: " + getJobbersQualityAsString() + ",\n" +
                "    Team names: " + getAttackerName() + "," + getDefenderName() + ",\n" +
                "    Auth code: \"" + getAuthCode() + "\"" + ",\n" +
                "    Run continuous: " + getRunContinuousMode() + ",\n" +
                "    Multiclient permitted: " + getMultiClientMode() + ",\n" +
                "    Breaks duration/interval: " + getBreak()[0] + ":" + getBreak()[1] + ",\n" +
                "]";
    }

    public static String getMapName() {
        return ServerConfiguration.mapName;
    }
    
    public static String getMapFilter() {
        return ServerConfiguration.mapFilter;
    }

    public static void setMapName(String mapName) {
        ServerConfiguration.mapName = mapName;
    }

    // setter/getter and generator for the next map in rotation.
    public static String getNextMapName() {
        return ServerConfiguration.nextMapName;
    }
    public static void overrideNextMapName(String mapName) { // specify one
        ServerConfiguration.nextMapName = mapName;
    }
    public static void pregenerateNextMapName() {       // choose one at random
        ServerConfiguration.nextMapName = ServerConfiguration.getRandomMapName();
    }

	public static int getMapRotationPeriod() {
		return ServerConfiguration.mapRotationPeriod;
	}

	public static void setMapRotationPeriod(int mapRotationPeriod) {
		ServerConfiguration.mapRotationPeriod = mapRotationPeriod;
	}

	// grab available maps and store the list in ServerConfig. restart required to recognize a change.
	public static void loadAvailableMaps() {
        Path currentRelativePath = Paths.get("");
        FilenameFilter textFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if(lowercaseName.endsWith(getMapFilter())) {
					return true;
				}else {
					return false;
				}
			}
        	
        };
        File[] mapList = currentRelativePath.resolveSibling(Constants.mapDirectory).toFile().listFiles(textFilter);
        ArrayList<String> names = new ArrayList<>();
        for (File f : mapList) {
            names.add(f.getName());
        }
        ServerConfiguration.mapList = names;
	}
	public static ArrayList<String> getAvailableMaps() {
	    return ServerConfiguration.mapList;
	}
	public static String getRandomMapName() {
        return ServerConfiguration.mapList.get(Utils.randInt(0, mapList.size()-1));
    }

	public static String getDisengageBehavior() {
		return ServerConfiguration.disengageBehavior ;
	}

	public static void setDisengageBehavior(String disengageBehavior) {
		ServerConfiguration.disengageBehavior = disengageBehavior;
	}

	public static void setVotingMajority(int votingMajority)
	{
		ServerConfiguration.votingMajority = votingMajority;
	}

	public static int getVotingMajority() {
		return ServerConfiguration.votingMajority;
	}

	public static boolean isVotingEnabled() {
		return ServerConfiguration.votingMajority != -1;
	}

	/**
	 * set with string so can load from cli
	 */
	public static void setJobbersQuality(String value) throws java.lang.IllegalArgumentException
	{
		if (value.toLowerCase().equals("elite"))
		{
			ServerConfiguration.jobbersQuality = JobbersQuality.ELITE;
		}
		else if (value.toLowerCase().equals("basic"))
		{
			ServerConfiguration.jobbersQuality = JobbersQuality.BASIC;
		}
		else
		{
			throw new java.lang.IllegalArgumentException("jobbersQuality was unexpectedly \"" + value + "\"");
		}
		
	}

	public static JobbersQuality getJobbersQuality() {
		
		return ServerConfiguration.jobbersQuality;
	}

	public static String getJobbersQualityAsString() {
		return ServerConfiguration.jobbersQuality.equals(JobbersQuality.ELITE)?"elite":"basic";
	}

	public static void setDefenderName(String string) {
		ServerConfiguration.defenderName = string;
		
	}

	public static String getDefenderName() {
		return ServerConfiguration.defenderName;
	}

	public static void setAttackerName(String string) {
		ServerConfiguration.attackerName = string;
	}

	public static String getAttackerName() {
		return ServerConfiguration.attackerName;
	}

	public static String getAuthCode() {
		return ServerConfiguration.authCode;
	}

	public static void setAuthCode(String authCode) {
		ServerConfiguration.authCode = authCode;
	}

	public static String getServerName() {
		return ServerConfiguration.serverName;
	}

	public static void setServerName(String serverName) {
		ServerConfiguration.serverName = serverName;
	}

	public static int getTokenExpiry() {
		return ServerConfiguration.tokenExpiry ;
	}

	public static void setTokenExpiry(int value) {
		ServerConfiguration.tokenExpiry = value;
	}

	public static boolean getRunContinuousMode() {
		return ServerConfiguration.runContinuousMode;
	}

	public static void setRunContinuousMode(boolean value) {
		ServerConfiguration.runContinuousMode = value;
	}

    public static boolean getMultiClientMode() {
        return ServerConfiguration.multiClientMode ;
    }

    public static void setMultiClientMode(boolean value) {
        ServerConfiguration.multiClientMode = value;
    }

    public static int[] getBreak() {
        return ServerConfiguration.breakInfo;
    }

    public static void setBreak(int duration, int interval) {
        ServerConfiguration.breakInfo[0] = duration;
        ServerConfiguration.breakInfo[1] = interval;
    }
}
