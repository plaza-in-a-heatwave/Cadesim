package com.benberi.cadesim.server.model.player.move;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.player.move.token.MoveToken;
import com.benberi.cadesim.server.model.player.move.token.MoveTokenList;
import com.benberi.cadesim.server.model.player.Player;

import java.util.*;

public class MoveTokensHandler {

    private Player player;

    /**
     * Left move tokens
     */
    private int left = 100;

    /**
     * Forward move tokens
     */
    private int forward = 100;

    /**
     * Right move tokens
     */
    private int right = 100;

    /**
     * List of moves
     */
    private MoveTokenList leftTokens = new MoveTokenList(MoveType.LEFT);
    private MoveTokenList rightTokens = new MoveTokenList(MoveType.RIGHT);
    private MoveTokenList forwardTokens = new MoveTokenList(MoveType.FORWARD);

    private MoveTokenList tempLeftTokens = new MoveTokenList(MoveType.LEFT);
    private MoveTokenList tempRightTokens = new MoveTokenList(MoveType.RIGHT);
    private MoveTokenList tempForwardTokens = new MoveTokenList(MoveType.FORWARD);

    /**
     * Cannons
     */
    private int cannons = 10;

    /**
     * The target token to generate
     */
    private MoveType targetTokenGeneration = MoveType.FORWARD;

    /**
     * If the token generation is automatic or not
     */
    private boolean automatic = true;



    public MoveTokensHandler(Player player) {
        this.player = player;

        for (int i = 0; i < 4; i++) {
            forwardTokens.add(new MoveToken(MoveType.FORWARD));
            if (i % 2 == 0) {
                rightTokens.add(new MoveToken(MoveType.RIGHT));
                leftTokens.add(new MoveToken(MoveType.LEFT));
            }
        }
    }

    public int countLeftMoves() {
        return leftTokens.size();
    }

    public int countRightMoves() {
        return rightTokens.size();
    }

    public int countForwardMoves() {
        return  forwardTokens.size();
    }

    /**
     * Adds a left move token
     * @param toAdd The amount of tokens to add
     */
    public void addLeft(int toAdd) {
        left += toAdd;
    }

    /**
     * Adds a forward move token
     * @param toAdd The amount of tokens to add
     */
    public void addForward(int toAdd) {
        forward += toAdd;
    }

    /**
     * Adds a right move token
     * @param toAdd The amount of tokens to add
     */
    public void addRight(int toAdd) {
        right += toAdd;
    }

    /**
     * Toggles the automatic move selection
     *
     * @param flag  If to automate or not
     */
    public void setAutomaticSealGeneration(boolean flag) {
        this.automatic = flag;
        if (flag) {
            updateAutoTargetSeal();
        }
    }

    /**
     * Checks if the generation target move is automatically selected
     *
     * @return {@link #automatic}
     */
    public boolean isAutomaticSealGeneration() {
        return this.automatic;
    }

    public void setTargetTokenGeneration(MoveType targetTokenGeneration, boolean notifyClient) {
        this.targetTokenGeneration = targetTokenGeneration;
        if (notifyClient) {
            player.getPackets().sendTargetSealPosition();
        }
    }

    /**
     * Removes a right move token
     * @param toRemove  The amount to remove
     */
    public void removeRight(int toRemove) {
        right -= toRemove;
        if (right < 0) {
            right = 0;
        }
    }

    /**
     * Removes a left move token
     * @param toRemove  The amount to remove
     */
    public void removeLeft(int toRemove) {
        left -= toRemove;
        if (left < 0) {
            left = 0;
        }
    }

    /**
     * Removes a forward move token
     * @param toRemove  The amount to remove
     */
    public void removeForward(int toRemove) {
       forward -= toRemove;
        if (forward < 0) {
            forward = 0;
        }
    }

    /**
     * Adds a cannon token
     * @param toAdd The amount to add
     */
    public void addCannons(int toAdd) {
        cannons += toAdd;
        if (cannons > player.getVessel().getMaxCannons()) {
            cannons = player.getVessel().getMaxCannons();
        }
    }

    public void removeCannons(int toRemove) {
        cannons -= toRemove;
        if (cannons < 0) {
            cannons = 0;
        }
    }

    public int getLeft() {
        return left;
    }

    public int getForward() {
        return forward;
    }

    public int getRight() {
        return right;
    }

    public int getCannons() {
        return cannons;
    }

    public boolean useTokenForMove(MoveType moveType) {
        if (moveType == MoveType.NONE) {
            return true;
        }
        switch (moveType) {
            case LEFT:
                if (countLeftMoves() > 0) {
                    tempLeftTokens.add(leftTokens.pollFirst());
                    return  true;
                }
                break;
            case RIGHT:
                if (countRightMoves() > 0) {
                    tempRightTokens.add(rightTokens.pollFirst());
                    return  true;
                }
                break;
            case FORWARD:
                if (countForwardMoves() > 0) {
                    tempForwardTokens.add(forwardTokens.pollFirst());
                    return  true;
                }
                break;
            case NONE:
            	ServerContext.log("warning - tried to useTokenForMove where movetype was NONE");
            	break;
        }
        return  false;
    }

    public void addToken(MoveType move, int amount) {
        switch (move) {
            case LEFT:
                left += amount;
                break;
            case RIGHT:
                right += amount;
                break;
            case FORWARD:
                forward += amount;
                break;
			case NONE:
				ServerContext.log("warning - tried to add " + Integer.toString(amount) + " tokens to movetype NONE");
				break;
        }
    }

    public void returnMove(MoveType currentMove) {
        switch (currentMove) {
            case LEFT:
                if (!tempLeftTokens.isEmpty())
                    leftTokens.add(tempLeftTokens.pollFirst());
                break;
            case RIGHT:
                if (!tempRightTokens.isEmpty())
                    rightTokens.add(tempRightTokens.pollFirst());
                break;
            case FORWARD:
                if (!tempForwardTokens.isEmpty())
                    forwardTokens.add(tempForwardTokens.pollFirst());
                break;
            case NONE:
				ServerContext.log("warning - tried to retrieve movetype NONE");
				break;
        }
    }

    public void clearTemp() {
        tempForwardTokens.clear();
        tempLeftTokens.clear();
        tempRightTokens.clear();
    }

    public void tickExpiration() {
        processExpirationIterator(leftTokens.iterator());
        processExpirationIterator(forwardTokens.iterator());
        processExpirationIterator(rightTokens.iterator());
    }

    private void processExpirationIterator(Iterator<MoveToken> itr) {
        while(itr.hasNext()) {
            MoveToken token = itr.next();
            token.tickTurn();
            if (token.getTurns() > Constants.TOKEN_LIFE) {
                itr.remove();
            }
        }
    }

    public void moveGenerated() {
        switch (targetTokenGeneration) {
            case RIGHT:
                rightTokens.add(new MoveToken(targetTokenGeneration));
                break;
            case LEFT:
                leftTokens.add(new MoveToken(targetTokenGeneration));
                break;
            case FORWARD:
                forwardTokens.add(new MoveToken(targetTokenGeneration));
                break;
            case NONE:
				ServerContext.log("warning - generated a token with movetype NONE");
				break;
    }

        if (automatic) {
            updateAutoTargetSeal();
        }
    }

    public void updateAutoTargetSeal() {
        int left = countLeftMoves();
        int right = countRightMoves();
        int forward = countForwardMoves();

        if (left == right && left == forward || left + right + forward == 0) {
            switch (targetTokenGeneration) {
                case FORWARD:
                    setTargetTokenGeneration(MoveType.LEFT, true);
                    break;
                case LEFT:
                    setTargetTokenGeneration(MoveType.RIGHT, true);
                    break;
                case RIGHT:
                    setTargetTokenGeneration(MoveType.FORWARD, true);
                    break;
                case NONE:
    				ServerContext.log("warning - requesting token generation for movetype NONE");
    				break;
            }
        }
        else {
            List<MoveTokenList> list = new ArrayList<>();
            list.add(leftTokens);
            list.add(rightTokens);
            list.add(forwardTokens);
            list.sort(Comparator.comparingInt(TreeSet::size));

            try {
                this.setTargetTokenGeneration(list.get(0).getType(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public MoveType getTargetSeal() {
        return targetTokenGeneration;
    }
}
