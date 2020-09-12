package com.benberi.cadesim.game.scene.impl.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.SceneComponent;

public class GameInformation extends SceneComponent {

    /**
     * The sprite batch
     */
    private SpriteBatch batch;

    private GameContext context;
    
    private Texture panel;
    private Texture contenders;
    private TextureRegion defenderThem;
    private TextureRegion defenderUs;
    private TextureRegion attackerThem;
    private TextureRegion attackerUs;

    /**
     * Font for texts
     */
    private BitmapFont fontTeamAttacker;
    private BitmapFont fontTeamDefender;
    private BitmapFont fontPointsAttacker;
    private BitmapFont fontPointsDefender;
    private BitmapFont timeFont;
    private BitmapFont breakInfoFont;

    private int defenderPoints;
    private int attackerPoints;

    private int time;
    private int timeUntilBreak = -1; // defaults
    private int breakTime      = -1; // "
    
    // default strings - will be overwritten
    private String defender = "Attacker";
    private String attacker = "Defender";
    private String longestTeam = attacker;

    // are we defender or attacker?
    boolean areDefender;

    GameInformation(GameContext context, GameScene owner) {
        super(context, owner);
        this.context = context;
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.panel = context.getManager().get(context.getAssetObject().infoPanel);
        this.contenders = context.getManager().get(context.getAssetObject().contenders);
        this.defenderThem = new TextureRegion(contenders, 0, 0, 13, 18);
        this.defenderUs = new TextureRegion(contenders, 13, 0, 13, 18);
        this.attackerThem = new TextureRegion(contenders, 26, 0, 13, 18);
        this.attackerUs = new TextureRegion(contenders, 39, 0, 13, 18);

        fontTeamAttacker = context.getManager().get(context.getAssetObject().fontTeamAttacker);
        fontTeamDefender = context.getManager().get(context.getAssetObject().fontTeamDefender);
        fontPointsAttacker = context.getManager().get(context.getAssetObject().fontTeamAttacker_Points);
        fontPointsDefender = context.getManager().get(context.getAssetObject().fontTeamDefender_Points);
        timeFont = context.getManager().get(context.getAssetObject().fontTime);
        breakInfoFont = context.getManager().get(context.getAssetObject().fontBreak);
        areDefender = context.myTeam.name().equals(Team.DEFENDER.toString());

        if (areDefender) {
        	fontTeamDefender.setColor(new Color(100 / 255f, 182 / 255f, 232 / 255f, 1));
        	fontTeamAttacker.setColor(new Color(203 / 255f, 42 / 255f, 25 / 255f, 1));

        	fontPointsDefender.setColor(new Color(100 / 255f, 182 / 255f, 232 / 255f, 1));
        	fontPointsAttacker.setColor(new Color(203 / 255f, 42 / 255f, 25 / 255f, 1));
        }
        else {
        	fontTeamDefender.setColor(new Color(146 / 255f, 236 / 255f, 30 / 255f, 1));
        	fontTeamAttacker.setColor(new Color(100 / 255f, 182 / 255f, 232 / 255f, 1));

        	fontPointsDefender.setColor(new Color(146 / 255f, 236 / 255f, 30 / 255f, 1));
        	fontPointsAttacker.setColor(new Color(100 / 255f, 182 / 255f, 232 / 255f, 1));
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTimeUntilBreak(int value) {
        timeUntilBreak = value;
    }

    public void setBreakTime(int value) {
        breakTime = value;
    }
    
    public boolean getIsBreak() {
        return timeUntilBreak == 0 && breakTime >= 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        int xPlacement = 60 + (longestTeam.length() * 6);

        Gdx.gl.glViewport(0,200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(panel, 5, 5);

        // draw defender
        fontTeamDefender.draw(batch, defender + ":", 38,120 );
        fontPointsDefender.draw(batch, Integer.toString(defenderPoints), xPlacement,118 );
        batch.draw(areDefender?defenderUs:defenderThem, 18, 105);

        // draw attacker
        fontTeamAttacker.draw(batch, attacker + ":", 38,97 );
        fontPointsAttacker.draw(batch, Integer.toString(attackerPoints), xPlacement,95 );
        batch.draw(areDefender?attackerThem:attackerUs, 18, 82);

        if (timeUntilBreak == 0 && breakTime >= 0)
        {
            // draw break
            int minutes = breakTime / 60;
            int seconds = breakTime % 60;
            timeFont.draw(
                batch,
                (minutes < 10 ? "0" + minutes : minutes) +
                    ":" +
                    (seconds < 10 ? "0" + seconds : seconds),
                62,
                50
            );

            // draw current break info
            breakInfoFont.draw(
                batch,
                "Break",
                62,
                65
            );
        }
        else
        {
            // draw time
            int minutes = time / 60;
            int seconds = time % 60;
            timeFont.draw(
                batch,
                (minutes < 10 ? "0" + minutes : minutes) +
                    ":" +
                    (seconds < 10 ? "0" + seconds : seconds),
                62,
                50
            );

            // draw next break info
            if (timeUntilBreak >= 0)
            {
                int breakMinutes = timeUntilBreak / 60;
                int breakSeconds = timeUntilBreak % 60;
                breakInfoFont.draw(
                    batch,
                    "Break in " +
                        (breakMinutes < 10 ? "0" + breakMinutes : breakMinutes) +
                        ":" +
                        (breakSeconds < 10 ? "0" + breakSeconds: breakSeconds),
                    62,
                    65
                );
            }
            else
            {
                // no-op. there are no breaks
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        defenderPoints = 0;
        attackerPoints = 0;
        time = 0;
    }

    @Override
    public boolean handleClick(float x, float y, int button) {
        return false;
    }

    @Override
    public boolean handleDrag(float screenX, float screenY, float x, float y) {
        return false;
    }

    @Override
    public boolean handleRelease(float x, float y, int button) {
        return false;
    }

    public void setPoints(int defenderPoints, int attackerPoints) {
        this.defenderPoints = defenderPoints;
        this.attackerPoints = attackerPoints;
    }

    public int getTime() {
        return time;
    }

    public void setTeamNames(String attacker, String defender) {
        this.attacker = attacker;
        this.defender = defender;
        if(defender.length() > attacker.length()) {
            this.longestTeam = defender;
        }
        else {
            this.longestTeam = attacker;
        }
    }

    @Override
    public boolean handleMouseMove(float x, float y) {
        return false;
    }
}
