package com.benberi.cadesim.server.model.player.domain;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.model.player.Player;

public class MoveGenerator {

    /**
     * The player
     */
    private Player player;

    private double cannonGenerationPercentage;

    private double moveGenerationFraction;

    public MoveGenerator(Player p) {
        this.player = p;
    }

    public void update() {
        if (player.getMoveTokens().getCannons() + player.getMoves().countAllShoots() < player.getVessel().getMaxCannons()) {
            updateCannonGeneration();
        }
        updateMoveGeneration();
    }

    private void updateMoveGeneration() {
        // BILGE_SAILOR_PENALTY is applied to all moves generated.
        // its effect is scaled linearly according to the fraction of bilge present.
        double movesPerTick = player.getJobbersQuality().getMovesPerTick();
        double rate = movesPerTick - (1.0 - Constants.BILGE_SAILOR_PENALTY) * player.getVessel().getBilgeFraction() * movesPerTick;
        moveGenerationFraction += rate;
        if (moveGenerationFraction >= 1) {
            moveGenerationFraction -= Math.floor(moveGenerationFraction);
            player.getMoveTokens().moveGenerated();
            player.getPackets().sendTokens();
        }
    }

    private void updateCannonGeneration() {
        double rate = player.getJobbersQuality().getCannonsPerTick();
        double threshold = 100.0 / (double)player.getVessel().getMaxCannons();
        cannonGenerationPercentage += rate;
        if (cannonGenerationPercentage >= threshold) {
            cannonGenerationPercentage -= Math.floor(cannonGenerationPercentage);
            player.getMoveTokens().addCannons(1);
            player.getPackets().sendTokens();
        }
    }
}
