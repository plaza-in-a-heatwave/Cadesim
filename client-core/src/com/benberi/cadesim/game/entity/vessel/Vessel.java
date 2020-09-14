package com.benberi.cadesim.game.entity.vessel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.entity.Entity;
import com.benberi.cadesim.game.entity.projectile.CannonBall;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.impl.Baghlah;
import com.benberi.cadesim.game.entity.vessel.impl.Blackship;
import com.benberi.cadesim.game.entity.vessel.impl.Dhow;
import com.benberi.cadesim.game.entity.vessel.impl.Fanchuan;
import com.benberi.cadesim.game.entity.vessel.impl.Grandfrig;
import com.benberi.cadesim.game.entity.vessel.impl.Junk;
import com.benberi.cadesim.game.entity.vessel.impl.Lgsloop;
import com.benberi.cadesim.game.entity.vessel.impl.Longship;
import com.benberi.cadesim.game.entity.vessel.impl.Merchbrig;
import com.benberi.cadesim.game.entity.vessel.impl.Merchgal;
import com.benberi.cadesim.game.entity.vessel.impl.Smsloop;
import com.benberi.cadesim.game.entity.vessel.impl.Warbrig;
import com.benberi.cadesim.game.entity.vessel.impl.Warfrig;
import com.benberi.cadesim.game.entity.vessel.impl.Wargal;
import com.benberi.cadesim.game.entity.vessel.impl.Xebec;
import com.benberi.cadesim.game.entity.vessel.move.MoveAnimationStructure;
import com.benberi.cadesim.game.entity.vessel.move.MovePhase;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.scene.TextureCollection;
import com.benberi.cadesim.game.scene.impl.battle.map.GameObject;
import com.benberi.cadesim.game.scene.impl.battle.map.tile.impl.BigRock;
import com.benberi.cadesim.util.OrientationLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a vessel abstraction
 */
public abstract class Vessel extends Entity {

    public static final Color DEFAULT_BORDER_COLOR = new Color(0.35294117647f, 0.67450980392f, 0.87058823529f, 1);

    /**
     * The name of this vessel player
     */
    private String name;

    /**
     * If the vessel is moving
     */
    private boolean isMoving;

    /**
     * The rotation index of the vessel
     */
    private int rotationIndex;

    /**
     * The target index for the rotation in animation
     */
    private int rotationTargetIndex = -1;

    /**
     * The animation handler
     */
    private VesselAnimationVector animation;

    /**
     * Current performing move
     */
    private VesselMovementAnimation currentPerformingMove;

    /**
     * The turn animation structure
     */
    private MoveAnimationStructure structure = new MoveAnimationStructure();

    /**
     * The team
     */
    private Team team;

    private int scoreDisplayMovement;

    /**
     * The last finished phase
     */
    private MovePhase finishedPhase;

    private int numberOfMoves;

    protected TextureRegion shootSmoke;

    private boolean isSmoking;

    private int smokeTicks;

    private boolean isSinking;

    private boolean isSinkingTexture;

    private int sinkingTicks;

    private boolean isBumping;
    private VesselBumpVector bumpVector;

    private List<FlagSymbol> flags = new ArrayList<>();

    /**
     * The cannon balls that were shoot
     */
    private List<CannonBall> cannonballs = new ArrayList<CannonBall>();
    private int moveDelay;
    private boolean bumpReached;
    public boolean sinkingAnimationFinished = true;

    public Vessel(GameContext context, String name, int x, int y) {
        super(context);
        this.name = name;
        this.setPosition(x, y);
    }

    public void setBumpReached(boolean bump) {
        this.bumpReached = bump;
        bumpVector = new VesselBumpVector(bumpVector.getEnd(), bumpVector.getStart(), bumpVector.getMove());
    }

    public void setMovePhase(MovePhase phase) {
        this.finishedPhase = phase;
    }

    public MovePhase getMovePhase() {
        return finishedPhase;
    }

    public void setScoreDisplayMovement() {
        this.scoreDisplayMovement = 100;
    }

    public boolean hasScoreDisplay() {
        return this.scoreDisplayMovement > -1;
    }

    public int getScoreDisplayMovement() {
        return this.scoreDisplayMovement;
    }

    public MoveAnimationStructure getStructure() {
        return structure;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(int moves) {
        this.numberOfMoves = moves;
    }

    public boolean isSinking() {
        return isSinking;
    }

    public boolean isSinkingTexture() {
        return isSinkingTexture;
    }

    public boolean isSinkingAnimationFinished() {
        return sinkingAnimationFinished;
    }

    public VesselBumpVector getBumpVector() {
        return bumpVector;
    }

    public int getSinkingTicks() {
        return sinkingTicks;
    }

    /**
     * Starts to perform a given move
     * @param move The move to perform
     */
    public void performMove(VesselMovementAnimation move) {
        Vector2 start = new Vector2(this.getX(), this.getY());
        Vector2 currentAnimationLocation = start.cpy();
        this.currentPerformingMove = move;

        Vector2 inbetween = null;
        if (move != VesselMovementAnimation.MOVE_FORWARD) {
            if (!move.isWhirlpoolMove()) {
                // Get the inbetween block by using forward
                inbetween = new Vector2(start.x + VesselMovementAnimation.MOVE_FORWARD.getIncrementXForRotation(rotationIndex),
                        start.y + VesselMovementAnimation.MOVE_FORWARD.getIncrementYForRotation(rotationIndex));
            }
            else {
                inbetween = move.getInbetweenWhirlpool(start);
            }
            this.rotationTargetIndex = move.getRotationTargetIndex(rotationIndex);
        }

        Vector2 end = new Vector2(start.x + move.getIncrementXForRotation(rotationIndex),
                start.y + move.getIncrementYForRotation(rotationIndex));

        Vector2 linear = start.cpy();
        this.animation = new VesselAnimationVector(start, inbetween, end, currentAnimationLocation, linear);
        setMoving(true);
    }

    public void performBump(MoveType move, VesselMovementAnimation animation) {
        this.isBumping = true;
        this.isMoving = true;
        currentPerformingMove = animation;
        Vector2 target = animation.getBumpTargetPosition(rotationIndex);
        bumpVector = new VesselBumpVector(new Vector2(getX(), getY()), new Vector2(getX() + target.x, getY() + target.y), move);
        tickBumpRotation(1);
    }

    @SuppressWarnings("incomplete-switch")
	public void tickBumpRotation(int amount) {
        switch (bumpVector.getMove()) {
            case LEFT:
                if (rotationIndex - amount < 0) {
                    setRotationIndex(16 - Math.abs(rotationIndex - amount));
                }
                else {
                    setRotationIndex(rotationIndex - amount);
                }
                break;
            case RIGHT:
                if (rotationIndex + amount > 15) {
                    setRotationIndex((rotationIndex + amount) - 16);
                }
                else {
                    setRotationIndex(getRotationIndex() + amount);
                }
                break;
        }
    }

    public boolean isBumping() {
        return this.isBumping;
    }

    public void setBumping(boolean bump) {
        this.isBumping = false;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSmoking() {
        return this.isSmoking;
    }

    public void tickMoveDelay() {
        moveDelay -= 100 * Gdx.graphics.getDeltaTime();
        if (moveDelay <= 0) {
            moveDelay = -1;
        }
    }

    public boolean hasDelay() {
        return moveDelay > -1;
    }

    public int getMoveDelay() {
        return this.moveDelay;
    }

    public void clearFlags() {
        if (flags != null) {
            flags.clear();
        }
    }

    public List<FlagSymbol> getFlags() {
        return this.flags;
    }

    public void tickSmoke() {
        if (smokeTicks >= 5) {
            shootSmoke.setRegion(shootSmoke.getRegionX() + 40, 0, 40, 30);
            if (shootSmoke.getRegionX() > shootSmoke.getTexture().getWidth()) {
                isSmoking = false;
                shootSmoke.setRegion(0, 0, 40, 30);
            }
            smokeTicks = 0;
        }
        else {
            smokeTicks += 100 * Gdx.graphics.getDeltaTime();
        }
    }

    /**
     * Gets the animation handler for vessel
     * @return {@link #animation}
     */
    public VesselAnimationVector getAnimation() {
        return this.animation;
    }

    /**
     * Gets the current performing move
     * @return {@link #currentPerformingMove}
     */
    public VesselMovementAnimation getCurrentPerformingMove() {
        return this.currentPerformingMove;
    }

    /**
     * @return The target rotation index for animation
     */
    public int getRotationTargetIndex() {
        return this.rotationTargetIndex;
    }
    /**
     * If the ship currently performing move animation or not
     * @param flag If moving or not
     */
    public void setMoving(boolean flag) {
        this.isMoving = flag;
    }

    /**
     * If the ship is moving or not
     * @return TRUE if moving FALSE if not
     */
    public boolean isMoving() {
        return this.isMoving;
    }

    /**
     * Gets the current rotation index
     * @return {{@link #rotationIndex}}
     */
    public int getRotationIndex() {
        return this.rotationIndex;
    }

    public TextureRegion getShootSmoke() {
        return shootSmoke;
    }

    /**
     * Ticks up to next rotation
     */
    public void tickRotation() {
        if (rotationIndex == rotationTargetIndex) {
            return;
        }
        if (currentPerformingMove == VesselMovementAnimation.TURN_LEFT) {
            this.rotationIndex--;
        }
        else if (currentPerformingMove == VesselMovementAnimation.TURN_RIGHT || currentPerformingMove.isWhirlpoolMove()){
            this.rotationIndex++;
        }

        if (rotationIndex > 15) {
            rotationIndex = 0;
        }
        else if (rotationIndex < 0) {
            rotationIndex = 15;
        }


        this.updateRotation();
    }

    /**
     * Sets rotation index
     * @param index The new index
     */
    public void setRotationIndex(int index) {
        this.rotationIndex = index;
        this.updateRotation();
    }


    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    /**
     * Updates sprite region to new rotation
     */
    private void updateRotation() {
        this.setOrientationLocation(this.rotationIndex);
        OrientationLocation location = this.getOrientationLocation();
        try {
            this.setRegion(location.getX(), location.getY(), location.getWidth(), location.getHeight());
        }
        catch(NullPointerException e) {
            System.err.println(rotationIndex + " " + rotationTargetIndex);
        }
    }

    private Vector2 getClosestLeftCannonCollide() {
        switch (rotationIndex) {
            case 2:
                for (int i = 1; i < 4; i++) {
                    float x = getX();
                    float  y = getY() + i;
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX(), getY() + 3);
            case 6:
                for (int i = 1; i < 4; i++) {
                    float x = getX() + i;
                    float  y = getY();
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX() + 3, getY());
            case 10:
                for (int i = 1; i < 4; i++) {
                    float x = getX();
                    float  y = getY() - i;
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX(), getY() - 3);
            case 14:
                for (int i = 1; i < 4; i++) {
                    float x = getX() - i;
                    float  y = getY();
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX() - 3, getY());
        }
        return new Vector2(getX(), getY());
    }


    public Vector2 getClosestRightCannonCollide() {
        switch (rotationIndex) {
            case 2:
                for (int i = 1; i < 4; i++) {
                    float x = getX();
                    float  y = getY() - i;
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX(), getY() - 3);
            case 6:
                for (int i = 1; i < 4; i++) {
                    float x = getX() - i;
                    float  y = getY();
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX() - 3, getY());
            case 10:
                for (int i = 1; i < 4; i++) {
                    float x = getX();
                    float  y = getY() + i;
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX(), getY() + 3);
            case 14:
                for (int i = 1; i < 4; i++) {
                    float x = getX() + i;
                    float  y = getY();
                    Vessel vessel = getContext().getEntities().getVesselByPosition(x, y);
                    GameObject object = getContext().getBattleScene().getMap().getObject(x, y);
                    if (vessel != null || object != null && object instanceof BigRock) {
                        return new Vector2(x, y);
                    }
                }
                return new Vector2(getX() + 3, getY());
        }
        return new Vector2(getX(), getY());
    }

    public abstract float getInfluenceRadius();

    public abstract CannonBall createCannon(GameContext ctx, Vessel source, Vector2 target);

    public abstract VesselMoveType getMoveType(); // 4 moves or 3?
    public abstract boolean isDoubleShot();       // 2 shots or 1?

    public abstract void setDefaultTexture();
    public abstract void setSinkingTexture();

    public List<CannonBall> getCannonballs() {
        return this.cannonballs;
    }

    public void performLeftShoot(int leftShoots) {
        if (leftShoots == 1) {
            Vector2 target = getClosestLeftCannonCollide();
            CannonBall ball = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null || getContext().getBattleScene().getMap().getObject(target.x, target.y) instanceof BigRock) {
                ball.setExplodeOnReach(true);
            }
            cannonballs.add(ball);
        }
        else if (leftShoots == 2) {
            Vector2 target = getClosestLeftCannonCollide();

            CannonBall ball1 = createCannon(getContext(), this, target);
            CannonBall ball2 = createCannon(getContext(), this, target);

            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null || getContext().getBattleScene().getMap().getObject(target.x, target.y) instanceof BigRock) {
                ball1.setExplodeOnReach(true);
                ball2.setExplodeOnReach(true);
            }
            ball2.setReleased(false);

            ball1.setSubcannon(ball2);
            cannonballs.add(ball1);
            cannonballs.add(ball2);
        }
        shootSmoke.setRegion(0, 0, 40, 30);
        isSmoking = true;

    }

    public void performRightShoot(int rightShoots) {
        if (rightShoots == 1) {
            Vector2 target = getClosestRightCannonCollide();
            CannonBall ball = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball.setExplodeOnReach(true);
            }
            cannonballs.add(ball);
        }
        else if (rightShoots == 2) {
            Vector2 target = getClosestRightCannonCollide();

            CannonBall ball1 = createCannon(getContext(), this, target);
            CannonBall ball2 = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball1.setExplodeOnReach(true);
                ball2.setExplodeOnReach(true);
            }
            ball2.setReleased(false);

            ball1.setSubcannon(ball2);
            cannonballs.add(ball1);
            cannonballs.add(ball2);
        }
        shootSmoke.setRegion(0, 0, 40, 30);
        isSmoking = true;
    }

    public void setSinking(boolean sinking) {
        this.isSinking = sinking;
        if (!isSinking) {
            setDefaultTexture();
            isSinkingTexture = false;
            sinkingAnimationFinished = true;
        }
        else {
            sinkingAnimationFinished = false;
        }
    }

    public void setMoveDelay() {
        this.moveDelay = 10;
    }


    public void tickNonSinkingTexture() {
        if (sinkingTicks == 6) {
            int next = rotationIndex - 1;
            if (next < 0) {
                next = 14;
            }
            setRotationIndex(next);
            if (next == 8) {
                setSinkingTexture();
                setRotationIndex(0);
                isSinkingTexture = true;
            }
            sinkingTicks = 0;
        }
        else {
            sinkingTicks++;
        }
    }

    public void tickSinkingTexture() {
        if (sinkingTicks == 5) {
            if (rotationIndex + 1 >= this.getOrientationPack().getAllOrientations().size()) {
                sinkingAnimationFinished = true;
                return;
            }
            setRotationIndex(rotationIndex + 1);
            sinkingTicks = 0;
        }
        else {
            sinkingTicks++;
        }
    }

    public boolean isBumpReached() {
        return bumpReached;
    }

    public void disposeBump() {
        isBumping = false;
        bumpVector = null;
        bumpReached = false;
        isMoving = false;
        setMoveDelay();
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return this.team;
    }

    protected Texture getVesselTexture(String v) {
        Texture t = getContext().getTextures().getVessel(v);
        if (getContext().myVessel.equals(this.name) || getContext().myTeam.getID() == getTeam().getID()) {
            return t;
        }
        return TextureCollection.prepareTextureForTeam(t, getTeam());
    }

    public void tickScoreMovement() {
        this.scoreDisplayMovement--;
    }
    
    public static Vessel createVesselByType(GameContext context, String name, int x, int y, int type) {
        try
        {
        	Class<?> shiptype = (VESSEL_TYPES.get(type));
            Constructor<?> c = shiptype.getConstructor(GameContext.class, String.class, int.class, int.class);
            return (Vessel)c.newInstance(context, name, x, y);
        }
        catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e)
        {
        	System.out.println("in createVesselByType(" + type + "), caught " + e.getClass());
        	return null;
        }
    }
    
    /**
     * map integer IDs to classes
     */
    public static final HashMap<Integer, Class<? extends Vessel>> VESSEL_TYPES = new HashMap<Integer, Class<? extends Vessel>>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
    	put(0, Smsloop.class);
    	put(1, Lgsloop.class);
    	put(2, Dhow.class);
    	put(3, Fanchuan.class);
    	put(4, Longship.class);
    	put(5, Junk.class);
    	put(6, Baghlah.class);
    	put(7, Merchbrig.class);
    	put(8, Warbrig.class);
    	put(9, Xebec.class);
    	put(10, Merchgal.class);
    	put(11, Warfrig.class);
    	put(12, Wargal.class);
    	put(13, Grandfrig.class);
    	put(14, Blackship.class);
    }};
    
    /**
     * @param name the name to search
     * @return id, or -1 if not found
     */
    public static int getIdFromName(String name) {
    	for (int i : VESSEL_TYPES.keySet())
    	{
    		try
    		{
    			Class<?> shiptype_field = VESSEL_TYPES.get(i);
    			Field vesselname_field = shiptype_field.getField("VESSELNAME");
    			String value = (String)vesselname_field.get(shiptype_field);
        		if (value.equals(name))
        		{
        			return i;
        		}
    		}
    		catch(NoSuchFieldException | IllegalAccessException e)
    		{
    			System.out.println("in getIdFromName(" + name + "), caught " + e.getMessage());
    			return -1;
    		}
    		
    	}
    	return -1;
    }
    
//    public static Class getClassFromPlayerName(String name) {
//    	for (int i : VESSEL_TYPES.keySet())
//    	{
//    		try
//    		{
//    			Class shiptype_class = VESSEL_TYPES.get(i);
//    			java.lang.reflect.Method vesselname_method = shiptype_class.getMethod("getName", new Class[] {});
//    			System.out.println("found vesselname method: " + vesselname_method);
//    			String ret = (String)vesselname_method.invoke(null, new Object[] {});
//        		if (ret.equals(name))
//        		{
//        			return shiptype_class;
//        		}
//    		}
//    		catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
//    		{
//    			System.out.println("in getClassFromPlayerName(" + name + "), caught " + e.getMessage());
//    			return null;
//    		}
//    		
//    	}
//    	return null;
//    }
    
    /**
     * @param id the id to search
     * @return name, or null if not found
     */
    public static String getNameFromId(int id) {
		try
		{
			Class<?> shiptype_field = VESSEL_TYPES.get(id);
			Field vesselname_field = shiptype_field.getField("VESSELNAME");
			return (String)vesselname_field.get(shiptype_field);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			System.out.println("in getNameFromId(" + id + "), caught " + e.getMessage());
			return null;
		}
    }
    
    
}
