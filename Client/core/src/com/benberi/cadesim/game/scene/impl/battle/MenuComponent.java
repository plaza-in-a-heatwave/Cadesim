package com.benberi.cadesim.game.scene.impl.battle;

import java.awt.Rectangle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.SceneComponent;
import com.benberi.cadesim.game.scene.impl.connect.ResolutionTypeLabel;
import com.benberi.cadesim.game.scene.impl.control.ControlAreaScene;

public class MenuComponent extends SceneComponent<SeaBattleScene> implements InputProcessor {
    /**
     * The context
     */
    private GameContext context;
    /**
     * Batch renderer for sprites and textures
     */
    private SpriteBatch batch;

    /**
     * Textures
     */
    private Texture menuUp;
    private Texture menuDown;
    private Texture lobbyUp;
    private Texture lobbyDown;
    private Texture mapUp;
    private Texture mapDown;
    
    BitmapFont font;
    
    // reference coords - menu control
    private int MENU_REF_X       = 0;
    private int MENU_REF_Y       = 0;
    private int MENU_buttonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 36);
    private int MENU_buttonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 238);

    private int MENU_tableX     = MENU_REF_X + (Gdx.graphics.getWidth() - 80);
    private int MENU_tableY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 321);
    
    private int MENU_lobbyButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_lobbyButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 273);
    
    private int MENU_mapsButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_mapsButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 308);
    
	
    // DISENGAGE shapes
    Rectangle menuButton_Shape   = new Rectangle(MENU_buttonX, 13, 25, 25);
    Rectangle menuTable_Shape   = new Rectangle(MENU_tableX, 30, 80, 80);
    Rectangle menuLobby_Shape   = new Rectangle(MENU_lobbyButtonX, 50, 70, 22);
    Rectangle menuMap_Shape   = new Rectangle(MENU_mapsButtonX, 80, 70, 22);

    /**
     * state of buttons. true if pushed, false if not.
     */
    private boolean menuButtonIsDown = false; // initial
	private boolean menuLobbyIsDown = false; // initial
	private boolean menuMapsIsDown = false; // initial
    
	public Stage stage;
	private SelectBox<String> selectBox;
	private InputProcessor input;
	private Table table;
	private Dialog dialog;
	private OrthographicCamera orthoCamera;
	public Skin skin;
	private String[] mapStrings;
	Texture texture;
	
    protected MenuComponent(GameContext context, SeaBattleScene owner) {
        super(context, owner);
        this.context = context;
    }

    @Override
    public void create() {
    	stage = new Stage();
        batch = new SpriteBatch();
        menuUp = context.getManager().get(context.getAssetObject().menuUp);
        menuDown = context.getManager().get(context.getAssetObject().menuDown);
        lobbyUp = context.getManager().get(context.getAssetObject().lobbyUp);
        lobbyDown = context.getManager().get(context.getAssetObject().lobbyDown);
        mapUp = context.getManager().get(context.getAssetObject().mapsUp);
        mapDown = context.getManager().get(context.getAssetObject().mapsDown);
        font = context.getManager().get(context.getAssetObject().menuFont);
    
        skin = new Skin(Gdx.files.internal("uiskin.json"));
    }
    
    @Override
    public void update() {
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw((menuButtonIsDown)?menuDown:menuUp, MENU_buttonX, MENU_buttonY);
        if(menuButtonIsDown) {
        	batch.draw((menuLobbyIsDown)?lobbyDown:lobbyUp, MENU_lobbyButtonX, MENU_lobbyButtonY);
        	font.draw(batch,"Lobby",MENU_lobbyButtonX+25,MENU_lobbyButtonY+21);
        	batch.draw((menuMapsIsDown)?mapDown:mapUp, MENU_mapsButtonX, MENU_mapsButtonY);
        	font.draw(batch,"Change Map",MENU_mapsButtonX+15,MENU_mapsButtonY+21);
        }
        batch.end();
        
        stage.act();
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void dispose() {
    }


    @Override
    public boolean handleClick(float x, float y, int button) {
    	if ((!menuButtonIsDown) && isClickingMenuButton(x,y)) {
            menuButtonIsDown = true;
            return true;
        }
        else if(menuButtonIsDown && !isClickingMenuTable(x,y)){
        	menuButtonIsDown = false;
        	menuLobbyIsDown = false;
        	menuMapsIsDown = false;
        	if(dialog != null) {
        		dialog.setVisible(true);
        	}
        	return false;
        }
    	else if(menuButtonIsDown && isClickingLobbyButton(x,y)) {
    		menuLobbyIsDown = true;
    		context.disconnect();
    		return true;
    	}
    	else if(menuButtonIsDown && isClickingMapsButton(x,y)) {
    		menuMapsIsDown = true;
            mapStrings = new String[context.getMaps().size()]; 
        	for(int j =0;j<context.getMaps().size();j++){
        		mapStrings[j] = context.getMaps().get(j);
        	}
            dialog = new Dialog("Map Selection", skin, "dialog") {
    			protected void result(Object object)
                {
    				//if 'No' is pushed
    				if (object.equals(2L))
    			    {
    					dialog.setVisible(false);
    					Gdx.input.setInputProcessor(input);
    			    } else if(object.equals(1L)){
    			    	dialog.setVisible(false);
    			    	Gdx.input.setInputProcessor(input);
    			    	String mapCommand = String.format("/propose changemap %s.txt",selectBox.getSelected());
    			    	context.getControlScene().getBnavComponent().getChatBar().getTextfield().setText(
    			    			mapCommand);
    		    		context.getControlScene().getBnavComponent().getChatBar().sendChat();
    			    }
                }
    		};
    		dialog.setMovable(true);
    		stage.addActor(dialog);
    		dialog.show(stage);
    		dialog.setSize(650, 450);
    		dialog.setResizable(true);
    		selectBox=new SelectBox<String>(skin);
    		selectBox.setItems(mapStrings);
    		selectBox.setMaxListCount(6);
    		Table table = new Table();
    		table.add().row();
    		Cell<?> cell = table.add();
    		table.add().row();
    		table.add(selectBox);
    		dialog.getContentTable().add(table).row();
    		selectBox.setSelected(context.currentMapName);
        	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
        	if(pixmap != null) {
            	Texture textureMap = new Texture(pixmap);
            	Image map = new Image(textureMap);
            	cell.setActor(map);
            	dialog.setSize(650, 450);
            	dialog.setPosition(Gdx.graphics.getWidth()/2 - 325, Gdx.graphics.getHeight()/2 - 200);
        	}else {
        		System.out.println("Not available");
        		dialog.setSize(400, 250);
        		dialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2 - 100);
        		Label notAvailable = new Label("Map preview not available.",skin);
        		cell.setActor(notAvailable);
        	}
    		selectBox.addListener(new ChangeListener(){
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                	try {
	                	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
	                	if(pixmap != null) {
		                	Texture textureMap = new Texture(pixmap);
		                	Image map = new Image(textureMap);
		                	cell.setActor(map);
		                	dialog.setSize(650, 450);
		                	dialog.setPosition(Gdx.graphics.getWidth()/2 - 325, Gdx.graphics.getHeight()/2 - 200);
	                	}else {
	                		System.out.println("Not available");
	                		dialog.setSize(400, 250);
	                		dialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2 - 100);
	                		Label notAvailable = new Label("Map preview not available.",skin);
	                		cell.setActor(notAvailable);
	                	}
                	}catch(Exception e) {
                		System.out.println("File does not exist");
                		
                	}
                }
            });
    		dialog.button("Change", 1L).pad(20, 20, 20, 20);
    		dialog.button("Cancel", 2L).pad(20, 20, 20, 20);
    		dialog.setVisible(true);
    		input = Gdx.input.getInputProcessor();
    		Gdx.input.setInputProcessor(stage);
    		return true;
    	}
        else {
            return false;
        }
    }

    /**
     * return whether point is in rect or not.
     */
    private boolean isPointInRect(float mouseX, float mouseY, Rectangle rec) {
    	if (( mouseX >= rec.getMinX() && mouseX <= rec.getMaxX() )
     		   && ( mouseY >= rec.getMinY() && mouseY <= rec.getMaxY()))
     		   {
     			return true;
     		   }
     	else {
     		return false;
     	}
    }

    private boolean isClickingMenuButton(float x, float y) {
        return isPointInRect(x,y,menuButton_Shape);
    }
    
    private boolean isClickingMenuTable(float x, float y) {
        return isPointInRect(x,y,menuTable_Shape);
    }
    
    private boolean isClickingLobbyButton(float x, float y) {
        return isPointInRect(x,y,menuLobby_Shape);
    }
    
    private boolean isClickingMapsButton(float x, float y) {
        return isPointInRect(x,y,menuMap_Shape);
    }
    
    @Override
    public boolean handleDrag(float x, float y, float ix, float iy) {
        // deactivate it with no penalty to the user.

        return false;
    }

    @Override
    public boolean handleRelease(float x, float y, int button) {
    	menuLobbyIsDown = false;
    	menuMapsIsDown = false;

        return false;
    }


	@Override
	public boolean keyDown(int arg0) {
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		return false;
	}

	@Override
	public boolean handleMouseMove(float x, float y) {
		return false;
	}
	
	public static Texture createTexture(int width, int height, Color col,
            float alfa) {
        Pixmap pixmap = new Pixmap(width, height, Format.Alpha);
        Color color = col;
        pixmap.setColor(color.r, color.g, color.b, alfa);
        pixmap.fillRectangle(0, 0, width, height);

        Texture pixmaptexture = new Texture(pixmap);
        return pixmaptexture;
    }
}
