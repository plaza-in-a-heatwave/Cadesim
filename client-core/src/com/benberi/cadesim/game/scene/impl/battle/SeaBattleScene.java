package com.benberi.cadesim.game.scene.impl.battle;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.projectile.CannonBall;
import com.benberi.cadesim.game.entity.vessel.*;
import com.benberi.cadesim.game.entity.vessel.move.MoveAnimationTurn;
import com.benberi.cadesim.game.entity.vessel.move.MovePhase;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Cell;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Whirlpool;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.Wind;
import com.benberi.cadesim.game.scene.impl.control.BattleControlComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SeaBattleScene implements GameScene {

    /**
     * The main game context
     */
    private GameContext context;

    /**
     * The sprite batch renderer
     */
    private SpriteBatch batch;

    /**
     * The camera view of the scene
     */
    private OrthographicCamera camera;
    
    /**
     * Whether the camera follows the vessel
     * Initially true on respawn
     * 
     * As per PP, drag moves the camera anywhere
     *     if you right clicked, camera stays when you release
     *     if you left  clicked, camera locks back onto ship
     */
    private boolean cameraFollowsVessel = true;

    /**
     * The sea texture
     */
    private Texture sea;
    
    /**
     * The islands texture
     */
    private Texture alkaid_island;
    private Texture pukru_island;
    private Texture doyle_island;
    private Texture isle_keris_island;
    
    private ArrayList<Texture> islandList = new ArrayList<Texture>();
    
    Texture selectedIsland = alkaid_island;

    /**
     * The shape renderer
     */
    private ShapeRenderer renderer;

    /**
     * If the user can drag the map
     */
    private boolean canDragMap;

    /**
     * The game information panel
     */
    private GameInformation information;

    /**
     * The sea battle font for ship names
     */
    private BitmapFont font;

    /**
     * The current execution slot move
     */
    private int currentSlot = -1;

    /**
     * The current executing phase
     */
    private MovePhase currentPhase;

    private BlockadeMap blockadeMap;
    public MenuComponent mainmenu;

    private int vesselsCountWithCurrentPhase = 0;
    private int vesselsCountNonSinking = 0;


    private boolean isAnimationOngoing = false;
    public boolean isAnimationOngoing() {
        return isAnimationOngoing;
    }

    public void setAnimationOngoing(boolean isAnimationOngoing) {
        this.isAnimationOngoing = isAnimationOngoing;
    }

    /**
     * Is the turn finished? (internal flag)
     */
    private boolean turnFinished;
    /**
     * Sound effects
     */
    private float sound_volume = 0.05f;

	private Sound cannonBigSound;
    private Sound cannonSmallSound;
    private Sound cannonMediumSound;
    private Sound cannonHitSound;
    private Sound splashSound;
    private Sound moveSound;
    private Sound bumpSound;
    private Sound sunkSound;
    @SuppressWarnings("unused")
	private Sound creakSound;
    private boolean isStartedShooting = false;
    
    private boolean isTurnFinished() {
        return turnFinished;
    }
    private void setTurnFinished(boolean turnFinished) {
        this.turnFinished = turnFinished;
    }

    public SeaBattleScene(GameContext context) {
        this.context = context;
        information = new GameInformation(context, this);
        context.inputMultiplexer.addProcessor(context.gameStage);
    }

    public void createMap(int[][] tiles) {
        // if there was a dialog window open, close it
        if (getMenu().isSettingsDialogOpen()) {
            getMenu().closeSettingsDialog();
        }
        
        // if there was a previous map: delete it
        if (blockadeMap != null) { blockadeMap.dispose();}
        blockadeMap = new BlockadeMap(context, tiles);
        selectedIsland = islandList.get(context.getIslandId());
    }

    /**
     * set up the input processor for the chat and game
     */
    public void setup() {
        
    }

    private void recountVessels() {
        vesselsCountWithCurrentPhase = context.getEntities().countVesselsByPhase(currentPhase);
        vesselsCountNonSinking = context.getEntities().countNonSinking();
    }

    @Override
    public void create() {
        font = context.getManager().get(context.getAssetObject().seaFont);
        renderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        information.create();
        initSounds();
        initTextures();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 200);
        this.mainmenu = new MenuComponent(context, this);
        mainmenu.create();
    }
    /*
     * Initialize sound effects for game
     */
    public void initSounds() {
        cannonBigSound = context.getManager().get(context.getAssetObject().cannonbig_sound);
        cannonSmallSound = context.getManager().get(context.getAssetObject().cannonsmall_sound);
        cannonMediumSound = context.getManager().get(context.getAssetObject().cannonmedium_sound);
        cannonHitSound = context.getManager().get(context.getAssetObject().hit_sound);
        splashSound = context.getManager().get(context.getAssetObject().splash_sound);
        moveSound = context.getManager().get(context.getAssetObject().move2_sound);
        bumpSound = context.getManager().get(context.getAssetObject().rockhit_sound);
        sunkSound = context.getManager().get(context.getAssetObject().shipsunk_sound);
        creakSound = context.getManager().get(context.getAssetObject().creak_sound);
    }
    /*
     * Initialize textures for game
     */
    public void initTextures() {
        sea = context.getManager().get(context.getAssetObject().sea);
        alkaid_island = context.getManager().get(context.getAssetObject().alkaid_island);
        pukru_island = context.getManager().get(context.getAssetObject().pukru_island);
        doyle_island = context.getManager().get(context.getAssetObject().doyle_island);
        isle_keris_island = context.getManager().get(context.getAssetObject().isle_keris_island);
        //add all island Textures in one go
        Collections.addAll(islandList,alkaid_island, pukru_island, doyle_island, isle_keris_island);
        sea.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }
    public float getSound_volume() {
		return sound_volume;
	}

	public void setSound_volume(float sound_volume) {
		this.sound_volume = sound_volume;
	}
    @Override
    public void update(){
    	if(context.getBattleSceneMenu().teamTable.isVisible()) {
    		context.getBattleSceneMenu().fillTeamList();
    	}
		
        // update the camera
        camera.update();
        //keep user from scrolling to far to black screens
        camera.position.x = clamp(camera.position.x, Gdx.graphics.getWidth() , -Gdx.graphics.getWidth() - 100);
        camera.position.y = clamp(camera.position.y, Gdx.graphics.getHeight() + 800, -Gdx.graphics.getHeight() + 500);
        if (currentSlot > -1) { 
            if (vesselsCountWithCurrentPhase != vesselsCountNonSinking) { //bug fix-new players joining
                MovePhase phase = MovePhase.getNext(currentPhase);
                if (phase == null) {
                    for (Vessel v : context.getEntities().listVesselEntities()) {
                        v.setMovePhase(null);
                    }
                    currentPhase = phase;
                    recountVessels();
                }
            }
            else if (vesselsCountWithCurrentPhase == vesselsCountNonSinking) {
                 MovePhase phase = MovePhase.getNext(currentPhase);
                 if (phase == null) {
                     for (Vessel vessel : context.getEntities().listVesselEntities()) {
                         MoveAnimationTurn turn = vessel.getStructure().getTurn(currentSlot);
                         if (turn.isSunk()) {
                             vessel.setSinking(true);
                         	 sunkSound.play(getSound_volume());
                         }
                     }
                     currentPhase = MovePhase.MOVE_TOKEN;
                     currentSlot++;
                     for (Vessel v : context.getEntities().listVesselEntities()) {
                         v.setMovePhase(null);
                     }

                     // end turn, but need to wait for things to sink...
                     if (currentSlot > 3) {
                         currentSlot = -1;
                         this.setTurnFinished(true);
                     }

                     recountVessels();
                 }
                 else {
                     currentPhase = phase;
                     recountVessels();
                 }
             }
         }

        boolean waitingForSink = false; // in case a ship needs to finish sinking before the turn can end
        for (Vessel vessel : context.getEntities().listVesselEntities()) {
            MovePhase phase = MovePhase.getNext(vessel.getMovePhase());
            if (vessel.isSinking()) {
                if(!vessel.isSinkingTexture()) {
                    vessel.tickNonSinkingTexture();
                }else {
                    vessel.tickSinkingTexture();
                }
                continue;
            }
            if (vessel.getMoveDelay() != -1) {
                vessel.tickMoveDelay();
            }
            if (!vessel.isMoving()) {
                if (currentSlot != -1) {
                    MoveAnimationTurn turn = vessel.getStructure().getTurn(currentSlot);
                    if (currentPhase == MovePhase.MOVE_TOKEN && vessel.getMovePhase() == null) {
                        if (turn.getAnimation() != VesselMovementAnimation.NO_ANIMATION && vessel.getMoveDelay() == -1) {
                            if (!VesselMovementAnimation.isBump(turn.getAnimation())) {
                                vessel.performMove(turn.getAnimation());
                                moveSound.play(getSound_volume());
                            }
                            else {
                                vessel.performBump(turn.getTokenUsed(), turn.getAnimation());
                                bumpSound.play(getSound_volume());
                            }

                            turn.setAnimation(VesselMovementAnimation.NO_ANIMATION);
                        }
                        else {
                            vessel.setMovePhase(phase);
                            recountVessels();
                        }
                    }
                    else if (currentPhase == MovePhase.ACTION_MOVE && vessel.getMovePhase() == MovePhase.MOVE_TOKEN && vessel.getMoveDelay() == -1 && !context.getEntities().hasDelayedVessels()) {
                        if (turn.getSubAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                            if (!VesselMovementAnimation.isBump(turn.getSubAnimation())) {
                                vessel.performMove(turn.getSubAnimation());

                            }
                            else {
                                if(turn.getSpinCollision()) { // for turning ship inside whirlpool
                                    vessel.setRotationIndex(turn.getFace().getDirectionId());
                                    bumpSound.play(getSound_volume());
                                }
                                vessel.performBump(MoveType.NONE, turn.getSubAnimation());
                            }
                            turn.setSubAnimation(VesselMovementAnimation.NO_ANIMATION);
                        }
                        else {
                            vessel.setMovePhase(phase);
                            recountVessels();
                        }
                    }
                    else if (currentPhase == MovePhase.SHOOT && vessel.getMovePhase() == MovePhase.ACTION_MOVE  && vessel.getMoveDelay() == -1 && vessel.getCannonballs().size() == 0 && !context.getEntities().hasDelayedVessels()) {
                        if (turn.getLeftShoots() == 0 && turn.getRightShoots() == 0) {
                            vessel.setMovePhase(phase);
                            recountVessels();
                        }
                        else {
                        	if (turn.getLeftShoots() > 0) {
                        		isStartedShooting = true;
                        		playCannonSounds(vessel, turn.getLeftShoots());
	                            vessel.performLeftShoot(turn.getLeftShoots());
	                            turn.setLeftShoots(0);
	                            
                        	}
                            
                            if (turn.getRightShoots() > 0) {
                            	isStartedShooting = true;
                            	playCannonSounds(vessel, turn.getRightShoots());
                                vessel.performRightShoot(turn.getRightShoots());
                                turn.setRightShoots(0);
                                
                            }
                        }
                    }
                }
            }
            else {
                if (vessel.isBumping()) {
                    VesselBumpVector vector = vessel.getBumpVector();
                    if (!vessel.isBumpReached()) {
                        float speed = vessel.getCurrentPerformingMove() == VesselMovementAnimation.BUMP_PHASE_1 ? 1f : 1.75f;

                        vessel.setX(vessel.getX() + (vector.getDirectionX() * speed * Gdx.graphics.getDeltaTime()));
                        vessel.setY(vessel.getY() + (vector.getDirectionY() * speed * Gdx.graphics.getDeltaTime()));

                        float distance = vector.getStart().dst(new Vector2(vessel.getX(), vessel.getY()));
                        if(distance >= vector.getDistance())
                        {
                            vessel.setPosition(vector.getEnd().x, vector.getEnd().y);
                            vessel.setBumpReached(true);
                            if (vessel.getCurrentPerformingMove() == VesselMovementAnimation.BUMP_PHASE_1) {
                                vessel.tickBumpRotation(2);
                            }
                            else {
                                vessel.tickBumpRotation(1);
                            }
                        }
                        else if (vessel.getCurrentPerformingMove() == VesselMovementAnimation.BUMP_PHASE_2 && distance >= vector.getDistance() / 2 && !vector.isPlayedMiddleAnimation()) {
                            vessel.tickBumpRotation(1);
                            vector.setPlayedMiddleAnimation(true);
                        }
                    }
                    else {
                        if (vessel.getCurrentPerformingMove() == VesselMovementAnimation.BUMP_PHASE_1 || vessel.getCurrentPerformingMove().getId() >= 12) {
                            vessel.setX(vessel.getX() + (vector.getDirectionX() * 2f * Gdx.graphics.getDeltaTime()));
                            vessel.setY(vessel.getY() + (vector.getDirectionY() * 2f * Gdx.graphics.getDeltaTime()));
                            if (vector.getStart().dst(new Vector2(vessel.getX(), vessel.getY())) >= vector.getDistance()) {
                                vessel.setPosition(vector.getEnd().x, vector.getEnd().y);
                                vessel.tickBumpRotation(1);
                                vessel.disposeBump();
                            }
                        }
                        else {
                            vessel.tickBumpRotation(1);
                            vessel.disposeBump();
                        }
                    }
                }
                else {
                    VesselMovementAnimation move = vessel.getCurrentPerformingMove();

                    Vector2 start = vessel.getAnimation().getStartPoint();
                    Vector2 inbetween = vessel.getAnimation().getInbetweenPoint();
                    Vector2 end = vessel.getAnimation().getEndPoint();
                    Vector2 current = vessel.getAnimation().getCurrentAnimationLocation();

                    // calculate step based on progress towards target (0 -> 1)
                    // float step = 1 - (ship.getEndPoint().dst(ship.getLinearVector()) / ship.getDistanceToEndPoint());


                   // float velocityTurns = (0.011f * Gdx.graphics.getDeltaTime()) * 100; //Gdx.graphics.getDeltaTime();
                    float velocityTurns = (1.25f * Gdx.graphics.getDeltaTime()); //Gdx.graphics.getDeltaTime();
                    float velocityForward = (1.8f * Gdx.graphics.getDeltaTime());

                    if (!move.isOneDimensionMove()) {
                        vessel.getAnimation().addStep(velocityTurns);
                        // step on curve (0 -> 1), first bezier point, second bezier point, third bezier point, temporary vector for calculations
                        Bezier.quadratic(current, (float) vessel.getAnimation().getCurrentStep(), start.cpy(),
                                inbetween.cpy(), end.cpy(), new Vector2());
                    }
                    else {
                        // When ship moving forward, we may not want to use the curve

                        int add = move.getIncrementXForRotation(vessel.getRotationIndex());
                        if (add == -1 || add == 1) {
                            current.x += (velocityForward * add);
                            //current.x += (velocityForward * (float) add);
                        }
                        else {
                            add = move.getIncrementYForRotation(vessel.getRotationIndex());
                            // current.y += (velocityForward * (float) add);
                            current.y += (velocityForward * add);
                        }
                        /// vessel.getAnimation().addStep(velocityForward);
                        vessel.getAnimation().addStep(velocityForward);
                    }

                    int result = (int) (vessel.getAnimation().getCurrentStep() * 100);
                    vessel.getAnimation().tickAnimationTicks(velocityTurns * 100);
                    
                    // check if the step is reached to the end, and dispose the movement
                    if (result >= 100) {
                        vessel.setX(end.x);
                        vessel.setY(end.y);
                        vessel.setMoving(false);

                        if (!move.isOneDimensionMove())
                            vessel.setRotationIndex(vessel.getRotationTargetIndex());

                        vessel.setMovePhase(MovePhase.getNext(vessel.getMovePhase()));
                        recountVessels();
                        vessel.setMoveDelay();
                    }
                    else {
                        // process move
                        vessel.setX(current.x);
                        vessel.setY(current.y);
                    }

                    if (result >= 25 && result <= 50 && vessel.getAnimation().getTickIndex() == 0 ||
                            result >= 50 && result <= 75 && vessel.getAnimation().getTickIndex() == 1 ||
                            result >= 75 && result <= 100 && vessel.getAnimation().getTickIndex() == 2 ||
                            result >= 99 && vessel.getAnimation().getTickIndex() == 3 ) {
                        vessel.tickRotation();
                        vessel.getAnimation().setTickIndex(vessel.getAnimation().getTickIndex() + 1);
                    }
                }
            }
            
            // let camera move with vessel if it's supposed to
            if (cameraFollowsVessel) {
                if(camera != null && context.myVessel != null) {
                    Vessel myVessel = context.getEntities().getVesselByName(context.myVessel);
                    camera.translate(
                            getIsometricX(myVessel.getX(), myVessel.getY(), myVessel) - camera.position.x,
                            getIsometricY(myVessel.getX(), myVessel.getY(), myVessel) - camera.position.y
                    );
                }
            }

            if (vessel.isSmoking()) {
                vessel.tickSmoke();
            }
            Iterator<CannonBall> itr = vessel.getCannonballs().iterator();
            while (itr.hasNext()) {
                CannonBall c = itr.next();
                if (c.isReleased()) {
                    if (c.hasSubCannon()) {
                        if (c.canReleaseSubCannon()) {
                            c.getSubcannon().setReleased(true);
                        }
                    }
                    if (!c.reached()) {
                        c.move();
                    } else {
                        if (c.finishedEndingAnimation()) {
                            itr.remove();
                        }
                        else {
                            c.tickEndingAnimation();
                        }
                    }
                }
            }
            if (vessel.isSinking()) {
                waitingForSink = true;
            }
        }
        BattleControlComponent b = context.getControlScene().getBnavComponent();
        if (b.isLockedDuringAnimate()) {

            // this condition breaks the lock if we've been timed out for too long
            boolean unlockTimedOut = ((System.currentTimeMillis() - b.getTimeSinceLockedDuringAnimate()) >=
                    Constants.MAX_CLIENT_LOCK_MILLIS);

            if ((isTurnFinished() && (!waitingForSink)) || unlockTimedOut)
            {
                // this is an error condition.
                if (unlockTimedOut) {
                    context.sendPostMessagePacket(
                            "/bug client unlock timeout threshold reached: " + Constants.MAX_CLIENT_LOCK_MILLIS + "ms."
                    ,"global");
                }

                setTurnFinished(false); // for next time
                b.updateMoveHistoryAfterTurn();  // post-process tooltips
                b.resetPlacedMovesAfterTurn();   // reset moves post-turn
                b.setLockedDuringAnimate(false); // unlock control
                setAnimationOngoing(false);      // mark animation done
            }

        }
        information.update();
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Gdx.gl.glViewport(0,200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 200);

        drawSea();
        drawIsland();
        // Render the map
        renderSeaBattle();

        // Render ships
        renderEntities();

        batch.end();
        information.render();
        //game stage
        context.gameStage.act();
        context.gameStage.getViewport().apply();
        context.gameStage.draw();
        mainmenu.render();
        Gdx.input.setInputProcessor(context.inputMultiplexer);
    }

    public GameInformation getInformation() {
        return information;
    }

    public MenuComponent getMenu() {
        return mainmenu;
    }

    /**
     * Draws the sea background
     */
    private void drawSea() {
        batch.draw(sea, -2000, -1000, 0, 0, 5000, 5000);
    }
    
    private void drawIsland() {
        if(selectedIsland == pukru_island) {
            batch.draw(selectedIsland, -1190,1090);
        }else {
            batch.draw(selectedIsland, -1290,1050);
        }
    }

    /**
     * Renders all entities
     */
    private void renderEntities() {
        renderer.setProjectionMatrix(camera.combined);

        for (int x = BlockadeMap.MAP_WIDTH - 1; x > -1; x--) {
            for (int y = BlockadeMap.MAP_HEIGHT - 1; y > -1; y--) {
                GameObject object = blockadeMap.getObject(x, y);
                if (object != null) {
                    TextureRegion region = object.getRegion();

                    int xx = (object.getX() * GameTile.TILE_WIDTH / 2) - (object.getY() * GameTile.TILE_WIDTH / 2) - region.getRegionWidth() / 2;
                    int yy = (object.getX() * GameTile.TILE_HEIGHT / 2) + (object.getY() * GameTile.TILE_HEIGHT / 2) - region.getRegionHeight() / 2;

                    if (!object.isOriented() || canDraw(xx + object.getOrientationLocation().getOffsetx(), yy + object.getOrientationLocation().getOffsety(), region.getRegionWidth(), region.getRegionHeight())) {
                        int offsetX = 0;
                        int offsetY = 0;
                        if (object.isOriented()) {
                            offsetX = object.getOrientationLocation().getOffsetx();
                            offsetY = object.getOrientationLocation().getOffsety();
                        }
                        else {
                            offsetX = object.getCustomOffsetX();
                            offsetY = object.getCustomOffsetY();
                        }
                        batch.draw(region, xx + offsetX, yy + offsetY);
                    }
                }

                Vessel vessel = context.getEntities().getVesselByPosition(x, y);
                if (vessel != null) {
                    // X position of the vessel
                    float xx = getIsometricX(vessel.getX(), vessel.getY(), vessel);

                    // Y position of the vessel
                    float yy = getIsometricY(vessel.getX(), vessel.getY(), vessel);

                    if (canDraw(xx + vessel.getOrientationLocation().getOffsetx(), yy + vessel.getOrientationLocation().getOffsety(), vessel.getRegionWidth(), vessel.getRegionHeight())) {
                        // draw vessel
                        batch.draw(vessel, xx + vessel.getOrientationLocation().getOffsetx(), yy + vessel.getOrientationLocation().getOffsety());
                    }


                    Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(v, 0, 200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 200);

                    float xxx = xx + vessel.getOrientationLocation().getOffsetx();
                    float yyy = yy + vessel.getOrientationLocation().getOffsety();

                    if (v.x>= xxx && v.x <= xxx + vessel.getRegionWidth() && v.y >= yyy && v.y <= yyy + vessel.getRegionHeight()) {

                        batch.end();

                        // get diameter, divide by sqrt(2): our diameter matches |_ (geometric), but we want \ (isometric).
                        float diameter = (vessel.getInfluenceRadius() * 2) / 1.4142f;
                        renderer.begin(ShapeRenderer.ShapeType.Line);

                        Gdx.gl.glEnable(GL20.GL_BLEND);
                        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                        Color color = (context.myVessel.equals(vessel.getName()) || context.myTeam.getID() == vessel.getTeam().getID()) ? Vessel.DEFAULT_BORDER_COLOR.cpy() : vessel.getTeam().getColor().cpy();
                        color.a = 0.35f;

                        renderer.setColor(color);
                        for (int i = 0; i < 5; i++) {
                            float width = (diameter * GameTile.TILE_WIDTH) + i;
                            float height = (diameter * GameTile.TILE_HEIGHT) + i;
                            renderer.ellipse(xx - width / 2 + vessel.getRegionWidth() / 2, yy - height / 2 + vessel.getRegionHeight() / 2, width, height);
                        }
                        renderer.end();

                        Gdx.gl.glDisable(GL20.GL_BLEND);
                        batch.begin();
                    }
                }
            }
        }

        for (Vessel vessel : context.getEntities().listVesselEntities()) {
            // render cannon balls
            for (CannonBall c : vessel.getCannonballs()) {
                float cx = getIsometricX(c.getX(), c.getY(), c);
                float cy = getIsometricY(c.getX(), c.getY(), c);
                if (!canDraw(cx, cy, c.getRegionWidth(), c.getRegionHeight())) {
                    continue;
                }

                if (!c.reached()) {
                    batch.draw(c, cx, cy);
                }
                else {
                    cx = getIsometricX(c.getX(), c.getY(), c.getEndingAnimationRegion());
                    cy = getIsometricY(c.getX(), c.getY(), c.getEndingAnimationRegion());
                    batch.draw(c.getEndingAnimationRegion(), cx, cy);
                    if(isStartedShooting)
                    {
    	            	if(c.getEndingAnimationRegion().getTexture() == context.getManager().get(context.getAssetObject().hit)) {
    	            		cannonHitSound.play(getSound_volume());
    	            	}else {
    	            		splashSound.play(getSound_volume());
    	            	}
    	            	isStartedShooting = false;
                    }

                }
            }

            // render smoke
            if (vessel.isSmoking()) {
                TextureRegion r = vessel.getShootSmoke();
                float cx = getIsometricX(vessel.getX(), vessel.getY(), r);
                float cy = getIsometricY(vessel.getX(), vessel.getY(), r);
                if (canDraw(cx, cy, r.getRegionWidth(), r.getRegionHeight())) {
                    batch.draw(r, cx, cy);
                }
            }

            batch.end();
            
            // render move bar
            int BAR_HEIGHT_ABOVE_SHIP = 15; // px
            int BAR_HEIGHT = 7;
            renderer.begin(ShapeRenderer.ShapeType.Line);
            float x = getIsometricX(vessel.getX(), vessel.getY(), vessel);
            float y = getIsometricY(vessel.getX(), vessel.getY(), vessel);
 
            int width = vessel.getMoveType().getBarWidth() + 1;
            renderer.setColor(Color.BLACK);

            // draw move bar bounding box
            renderer.rect(x + (vessel.getRegionWidth() / 2) - (width / 2), y + vessel.getRegionHeight() + BAR_HEIGHT_ABOVE_SHIP, width, BAR_HEIGHT);

            // draw white move fill
            renderer.end();
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.WHITE);

            int fill = vessel.getNumberOfMoves(); // number to fill
            int w; // width of each fill
            if (vessel.getMoveType() == VesselMoveType.FOUR_MOVES)
            {
                w = (width) / 4;
            }
            else
            {
                fill = fill > 3? 3:fill; // cap at 3 if large ship
                w = (width) / 3;
            }
            renderer.rect(x + (vessel.getRegionWidth() / 2) - (width / 2), y + vessel.getRegionHeight() + BAR_HEIGHT_ABOVE_SHIP, fill * w, BAR_HEIGHT - 1);

            // draw red fill extension if large ship
            if (vessel.getMoveType() == VesselMoveType.THREE_MOVES && vessel.getNumberOfMoves() > 3) {
                renderer.setColor(Color.RED);
                renderer.rect(x + (vessel.getRegionWidth() / 2) - (width / 2) + (3*w), y + vessel.getRegionHeight() + BAR_HEIGHT_ABOVE_SHIP, w, BAR_HEIGHT - 1);
            }
            renderer.end();

            // draw ship name and flags
            if (vessel.getName().equalsIgnoreCase(context.myVessel) || vessel.getTeam().getID() == context.myTeam.getID()) {
                font.setColor(Vessel.DEFAULT_BORDER_COLOR);
            }
            else {
                font.setColor(vessel.getTeam().getColor());
            }

            // name
            int NAME_HEIGHT_ABOVE_SHIP = BAR_HEIGHT_ABOVE_SHIP + BAR_HEIGHT + (int)font.getCapHeight() + 10; // px
            batch.begin();
            GlyphLayout layout = new GlyphLayout(font, vessel.getName());
            font.draw(batch, vessel.getName(), x + (vessel.getRegionWidth() / 2) - (layout.width / 2), y + vessel.getRegionHeight() + NAME_HEIGHT_ABOVE_SHIP);

            // flags
            int FLAG_HEIGHT_ABOVE_SHIP = 10 + NAME_HEIGHT_ABOVE_SHIP;

            int symbwidth   = 10; // width of symbol
            int symbspacing = 3;  // space between symbols
            int numsymbols  = vessel.getFlags().size();
            float startX = x + (vessel.getRegionWidth() / 2) - ((numsymbols * symbwidth) / 2) - (((numsymbols-1) * symbspacing) / 2);
            float flagsY = y + vessel.getRegionHeight() + FLAG_HEIGHT_ABOVE_SHIP;

            int points = 0;
            for (FlagSymbol symbol : vessel.getFlags()) {
                if (!symbol.isWar()) {
                    points += symbol.getSize();
                }
                batch.draw(symbol, startX, flagsY);
                startX += symbol.getRegionWidth() + 3;
            }

            if (vessel.hasScoreDisplay()) {
                if (points > 0) {
                    Color color = (context.myVessel.equals(vessel.getName()) || context.myTeam.getID() == vessel.getTeam().getID()) ? Vessel.DEFAULT_BORDER_COLOR.cpy() : vessel.getTeam().getColor().cpy();
                    color.a = vessel.getScoreDisplayMovement() / 100f;
                    if (color.a < 0) {
                        color.a = 0;
                    }
                    font.setColor(color);
                    font.draw(batch, "+" + points + " points", x + vessel.getRegionWidth() + 20, y + vessel.getRegionHeight() * 1.7f - (100 - vessel.getScoreDisplayMovement()));
                }
                vessel.tickScoreMovement();
            }
        }
    }


    public float getIsometricX(float x, float y, TextureRegion region) {
        return (x * GameTile.TILE_WIDTH / 2) - (y * GameTile.TILE_WIDTH / 2) - (region.getRegionWidth() / 2);
    }

    public float getIsometricY(float x, float y, TextureRegion region) {
        return (x * GameTile.TILE_HEIGHT / 2) + (y * GameTile.TILE_HEIGHT / 2) - (region.getRegionHeight() / 2);
    }

    @Override
    public void dispose() {
        currentPhase = MovePhase.MOVE_TOKEN;
        currentSlot = -1;
        information.dispose();
        recountVessels();
        camera = null;
    }

    @Override
    public boolean handleDrag(float sx, float sy, float x, float y) {
        if(!mainmenu.audio_slider.isVisible() && !mainmenu.isSettingsDialogOpen()) {
            if (sy > camera.viewportHeight) {
                return false;
            }

            if (this.canDragMap) {
                camera.translate(-x, y);
            }
            if (mainmenu.handleDrag(sx, sy, x, y)) {
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean handleClick(float x, float y, int button) {
    	if(!mainmenu.isClickingMenuButton(x, y) && !mainmenu.isClickingMenuTable(x, y) && !mainmenu.isSettingsDialogOpen()) {//keep from moving when menu button clicked
            if (mainmenu.handleClick(x, y, button)) {
                return true;
            }
            if (y < camera.viewportHeight && !mainmenu.teamTable.isVisible()) {
                // handle camera not following vessel
                cameraFollowsVessel = false;

                this.canDragMap = true;
                return true;
            }
            this.canDragMap = false;	 
    	}
        return false;
    }
    
    @Override
    public boolean handleMouseMove(float x, float y) {
        if (mainmenu.handleMouseMove(x, y)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean handleClickRelease(float x, float y, int button) {
    	if(!mainmenu.isClickingMenuButton(x, y) && !mainmenu.isClickingMenuTable(x, y) && !mainmenu.isSettingsDialogOpen() ) {//keep from moving when menu button clicked
	        if (mainmenu.handleRelease(x, y, button)) {
	            return true;
	        }
        	if (y < camera.viewportHeight && !mainmenu.teamTable.isVisible()) {
                // handle camera following/not following vessel
                if (button == Input.Buttons.RIGHT) {
                    cameraFollowsVessel = false;
                } else {
                    this.cameraFollowsVessel = true;
                    try {
                        Vessel vessel = context.getEntities().getVesselByName(context.myVessel);
                        camera.translate(
                                getIsometricX(vessel.getX(), vessel.getY(), vessel) - camera.position.x,
                                getIsometricY(vessel.getX(), vessel.getY(), vessel) - camera.position.y
                        );
                    }catch(NullPointerException e){
                        //TO-DO -fix issue with null pointer
                    }
                }
                return true;
            }
            this.canDragMap = false;	
    	}
        return false;
    }



    private void renderSeaBattle() {
        // The map tiles
       // GameTile[][] tiles = map.getTiles();

        Cell[][] sea = blockadeMap.getSea();
        Wind[][] winds = blockadeMap.getWinds();
        Whirlpool[][] whirls = blockadeMap.getWhirls();

        for (int i = 0; i < sea.length; i++) {
            for(int j = 0; j < sea[i].length; j++) {
                TextureRegion region = sea[i][j].getRegion();
                int x = (i * GameTile.TILE_WIDTH / 2) - (j * GameTile.TILE_WIDTH / 2) - region.getRegionWidth() / 2;
                int y = (i * GameTile.TILE_HEIGHT / 2) + (j * GameTile.TILE_HEIGHT / 2) - region.getRegionHeight() / 2;

                if (canDraw(x, y, GameTile.TILE_WIDTH, GameTile.TILE_HEIGHT)) {
                    batch.draw(region, x, y);
                    if (winds[i][j] != null) {
                        region = winds[i][j].getRegion();

                        batch.draw(region, x, y);
                    }
                    else if (whirls[i][j] != null) {
                        region = whirls[i][j].getRegion();
                        batch.draw(region, x, y);
                    }
                }
            }
        }
    }

    private boolean canDraw(float x, float y, int width, int height) {
        return x + width >= camera.position.x - camera.viewportWidth / 2 && x <= camera.position.x + camera.viewportWidth / 2 &&
                y + height >= camera.position.y - camera.viewportHeight / 2 && y <= camera.position.y + camera.viewportHeight / 2;
    }

    public void setTurnExecute() {
        currentSlot = 0;
        currentPhase = MovePhase.MOVE_TOKEN;
        for (Vessel vessel : context.getEntities().listVesselEntities()) {
            vessel.setMovePhase(null);
        }
        recountVessels();

        //lock controls
        context.getControlScene().getBnavComponent().setLockedDuringAnimate(true);
        setAnimationOngoing(true);
    }

    public BlockadeMap getMap() {
        return blockadeMap;
    }

    public void initializePlayerCamera(Vessel vessel) {
        cameraFollowsVessel = true; // force reset
        camera.translate(
                getIsometricX(vessel.getX(), vessel.getY(), vessel) - camera.position.x,
                getIsometricY(vessel.getX(), vessel.getY(), vessel) - camera.position.y
        );
    }
    
    /**
     * Helper method to clamp values in certain range
     */
    public float clamp(float x, float f, int min) {
        if(x > min) {
            if(x < f) {
                return x;
            } else return f;
        } else return min;
    }
    
    public void playCannonSounds(Vessel vessel, int shots) {
    	if(shots == 1) {
        	switch(vessel.getCannonSize()) {
		        case "large":
		        	cannonBigSound.play(getSound_volume());
		        case "medium":
		            cannonMediumSound.play(getSound_volume());
		        case "small":
		            cannonSmallSound.play(getSound_volume());
	    	}
    	}else if(shots == 2) {
        	switch(vessel.getCannonSize()) {
		        case "large":
		        	//doesn't sound like shooting 2, need to figure out how to add delay
		        	cannonBigSound.play(getSound_volume());
		        	cannonBigSound.play(getSound_volume());
		        case "medium":
		            cannonMediumSound.play(getSound_volume());
		            cannonMediumSound.play(getSound_volume());
		        case "small":
		            cannonSmallSound.play(getSound_volume());
		            cannonSmallSound.play(getSound_volume());
		    	}
    		}
    	}
}
