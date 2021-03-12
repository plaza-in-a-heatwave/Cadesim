package com.benberi.cadesim.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.benberi.cadesim.GameContext;

public class TextureCollection {

    /**
     * Sea tile textures
     */
    private Map<String, Texture> seaTiles = new HashMap<String, Texture>();

    /**
     * Vessel spritesheet textures
     */
    private Map<String, Texture> vessels = new HashMap<String, Texture>();

    private Map<String, Texture> misc = new HashMap<String, Texture>();

    private GameContext context;

    public TextureCollection(GameContext context) {
        this.context = context;
    }

    /**
     * Creates all textures
     */
    public void loadSeaTextures() {
        createSeaTiles();
    }
    
    public void loadSeaBattleTextures() {
        createVessels();
        createMisc();
    }

    /**
     * Gets a sea tile
     * @param index The index
     * @return  A texture of the tile
     */
    public Texture getSeaTile(String index) {
        return seaTiles.get(index);
    }

    /**
     * Gets a vessel spritesheet texture
     * @param index The index
     * @return A texture of the vessel spritesheet
     */
    public Texture getVessel(String index) {
        return vessels.get(index);
    }

    /**
     * Gets misc spritesheet texture
     * @param index The index
     * @return A misc texture
     */
    public Texture getMisc(String index) {
        return misc.get(index);
    }

    /**
     * Creates vessel textures
     */
    private void createVessels() {
    	// sail textures
    	vessels.put("baghlah", context.getManager().get(context.getAssetObject().baghlah));
    	vessels.put("blackship", context.getManager().get(context.getAssetObject().blackship));
    	vessels.put("dhow", context.getManager().get(context.getAssetObject().dhow));
    	vessels.put("fanchuan", context.getManager().get(context.getAssetObject().fanchuan));
    	vessels.put("grandfrig", context.getManager().get(context.getAssetObject().grandfrig));
    	vessels.put("junk", context.getManager().get(context.getAssetObject().junk));
    	vessels.put("lgsloop", context.getManager().get(context.getAssetObject().lgsloop));
    	vessels.put("longship", context.getManager().get(context.getAssetObject().longship));
    	vessels.put("merchbrig", context.getManager().get(context.getAssetObject().merchbrig));
    	vessels.put("merchgal", context.getManager().get(context.getAssetObject().merchgal));
    	vessels.put("smsloop", context.getManager().get(context.getAssetObject().smsloop));
    	vessels.put("warbrig", context.getManager().get(context.getAssetObject().warbrig));
    	vessels.put("warfrig", context.getManager().get(context.getAssetObject().warfrig));
    	vessels.put("wargal", context.getManager().get(context.getAssetObject().wargal));
    	vessels.put("xebec", context.getManager().get(context.getAssetObject().xebec));

    	// sink textures (the Black Ship doesn't sink!)
    	vessels.put("baghlah_sinking", context.getManager().get(context.getAssetObject().baghlah_sinking));
    	vessels.put("dhow_sinking", context.getManager().get(context.getAssetObject().dhow_sinking));
    	vessels.put("fanchuan_sinking", context.getManager().get(context.getAssetObject().fanchuan_sinking));
    	vessels.put("grandfrig_sinking", context.getManager().get(context.getAssetObject().grandfrig_sinking));
    	vessels.put("junk_sinking", context.getManager().get(context.getAssetObject().junk_sinking));
    	vessels.put("lgsloop_sinking", context.getManager().get(context.getAssetObject().lgsloop_sinking));
    	vessels.put("longship_sinking", context.getManager().get(context.getAssetObject().longship_sinking));
    	vessels.put("merchbrig_sinking", context.getManager().get(context.getAssetObject().merchbrig_sinking));
    	vessels.put("merchgal_sinking", context.getManager().get(context.getAssetObject().merchgal_sinking));
    	vessels.put("smsloop_sinking", context.getManager().get(context.getAssetObject().smsloop_sinking));
    	vessels.put("warbrig_sinking", context.getManager().get(context.getAssetObject().warbrig_sinking));
    	vessels.put("warfrig_sinking", context.getManager().get(context.getAssetObject().warfrig_sinking));
    	vessels.put("wargal_sinking", context.getManager().get(context.getAssetObject().wargal_sinking));
    	vessels.put("xebec_sinking", context.getManager().get(context.getAssetObject().xebec_sinking));
    }

    /**
     * Creates sea tiles textures
     */
    private void createSeaTiles() {
        seaTiles.put("cell", context.getManager().get(context.getAssetObject().cell));
        seaTiles.put("safe", context.getManager().get(context.getAssetObject().safe));
    }

    private void createMisc() {
        misc.put("cannonball_large", context.getManager().get(context.getAssetObject().cannonball_large));
        misc.put("cannonball_medium", context.getManager().get(context.getAssetObject().cannonball_medium));
        misc.put("cannonball_small", context.getManager().get(context.getAssetObject().cannonball_small));
        misc.put("splash_large", context.getManager().get(context.getAssetObject().splash_large));
        misc.put("splash_small", context.getManager().get(context.getAssetObject().splash_small));
        misc.put("explode_large", context.getManager().get(context.getAssetObject().explode_large));
        misc.put("explode_medium", context.getManager().get(context.getAssetObject().explode_medium));
        misc.put("explode_small"  , context.getManager().get(context.getAssetObject().explode_small));
        misc.put("hit", context.getManager().get(context.getAssetObject().hit));
    }
    
    public void dispose() {
    	seaTiles.clear();
    	misc.clear();
    	vessels.clear();
    }

    public static Texture prepareTextureForTeam(Texture texture, Team team) {
        // The texture data
        TextureData data = texture.getTextureData();

        // Make sure its prepared
        if (!data.isPrepared()) {
            data.prepare();
        }

        // Our pixmap
        Pixmap pixmap = data.consumePixmap();

        // Loop through all pixels
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {

                // The current color in the given position
                int color = pixmap.getPixel(x, y);

                // RGBA conversion from int
                int R = ((color & 0xff000000) >>> 24);
                int G = ((color & 0x00ff0000) >>> 16);
                int B = ((color & 0x0000ff00) >>> 8);
                int A = ((color & 0x000000ff));

                // Chec
                if (R == 90 && G == 172 && B == 222 && A == 255) {
                    pixmap.drawPixel(x, y, Color.rgba8888(team.getColor()));
                }

            }
        }
        return new Texture(pixmap);
    }
    
    public static Texture prepareAltTextureForTeam(Texture texture, Team team) {
        // The texture data
        TextureData data = texture.getTextureData();

        // Make sure its prepared
        if (!data.isPrepared()) {
            data.prepare();
        }

        // Our pixmap
        Pixmap pixmap = data.consumePixmap();

        // Loop through all pixels
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {

                // The current color in the given position
                int color = pixmap.getPixel(x, y);

                // RGBA conversion from int
                int R = ((color & 0xff000000) >>> 24);
                int G = ((color & 0x00ff0000) >>> 16);
                int B = ((color & 0x0000ff00) >>> 8);
                int A = ((color & 0x000000ff));

                // Chec
                if (R == 90 && G == 172 && B == 222 && A == 255) {
                    pixmap.drawPixel(x, y, Color.rgba8888(team.getAltColor()));
                }

            }
        }
        return new Texture(pixmap);
    }
}
