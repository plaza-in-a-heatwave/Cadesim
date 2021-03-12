package com.benberi.cadesim.game.screen.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.screen.SeaBattleScreen;
import com.benberi.cadesim.util.ShapeArc;
import com.benberi.cadesim.util.Team;

public class GameInformation extends SeaBattleScreen{
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
    private ShapeArc a;
    private Color panelBorderColor = new Color(216f/255f,196f/255f,55f/255f,255f/255f);
	public GameInformation(GameContext context) {
		super(context);
        this.context = context;
        buildStage();
    }

    public void buildStage() {
    	a = new ShapeArc();
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
        setTeamColors();
        this.panel = drawPanel(140,140, 20, new Color(45/255f,45/255f,45/255f,0.8f));
    }

    public void setTeamColors() {
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
    
    public static Texture drawPanel(int width, int height, int cornerRadius, Color color) {

    	Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap ret = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(color);

        pixmap.drawCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.drawCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.drawCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.drawCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);
        pixmap.drawRectangle(cornerRadius, 0, width - cornerRadius * 2, height);
        pixmap.fillRectangle(cornerRadius, 0, width - cornerRadius * 2, height);
        pixmap.drawRectangle(0, cornerRadius, width, height - cornerRadius * 2);
        pixmap.fillRectangle(0, cornerRadius, width, height - cornerRadius * 2);

        ret.setColor(color);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixmap.getPixel(x, y) != 0) ret.drawPixel(x, y);
            }
        }
        pixmap.dispose();
    	 
    	 Texture texture = new Texture(ret);
    	 return texture;
    	}

    public void render(float delta) {
        int xPlacement = 60 + (longestTeam.length() * 6);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        a.setProjectionMatrix(stage.getCamera().combined);
        stage.getBatch().begin();
        stage.getBatch().draw(panel, 5, 205);
        // draw defender
        fontTeamDefender.draw(stage.getBatch(), defender + ":", 38,320 );
        fontPointsDefender.draw(stage.getBatch(), Integer.toString(defenderPoints), xPlacement,318 );
        stage.getBatch().draw(areDefender?defenderUs:defenderThem, 18, 305);

        // draw attacker
        fontTeamAttacker.draw(stage.getBatch(), attacker + ":", 38,297 );
        fontPointsAttacker.draw(stage.getBatch(), Integer.toString(attackerPoints), xPlacement,295 );
        stage.getBatch().draw(areDefender?attackerThem:attackerUs, 18, 282);

        if (timeUntilBreak == 0 && breakTime >= 0)
        {
            // draw break
            int minutes = breakTime / 60;
            int seconds = breakTime % 60;
            timeFont.draw(
            		stage.getBatch(),
                (minutes < 10 ? "0" + minutes : minutes) +
                    ":" +
                    (seconds < 10 ? "0" + seconds : seconds),
                62,
                350
            );

            // draw current break info
            breakInfoFont.draw(
            		stage.getBatch(),
                "Break",
                62,
                265
            );
        }
        else
        {
            // draw time
            int minutes = time / 60;
            int seconds = time % 60;
            timeFont.draw(
            		stage.getBatch(),
                (minutes < 10 ? "0" + minutes : minutes) +
                    ":" +
                    (seconds < 10 ? "0" + seconds : seconds),
                62,
                250
            );

            // draw next break info
            if (timeUntilBreak >= 0)
            {
                int breakMinutes = timeUntilBreak / 60;
                int breakSeconds = timeUntilBreak % 60;
                breakInfoFont.draw(
                		stage.getBatch(),
                    "Break in " +
                        (breakMinutes < 10 ? "0" + breakMinutes : breakMinutes) +
                        ":" +
                        (breakSeconds < 10 ? "0" + breakSeconds: breakSeconds),
                    62,
                    365
                );
            }
            else
            {
                // no-op. there are no breaks
            }
        }

        stage.getBatch().end();
        
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(panelBorderColor); // Red line
 
        shapeRenderer.rectLine(5, 270, 145, 270, 2);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.line(5, 225, 5, 325);
        shapeRenderer.line(5+15, 205, 140-15, 205);
        shapeRenderer.line(6+140, 225, 6+140, 325);
        shapeRenderer.line(10+15, 205+140, 140-15, 205+140);
        shapeRenderer.end();
        a.begin(ShapeType.Line);
        a.setColor(panelBorderColor);
        a.arc(25, 225, 20, 180, 90);
        a.arc(5+120, 325, 20, 0, 90);
        a.arc(25, 325, 20, 90, 90);
        a.arc(5+120, 225, 20, 270, 90);
        a.end();
    }

    public void dispose() {
        defenderPoints = 0;
        attackerPoints = 0;
        time = 0;
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
}
