package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.player.Player;

public class MoveGenerator {

    /**
     * The player
     */
    private Player player;

    private double cannonGenerationPercentage;

    private double moveGenerationPercentage;

    public MoveGenerator(Player p) {
        this.player = p;
    }

    public void update() {
        if (player.getMoveTokens().getCannons() + player.getMoves().countAllShoots() < 24) {
            updateCannonGeneration();
        }
        updateMoveGeneration();
    }

    private void updateMoveGeneration() {
        double movesPerTurn = player.getJobbersQuality().getMovesPerTurn();
        double movesPerTurnAffectBilge = movesPerTurn - 0.009 * player.getVessel().getBilgePercentage() * movesPerTurn;
        double rate = movesPerTurnAffectBilge / ServerConfiguration.getTurnDuration();

        moveGenerationPercentage += rate;
        if (moveGenerationPercentage >= 1) {
            moveGenerationPercentage -= Math.floor(moveGenerationPercentage);
            player.getMoveTokens().moveGenerated();
            player.getPackets().sendTokens();
        }
    }

    private void updateCannonGeneration() {
        double rate = player.getJobbersQuality().getCannonsPerTurn() / ServerConfiguration.getTurnDuration();
        cannonGenerationPercentage += rate;
        if (cannonGenerationPercentage >= 1) {
            cannonGenerationPercentage -= Math.floor(cannonGenerationPercentage);
            player.getMoveTokens().addCannons(1);
            player.getPackets().sendTokens();
        }
    }
}
