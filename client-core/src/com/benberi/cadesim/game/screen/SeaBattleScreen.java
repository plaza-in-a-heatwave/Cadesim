package com.benberi.cadesim.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.benberi.cadesim.game.screen.component.BattleControlComponent;
import com.benberi.cadesim.game.screen.component.GameInformation;
import com.benberi.cadesim.game.screen.component.MenuComponent;
import com.benberi.cadesim.game.screen.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.game.screen.impl.battle.map.GameObject;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.GameTile;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Cell;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Whirlpool;
import com.benberi.cadesim.game.screen.impl.battle.map.tile.impl.Wind;
import com.benberi.cadesim.util.AbstractScreen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SeaBattleScreen extends AbstractScreen implements InputProcessor{

    /**
     * The main game context
     */
    private GameContext context;


    /**
     * The camera view of the scene
     */
    public OrthographicCamera othercamera;
    
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
    public MenuComponent battleMenu;
    public BattleControlComponent control;

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

    public SeaBattleScreen(GameContext context) {
        this.context = context;
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

    private void recountVessels() {
        vesselsCountWithCurrentPhase = context.getEntities().countVesselsByPhase(currentPhase);
        vesselsCountNonSinking = context.getEntities().countNonSinking();
    }

    @Override
    public void buildStage() {
        othercamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 200);
    	Vessel v = Vessel.createVesselByType(context, null, 0, 0, context.myVesselType);
        control = new BattleControlComponent(
        	context,
        	stage,
        	v.getMoveType() != VesselMoveType.FOUR_MOVES, // is it a big ship
        	v.isDoubleShot()                              // single or double shot
        );
        battleMenu = new MenuComponent(context,stage);
        information = new GameInformation(context);
        context.setBattleScreen(this);
        context.setControl(control);
        context.setBattleMenu(battleMenu);
        font = context.getManager().get(context.getAssetObject().seaFont);
        initSounds();
        initTextures();
        SeaBattleScreen screen = this;
        Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
		    	multiplexer.addProcessor(stage);
		    	multiplexer.addProcessor(screen);
		    	Gdx.input.setInputProcessor(multiplexer);
			}
        });
    	update();
    	control.buildStage();
    	battleMenu.buildStage();
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
	
    public void update(){
        // update the camera
        othercamera.update();
        //keep user from scrolling to far to black screens
        othercamera.position.x = clamp(othercamera.position.x, Gdx.graphics.getWidth() , -Gdx.graphics.getWidth() - 100);
        othercamera.position.y = clamp(othercamera.position.y, Gdx.graphics.getHeight() + 800, -Gdx.graphics.getHeight() + 500);
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
                if(othercamera != null && context.myVessel != null) {
                    Vessel myVessel = context.getEntities().getVesselByName(context.myVessel);
                    othercamera.translate(
                            getIsometricX(myVessel.getX(), myVessel.getY(), myVessel) - othercamera.position.x,
                            getIsometricY(myVessel.getX(), myVessel.getY(), myVessel) - othercamera.position.y
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
        BattleControlComponent b = context.getControl();
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
    }

    @Override
    public void render(float delta) {
    	update();
    	if(battleMenu.teamTable.isVisible()) {
    		battleMenu.fillTeamList();
    	}
    	stage.getViewport().setCamera(othercamera);
    	stage.getBatch().setProjectionMatrix(othercamera.combined);
    	stage.getViewport().apply();
        drawSea();
        drawIsland();
        // Render the map
        renderSeaBattle();
        // Render ships
        renderEntities();
        information.render(delta);
        control.render(delta);
        control.update();
        battleMenu.render(delta);
    }

    public GameInformation getInformation() {
        return information;
    }

    public MenuComponent getMenu() {
        return battleMenu;
    }

    /**
     * Draws the sea background
     */
    private void drawSea() {
    	stage.getBatch().begin();
    	stage.getBatch().draw(sea, -2000, -1000, 0, 0, 5000, 5000);
    	stage.getBatch().end();
    }
    
    private void drawIsland() {
    	if(selectedIsland != null) {
            if(selectedIsland == pukru_island) {
            	stage.getBatch().begin();
            	stage.getBatch().draw(selectedIsland, -1190,1090);
            	stage.getBatch().end();
            }else {
            	stage.getBatch().begin();
            	stage.getBatch().draw(selectedIsland, -1290,1050);
            	stage.getBatch().end();
            }	
    	}
    }

    /**
     * Renders all entities
     */
    private void renderEntities() {
        renderer.setProjectionMatrix(othercamera.combined);
        if(blockadeMap != null) {
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
                            stage.getBatch().begin();
                            stage.getBatch().draw(region, xx + offsetX, yy + offsetY);
                            stage.getBatch().end();
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
                        	stage.getBatch().begin();
                        	stage.getBatch().draw(vessel, xx + vessel.getOrientationLocation().getOffsetx(), yy + vessel.getOrientationLocation().getOffsety());
                        	stage.getBatch().end();
                        }


                        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                        othercamera.unproject(v, 0, 200, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 200);

                        float xxx = xx + vessel.getOrientationLocation().getOffsetx();
                        float yyy = yy + vessel.getOrientationLocation().getOffsety();

                        if (v.x>= xxx && v.x <= xxx + vessel.getRegionWidth() && v.y >= yyy && v.y <= yyy + vessel.getRegionHeight()) {

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
                    	stage.getBatch().begin();
                    	stage.getBatch().draw(c, cx, cy);
                    	stage.getBatch().end();
                    }
                    else {
                        cx = getIsometricX(c.getX(), c.getY(), c.getEndingAnimationRegion());
                        cy = getIsometricY(c.getX(), c.getY(), c.getEndingAnimationRegion());
                        stage.getBatch().begin();
                        stage.getBatch().draw(c.getEndingAnimationRegion(), cx, cy);
                        stage.getBatch().end();
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
                    	stage.getBatch().begin();
                    	stage.getBatch().draw(r, cx, cy);
                    	stage.getBatch().end();
                    }
                }
                
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
                GlyphLayout layout = new GlyphLayout(font, vessel.getName());
                stage.getBatch().begin();
                font.draw(stage.getBatch(), vessel.getName(), x + (vessel.getRegionWidth() / 2) - (layout.width / 2), y + vessel.getRegionHeight() + NAME_HEIGHT_ABOVE_SHIP);
                stage.getBatch().end();
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
                    stage.getBatch().begin();
                    stage.getBatch().draw(symbol, startX, flagsY);
                    stage.getBatch().end();
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
                        stage.getBatch().begin();
                        font.draw(stage.getBatch(), "+" + points + " points", x + vessel.getRegionWidth() + 20, y + vessel.getRegionHeight() * 1.7f - (100 - vessel.getScoreDisplayMovement()));
                        stage.getBatch().end();
                    }
                    vessel.tickScoreMovement();
                }
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
        othercamera = null;
    }

    private void renderSeaBattle() {
        // The map tiles
       // GameTile[][] tiles = map.getTiles();
    	if(blockadeMap != null) {
    		Cell[][] sea = blockadeMap.getSea();
            Wind[][] winds = blockadeMap.getWinds();
            Whirlpool[][] whirls = blockadeMap.getWhirls();

            for (int i = 0; i < sea.length; i++) {
                for(int j = 0; j < sea[i].length; j++) {
                    TextureRegion region = sea[i][j].getRegion();
                    int x = (i * GameTile.TILE_WIDTH / 2) - (j * GameTile.TILE_WIDTH / 2) - region.getRegionWidth() / 2;
                    int y = (i * GameTile.TILE_HEIGHT / 2) + (j * GameTile.TILE_HEIGHT / 2) - region.getRegionHeight() / 2;

                    if (canDraw(x, y, GameTile.TILE_WIDTH, GameTile.TILE_HEIGHT)) {
                    	stage.getBatch().begin();
                    	stage.getBatch().draw(region, x, y);
                    	stage.getBatch().end();
                        if (winds[i][j] != null) {
                            region = winds[i][j].getRegion();
                            stage.getBatch().begin();
                            stage.getBatch().draw(region, x, y);
                            stage.getBatch().end();
                        }
                        else if (whirls[i][j] != null) {
                            region = whirls[i][j].getRegion();
                            stage.getBatch().begin();
                            stage.getBatch().draw(region, x, y);
                            stage.getBatch().end();
                        }
                    }
                }
            }
    	}
        
    }

    private boolean canDraw(float x, float y, int width, int height) {
        return x + width >= othercamera.position.x - othercamera.viewportWidth / 2 && x <= othercamera.position.x + othercamera.viewportWidth / 2 &&
                y + height >= othercamera.position.y - othercamera.viewportHeight / 2 && y <= othercamera.position.y + othercamera.viewportHeight / 2;
    }

    public void setTurnExecute() {
        currentSlot = 0;
        currentPhase = MovePhase.MOVE_TOKEN;
        for (Vessel vessel : context.getEntities().listVesselEntities()) {
            vessel.setMovePhase(null);
        }
        recountVessels();
        //lock controls
        context.getControl().setLockedDuringAnimate(true);
        setAnimationOngoing(true);
    }

    public BlockadeMap getMap() {
        return blockadeMap;
    }

    public void initializePlayerCamera(Vessel vessel) {
        cameraFollowsVessel = true; // force reset
	   	othercamera.position.add(getIsometricX(vessel.getX(), vessel.getY(), vessel) - othercamera.position.x, getIsometricY(vessel.getX(), vessel.getY(), vessel) - othercamera.position.y, 0); 
	   	othercamera.update();
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
		            break;
		        case "medium":
		            cannonMediumSound.play(getSound_volume());
		            break;
		        case "small":
		            cannonSmallSound.play(getSound_volume());
		            break;
	    	}
    	}else if(shots == 2) {
        	switch(vessel.getCannonSize()) {
		        case "large":
		        	//doesn't sound like shooting 2, need to figure out how to add delay
		        	cannonBigSound.play(getSound_volume());
		            break;
		        case "medium":
		            cannonMediumSound.play(getSound_volume());
		            break;
		        case "small":
		            cannonSmallSound.play(getSound_volume());
		            break;
		    	}
    		}
    	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if(control.touchDown(x, y, pointer,button)) {
			return true;
		}
    	if(!battleMenu.isClickingMenuButton(x, y) && !battleMenu.isClickingMenuTable(x, y) && !battleMenu.isSettingsDialogOpen()) {//keep from moving when menu button clicked
            if (battleMenu.touchDown(x, y, pointer, button)) {
                return true;
            }
            if (othercamera != null && y < othercamera.viewportHeight && !battleMenu.teamTable.isVisible()) {
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
	public boolean touchUp(int x, int y, int pointer, int button) {	
		if(control.touchUp(x, y, pointer,button)) {
			return true;
		}
		if(!battleMenu.isClickingMenuButton(x, y) && !battleMenu.isClickingMenuTable(x, y) && !battleMenu.isSettingsDialogOpen() ) {//keep from moving when menu button clicked
	        if (battleMenu.touchUp(x, y, pointer,button)) {
	            return true;
	        }
        	if (othercamera != null && y < othercamera.viewportHeight && !battleMenu.teamTable.isVisible()) {
                // handle camera following/not following vessel
                if (button == Input.Buttons.RIGHT) {
                    cameraFollowsVessel = false;
                } else {
                    this.cameraFollowsVessel = true;
                    try {
                        Vessel vessel = context.getEntities().getVesselByName(context.myVessel);
	    	        	 othercamera.position.add(getIsometricX(vessel.getX(), vessel.getY(), vessel) - othercamera.position.x, getIsometricY(vessel.getX(), vessel.getY(), vessel) - othercamera.position.y, 0); 
	    	        	 othercamera.update();
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

	@Override
	public boolean touchDragged(int sx, int sy, int pointer) {
		if(control.touchDragged(sx, sy, pointer)) {
			return false;
		}
        if(!battleMenu.audio_slider.isVisible() && !battleMenu.isSettingsDialogOpen()) {
            if (othercamera != null && sy > othercamera.viewportHeight) {
                return false;
            }
            if (this.canDragMap && sy < Gdx.graphics.getHeight() - 200) {
	        	 float x = Gdx.input.getDeltaX(); float y = Gdx.input.getDeltaY();
	        	 othercamera.position.add(-x*1.3f, y*1.3f, 0); 
	        	 othercamera.update();
            }
            if (battleMenu.touchDragged(sx, sy, pointer)) {
                return true;
            }
        }
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
        if (control.mouseMoved(x, y)) {
            return true;
        }
        if (battleMenu.mouseMoved(x, y)) {
            return true;
        }
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
