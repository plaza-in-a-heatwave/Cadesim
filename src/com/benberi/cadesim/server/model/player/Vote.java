package com.benberi.cadesim.server.model.player;

import java.util.ArrayList;
import java.util.List;

public class Vote {
	private int votesFor     = 0;              // count votes for
	private int votesAgainst = 0;              // count votes against
	private static final int MIN_TURNOUT_PERCENT = 75; // turnout must be >= 75%
	private static final int CARRY_PERCENT = 50; // vote carried at >50%
	private static final int VOTE_TIMEOUT_MILLIS = 30000; // timeout after 30s
	private List<String> eligibleIPs = new ArrayList<String>(); // restrict to players present when vote started
	private List<String> voterIPs    = new ArrayList<String>(); // prevent multi ip voting
	private long voteStartTime; // system time in millis() since vote started
	private boolean voteInProgress = true; // is vote in progress? true initially
	private String description;            // describe what the vote relates to
	
	/**
	 * the kind of vote result which could be returned
	 */
	public static enum VOTE_RESULT {
	    TBD,
	    FOR,
	    AGAINST,
	    TIMEDOUT
	}
	
	/**
	 * get the internal vote threshold percentage
	 * @return threshold as a percentage
	 */
	public float getVoteThreshold() {
		return (float)MIN_TURNOUT_PERCENT;
	}
	
	/**
	 * create a new vote
	 * @param totalPlayers the number of players currently active
	 */
	public Vote(PlayerManager pm, String description)
	{
		this.voteStartTime = System.currentTimeMillis();
		
		for (Player p:pm.getPlayers())
		{
			eligibleIPs.add(p.getChannel().remoteAddress().toString());
		}
		
		this.description = description;
	}
	
	/**
	 * @return whether vote is in progress (true) or not (false)
	 */
	public boolean isVoteInProgress() {
		return voteInProgress;
	}
	
	/**
	 * get the description.
	 * @return string representation of the description configured with
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * a player casts a vote.
	 * @param pl      the player
	 * @param voteFor true->for, false->against
	 * @return        the vote result after the vote is cast
	 */
	public VOTE_RESULT castVote(Player pl, boolean voteFor)
	{
		// timeout after n seconds
		if ((voteStartTime + System.currentTimeMillis()) > VOTE_TIMEOUT_MILLIS)
		{
			voteInProgress = false;
			return VOTE_RESULT.TIMEDOUT;
		}

		// restrict based on IP - can only vote if were around when vote was cast
		if (!eligibleIPs.contains(pl.getChannel().remoteAddress().toString()))
		{
			// TODO warn [to player] this IP can't vote as it wasn't in the original list
			return VOTE_RESULT.TBD;
		}
		
		// prevent duplicate IPs
		if (voterIPs.contains(pl.getChannel().remoteAddress().toString()))
		{
			// TODO warn [to player] this IP can't vote as it's a duplicate
			return VOTE_RESULT.TBD;
		}
		
		if (voteFor)
		{
			votesFor++;
			voterIPs.add(pl.getChannel().remoteAddress().toString());
		}
		else
		{
			votesAgainst++;
			voterIPs.add(pl.getChannel().remoteAddress().toString());
		}
		
		// MIN_TURNOUT% of the players must have voted
		if ((((float)(votesFor + votesAgainst) / (float)eligibleIPs.size()) * 100.0) >= MIN_TURNOUT_PERCENT)
		{
			// simple majority of > CARRY_VOTE_BEYOND% carries the vote
			if ((((float)votesFor / (float)eligibleIPs.size()) * 100.0) > CARRY_PERCENT)
			{
				voteInProgress = false;
				return VOTE_RESULT.FOR;
			}
			else
			{
				voteInProgress = false;
				return VOTE_RESULT.AGAINST;
			}
		}
		else
		{
			// if not timed out, and not resolved, must be TBD
			return VOTE_RESULT.TBD;
		}
	}
}
