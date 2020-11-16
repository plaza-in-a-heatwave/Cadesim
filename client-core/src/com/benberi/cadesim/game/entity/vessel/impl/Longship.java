package com.benberi.cadesim.game.entity.vessel.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.projectile.CannonBall;
import com.benberi.cadesim.game.entity.projectile.impl.SmallCannonball;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.VesselMoveType;
import com.benberi.cadesim.util.PackedObjectOrientation;

public class Longship extends Vessel {
	public static final String VESSELNAME = "longship";

	private static String cannonsize = "small";
	
    public Longship(GameContext context, String name, int x, int y) {
        super(context, name, x, y);
    }

    @Override
    public void create() {
        try {
            setDefaultTexture();
            this.shootSmoke = new TextureRegion(getContext().getTextures().getMisc("explode_small"));
            shootSmoke.setRegion(0,0,40, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getInfluenceRadius() {
        return 1f;
    }

    @Override
    public CannonBall createCannon(GameContext ctx, Vessel source, Vector2 target) {
        return new SmallCannonball(ctx, source, target, getContext().getTextures().getMisc("splash_small"),
                getContext().getTextures().getMisc("hit"));
    }
    
    @Override
    public String getCannonSize() {
    	return cannonsize;
    }
    
    @Override
    public VesselMoveType getMoveType() {
        return VesselMoveType.FOUR_MOVES;
    }
    
    @Override
    public boolean isDoubleShot() {
    	return true;
    }

    @Override
    public void setDefaultTexture() {
        this.setTexture(getVesselTexture("longship"));
        this.setOrientationPack(getContext().getTools().getGson().fromJson(
                Gdx.files.internal("vessel/longship/sail.json").readString(),
                PackedObjectOrientation.class));
    }

    @Override
    public void setSinkingTexture() {
        this.setTexture(getVesselTexture("longship_sinking"));
        this.setOrientationPack(getContext().getTools().getGson().fromJson(
                Gdx.files.internal("vessel/longship/sink.json").readString(),
                PackedObjectOrientation.class));
    }
}
