package com.benberi.cadesim.server.model.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.benberi.cadesim.server.ServerContext;


/**
 * Provide a way to capture player stats across rounds to display
 * at the end of the session.
 *
 */
public class Stats implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * represent time series data
     * each series represents e.g. player1 points, player1 sinks
     *
     */
    public class Series {
        private HashMap<Integer, Integer> dataPoints = new HashMap<Integer, Integer>();
        private String name;
        private List<String> tags;
        
        // maintain mins and maxes 
        private int min_key   = Integer.MAX_VALUE;
        private int min_value = Integer.MAX_VALUE;
        private int max_key   = Integer.MIN_VALUE;
        private int max_value = Integer.MIN_VALUE;

        public int getMin_key() {
            return min_key;
        }

        public int getMin_value() {
            return min_value;
        }

        public int getMax_key() {
            return max_key;
        }

        public int getMax_value() {
            return max_value;
        }
        
        /**
         * helper method to update mins and maxes
         */
        private void updateLimits(int key, int value)
        {
            // update key min/max
            if (key < min_key)
            {
                min_key = key;
            }
            if (key > max_key)
            {
                max_key = key;
            }
            
            // update value min/max
            if (value < min_value)
            {
                min_value = value;
            }
            if (value > max_value)
            {
                max_value = value;
            }
        }

        public Series(String name, List<String> tags) {
            this.name = name;
            this.tags = tags;
        }

        /**
         * add a point to the series
         * @param key    integer time point e.g. 0 (sec), 30 (sec)
         * @param value  integer value      e.g. 5 (sinks)
         * @param update whether to overwrite (false) or sum to existing (true)
         */
        public void addDataPoint(int key, int value, boolean update) {
            if (update)
            {
                // update
                if (!dataPoints.containsKey(key))
                {
                    // if dont have data point, initialise it
                    dataPoints.put(key, value);
                    updateLimits(key, value);
                }
                else
                {
                    // update by summation
                    dataPoints.put(key, dataPoints.get(key) + value);
                    updateLimits(key, dataPoints.get(key));
                }
            }
            else
            {
                // overwrite
                dataPoints.put(key, value);
                updateLimits(key, value);
            }
        }

        /**
         * replace all datapoints in the series
         */
        public void replaceDataPoints(HashMap<Integer, Integer> dataPoints)
        {
            this.dataPoints = dataPoints;
        }

        /**
         * get all data points in this series
         */
        public HashMap<Integer, Integer> getDataPoints() {
            return dataPoints;
        }
        
        public String getName() {
            return name;
        }
        
        public List<String> getTags() {
            return tags;
        }
    };
    
    // the list of series
    private List<Series> listOfSeries = new ArrayList<Series>();

    // prefixes for series and tags
    public static final String TEAM_TAG_PREFIX   = "team_";
    public static final String SINK_TAG          = "sink";
    public static final String SUNK_TAG          = "sunk";
    public static final String POINTS_TAG        = "points";
    public static final String CANNONS_TAG       = "cannons";
    public static final String MOVES_TAG         = "moves";
    public static final String DAMAGE_TAG        = "damage";
    public static final String BILGE_TAG         = "bilge";

    public static final String SINK_SERIES_PREFIX   = "sink_";
    public static final String SUNK_SERIES_PREFIX   = "sunk_";
    public static final String POINTS_SERIES_PREFIX = "points_";
    public static final String CANNONS_SERIES_PREFIX= "cannons_";
    public static final String MOVES_SERIES_PREFIX  = "moves_";
    public static final String DAMAGE_SERIES_PREFIX = "damage_";
    public static final String BILGE_SERIES_PREFIX  = "bilge_";

    /**
     * Start tracking another player.
     * @param playername name of player
     * @param teamname   name of player's team
     */
    public void addPlayer(String playername, String teamname) {
        // setup a tags obj
        List<String> tags = new ArrayList<String>();
        
        // tags should be used to aggregate multiple graphs on the same plot.
        // the TEAM_TAG is special - this means you can optionally add the
        // values and display a special TEAM score.

        // when a player sinks another player
        tags.clear();
        tags.add(SINK_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(SINK_SERIES_PREFIX + playername, tags));

        // when a player is sunk
        tags.clear();
        tags.add(SUNK_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(SUNK_SERIES_PREFIX + playername, tags));

        // when a player wins points
        tags.clear();
        tags.add(POINTS_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(POINTS_SERIES_PREFIX + playername, tags));
        
        // when a player shoots some cannons
        tags.clear();
        tags.add(CANNONS_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(CANNONS_SERIES_PREFIX + playername, tags));
        
        // when a player uses some moves
        tags.clear();
        tags.add(MOVES_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(MOVES_SERIES_PREFIX + playername, tags));
        
        // player damage
        tags.clear();
        tags.add(DAMAGE_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(DAMAGE_SERIES_PREFIX + playername, tags));
        
        // player bilge
        tags.clear();
        tags.add(BILGE_TAG);
        tags.add(TEAM_TAG_PREFIX + teamname);
        listOfSeries.add(new Series(BILGE_SERIES_PREFIX + playername, tags));
    }

    /**
     * get all Series
     * @return all series
     */
	public List<Series> getListOfSeries() {
	    return listOfSeries;
	}

	/**
	 * Find a series by playername and type, e.g. sink_player1
	 * @param seriesPrefix the prefix e.g. SINK_SERIES_PREFIX
	 * @param playername   the playername e.g. player1
	 * @return             the matching series or null if not found
	 */
	public Series findSeriesByName(String seriesPrefix, String playername) {
	    for (Series s : listOfSeries)
	    {
	        if (s.getName().equals(seriesPrefix + playername))
	        {
	            return s;
	        }
	    }
	    return null; // not found
	}

	/**
	 * Get a list of series by tag
	 */
	public List<Series> findSeriesCollectionByTag(String tag) {
        ArrayList<Series> ret = new ArrayList<Series>();
        for (Series s : listOfSeries)
        {
            for (String t : s.getTags())
            {
                if (t.equals(tag))
                {
                    ret.add(s);
                    break; // move to next series
                }
            }
        }

        // always return a list
        return ret;
    }

	/**
	 * Get a list of series matching a special 'team' tag
	 * e.g. get the combined sink count for the team
	 */
	public List<Series> findSeriesCollectionByTeam(String team) {
        return findSeriesCollectionByTag(TEAM_TAG_PREFIX + team);
    }
	
	/**
	 * Combine all team series of one type into a single series and return it
	 * e.g. track combined sink counts across team1
	 * @param seriesPrefix the prefix to search e.g. SINK_SERIES_PREFIX
	 * @param team         the team to find
	 * @return a series or null if not found
	 */
	public Series getCombinedSeriesForTeam(String seriesPrefix, String team) {
	    List<Series> sl = findSeriesCollectionByTeam(team);
	    Series ret = new Series(team, new ArrayList<String>());
	    HashMap<Integer, Integer> h = new HashMap<>();
	    for (Series s : sl)
	    {
	        if (
	            s.getName().startsWith(seriesPrefix) &&
	            s.getTags().contains(TEAM_TAG_PREFIX + team)
	        )
	        {
	            // combine the series' data points with our own series
	            HashMap<Integer, Integer> d = s.getDataPoints();
	            for (Integer i : d.keySet())
	            {
	                if (!h.containsKey(i))
	                {
	                    // if dont have data point, initialise it
	                    h.put(i, d.get(i));
	                }
	                else
	                {
	                    h.put(i, d.get(i) + h.get(i)); // update
	                }
	            }
	        }
	    }
	    
	    // now assign h to ret
	    ret.replaceDataPoints(h);
	    
	    // and return it
	    // if list [], then h will be {}. this is fine
	    return ret;
	}

	/**
	 * Add a data point to a series
	 * @param seriesPrefix the prefix e.g. SINK_SERIES_PREFIX
	 * @param playerName   the playername e.g. player1
	 * @param timePoint    the time in seconds e.g. 30
	 * @param value        the value e.g. 5
	 * @param update       whether to overwrite (false) or just sum to existing (true)
	 */
	private void addData(String seriesPrefix, String playerName, int timePoint, int value, boolean update) {
	    Series s = findSeriesByName(seriesPrefix, playerName);
	    if (s != null)
	    {
	        s.addDataPoint(timePoint, value, update);
	    }
	    else
	    {
	        ServerContext.log(
	            "WARNING - tried to add data to a non existent series: " + seriesPrefix + playerName
	        );
	    }
	}

	/**
     * Add a data point to a series, ADDING to existing or INITIALISING if not found
     * @param seriesPrefix the prefix e.g. SINK_SERIES_PREFIX
     * @param playerName   the playername e.g. player1
     * @param timePoint    the time in seconds e.g. 30
     * @param value        the value e.g. 5
     */
	public void updateData(String seriesPrefix, String playerName, int timePoint, int value)
	{
	    addData(seriesPrefix, playerName, timePoint, value, true);
	}

	public Stats() {
	}
}
