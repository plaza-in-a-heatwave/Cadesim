package com.benberi.cadesim.game.entity.vessel.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.projectile.CannonBall;
import com.benberi.cadesim.game.entity.projectile.impl.MediumCannonball;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.entity.vessel.VesselMoveType;
import com.benberi.cadesim.util.PackedObjectOrientation;

public class Dhow extends Vessel {
	public static final String VESSELNAME = "dhow";

	private static String cannonsize = "medium";
	
    public Dhow(GameContext context, String name, int x, int y) {
        super(context, name, x, y);
    }

    @Override
    public void create() {
        try {
            setDefaultTexture();
            this.shootSmoke = new TextureRegion(getContext().getTextures().getMisc("explode_medium"));
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
        return new MediumCannonball(ctx, source, target, getContext().getTextures().getMisc("splash_small"),
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
    	return false;
    }

    @Override
    public void setDefaultTexture() {
        this.setTexture(getVesselTexture("dhow"));
        this.setOrientationPack(getContext().getTools().getGson().fromJson(
                Gdx.files.internal("vessel/dhow/sail.json").readString(),
                PackedObjectOrientation.class));
    }

    @Override
    public void setSinkingTexture() {
        this.setTexture(getVesselTexture("dhow_sinking"));
        this.setOrientationPack(getContext().getTools().getGson().fromJson(
                Gdx.files.internal("vessel/dhow/sink.json").readString(),
                PackedObjectOrientation.class));
    }
}
