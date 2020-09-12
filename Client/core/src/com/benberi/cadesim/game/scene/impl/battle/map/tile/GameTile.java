package com.benberi.cadesim.game.scene.impl.battle.map.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.OrientationLocation;
import com.benberi.cadesim.util.PackedObjectOrientation;

/**
 * Abstract design for a game tile
 */
public abstract class GameTile {

    /**
     * Tile dimensions WIDTH
     */
    public static final int TILE_WIDTH = 64;

    /**
     * Tile dimensions HEIGHT
     */
    public static final int TILE_HEIGHT = 48;

    private GameContext context;

    /**
     * The texture of the tile
     */
    private Texture texture;

    /**
     * The texture region
     */
    private TextureRegion region;

    private PackedObjectOrientation orientation;

    private OrientationLocation orientationLocation;

    public GameTile(GameContext context) {
        this.context = context;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.region = new TextureRegion(texture, 0, 0, TILE_WIDTH, TILE_HEIGHT);
    }

    /**
     * Gets the tile texture
     * @return {@link #texture}
     */
    public Texture getTexture() {
        return this.texture;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setPackedObjectOrientation(String pack) {
        this.orientation = context.getTools().getGson().fromJson(
                Gdx.files.internal("assets/objects/" + pack + ".json").readString(),
                PackedObjectOrientation.class);
    }

    public void setOrientation(int index) {
        orientationLocation = orientation.getOrienation(index);
        region = new TextureRegion(texture, orientationLocation.getX(), orientationLocation.getY(), orientationLocation.getWidth(), orientationLocation.getHeight());
    }

    public OrientationLocation getOrientationLocation() {
        return orientationLocation;
    }
}
