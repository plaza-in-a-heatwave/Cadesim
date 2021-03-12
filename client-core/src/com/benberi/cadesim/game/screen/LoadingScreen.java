package com.benberi.cadesim.game.screen;

import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.AbstractScreen;
import com.benberi.cadesim.util.LoadingBar;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;

public class LoadingScreen extends AbstractScreen{
	private BitmapFont font;
	private GameContext context;
	private String text;
	private Texture clientlogo;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;
    
    private float startX, endX;
    private float percent;
    private long loginAttemptTimestampMillis;
    private boolean isConnected = false;
    private Actor loadingBar;
	
	public LoadingScreen(GameContext context, String text) {
		this.context = context;
		this.text = text;
	}
	private AsyncExecutor executor = new AsyncExecutor(4);
	private AsyncResult<Void> connectTask;
	@Override
	public void buildStage() {
		font = context.getManager().get(context.getAssetObject().regularFont);
		clientlogo = context.getManager().get(context.getAssetObject().clientlogo);
		
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				connectTask = executor.submit(new AsyncTask<Void>() {
			         public Void call() {
			            try {
							Thread.sleep(1200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
			            return null;
			         }
			    });
			}
		});
	}
	
    @Override
    public void show() {
        // Tell the manager to load assets for the loading screen
    	context.getManager().load("loading.pack", TextureAtlas.class);
    	context.loadGameAssets();
        // Wait until they are finished loading
        context.getManager().finishLoading();

        // Get our textureatlas from the manager
        TextureAtlas atlas = context.getManager().get("loading.pack", TextureAtlas.class);

        // Grab the regions from the atlas and create some images
        logo = new Image(clientlogo);
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation<?> anim = new Animation<Object>(0.05f, atlas.findRegions("loading-bar-anim") );
        anim.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
        logo.toFront();
        loginAttemptTimestampMillis = System.currentTimeMillis();
    }

    @Override
    public void resize(int width, int height) {
        // Set our screen to always be XXX x 480 in size
    	stage.getViewport().update(width, height,true);
        // Make the background fill the screen
        screenBg.setSize(width, height);
        
        // Place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 100);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if (connectTask != null && connectTask.isDone() && context.getManager().update() && !isConnected) {
			isConnected = true;
			try {
				context.connect(context.getAccountName(), context.getAccountPass(), context.getVesselName(), context.getHostURL(), context.getVesselType(), context.getTeam());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
        if(System.currentTimeMillis() - loginAttemptTimestampMillis >= 8000) {
            if(!(ScreenManager.getScreen() instanceof SeaBattleScreen)) {
            	Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
                    	ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
                    	context.getLobbyScreen().setStatusMessage("Client: " + "Unable to connect; please retry.");
        			}
            		
            	});
            }	
        }
        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, context.getManager().getProgress(), 0.1f);
        
		if(context.getManager().update()) {
	        context.getTextures().loadSeaBattleTextures();
		}
        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();
        stage.getBatch().begin();
        font.setColor(Color.BLACK);
        font.draw(stage.getBatch(), text, (Gdx.graphics.getWidth()/2 - text.length() * 3),Gdx.graphics.getHeight()/2 + 10);
        stage.getBatch().end();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
    	context.getManager().unload("loading.pack");
    }
    
    public void dispose() {
    	super.dispose();
    }
}
