package com.benberi.cadesim.game.screen;

import java.io.*;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.AbstractScreen;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;
import com.benberi.cadesim.util.ShipSelection;
import com.benberi.cadesim.util.Team;
import com.benberi.cadesim.util.TeamSelection;
import com.benberi.cadesim.util.TextureCollection;
import com.benberi.cadesim.util.UtilMethods;

public class SelectionScreen extends AbstractScreen implements InputProcessor {
    private GameContext context;
    final Graphics graphics = Gdx.graphics;

    private BitmapFont font;

    private TextField shipName;
    private Texture clientlogo;

    public Table selectionTable;
    
    private String old_Name;
    
    private final int MAIN_GROUP_OFFSET_Y = 20;
    private TextButton buttonConn;
    
    public int screenWidth;
    public int screenHeight;
    
    private Cell<?> cell;
    private Cell<?> shipCell;
    private Cell<?> teamCell;
    private Label shipNameLabel;
    private Label incorrectNameLabel;
    private Label shipLabel;
    private Label teamLabel;
    private Label shipTextLabel;
    private Label teamTextLabel;
    
    private ShipSelection ship;
    private TeamSelection team;
    private TextButton backButton;
    private TextButton prevButton;
    private TextButton nextButton;
    private TextButton prevTeamButton;
    private TextButton nextTeamButton;
    
    private Image shipImage;
    
    public SelectionScreen(GameContext context) {
    	super();
        this.context = context;
    }
    
    public void buildStage() {
    	backButton = new TextButton("Return to Login", skin);
        ship = new ShipSelection(context);
        team = new TeamSelection(context);
        ship.setCurrentShipAsInt(Integer.parseInt(Constants.USERPROPERTIES.get("user.last_ship")));
        team.setCurrentTeamAsInt(Integer.parseInt(Constants.USERPROPERTIES.get("user.last_team")));
        prevButton = new TextButton("<<<", skin);
        nextButton = new TextButton(">>>", skin);
        prevTeamButton = new TextButton("<<<", skin);
        nextTeamButton = new TextButton(">>>", skin);
    	shipNameLabel = new Label("Ship Name:", skin);
    	shipNameLabel.setColor(Color.ORANGE);
    	incorrectNameLabel = new Label("", skin);
        shipName = new TextField("",skin);
        if (Constants.USERPROPERTIES.get("user.username") != null) shipName.setText(Constants.USERPROPERTIES.get("user.username"));
        shipLabel = new Label("Ship Type:", skin);
        shipLabel.setColor(Color.ORANGE);
        teamLabel = new Label("Team:", skin);
        teamLabel.setColor(Color.ORANGE);
    	initTextures();
        //login button
        Skin altskin = new Skin(Gdx.files.internal("skin/glassy/glassy-ui.json"));
        buttonConn = new TextButton("JOIN BATTLE", altskin);
        initListeners();
        selectionTable = new Table();
        selectionTable.add(prevButton).padRight(30).width(40).height(20);
        cell = selectionTable.add().width(100).height(90);
        shipImage = new Image(TextureCollection.prepareAltTextureForTeam(ship.getCurrentShip(), Team.forId(team.getCurrentTeamAsInt())));
    	cell.setActor(shipImage);
        selectionTable.add(nextButton).width(40).height(20).padLeft(30).row();
        shipCell = selectionTable.add().colspan(3).padBottom(20f);
        selectionTable.row();
        selectionTable.add(prevTeamButton).width(40).height(20);
        teamCell = selectionTable.add().width(70).padLeft(10).padRight(10);
		teamTextLabel = new Label(team.getCurrentTeam(), skin);
        teamCell.setActor(teamTextLabel);
        teamTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
        shipTextLabel = new Label(ship.getCurrentShipLabel(), skin);
        shipCell.setActor(shipTextLabel);
        shipTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
        selectionTable.add(nextTeamButton).width(40).height(20);
        addStage();
        selectionTable.pack();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    	multiplexer.addProcessor(this);
    	multiplexer.addProcessor(stage);
    	Gdx.input.setInputProcessor(multiplexer);
    }

    public void addStage() {
    	stage.addActor(backButton);
    	stage.addActor(shipName);
    	stage.addActor(shipNameLabel);
    	stage.addActor(shipLabel);
    	stage.addActor(teamLabel);
    	stage.addActor(incorrectNameLabel);
    	stage.addActor(selectionTable);
        stage.addActor(buttonConn);
    }
    
    public Color toRGB(int r, int g, int b) {
    	  float RED = r / 255.0f;
    	  float GREEN = g / 255.0f;
    	  float BLUE = b / 255.0f;
    	  return new Color(RED, GREEN, BLUE, 1);
    	 }

    @Override
    public void render(float delta) {
		Gdx.gl.glClearColor(37f/255f, 37f/255f, 38/255f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//enable login click if disabled
    	if(!buttonConn.isTouchable()) {
    		buttonConn.setTouchable(Touchable.enabled);
    	}
    	stage.getBatch().begin();
    	stage.getBatch().draw(clientlogo, Gdx.graphics.getWidth()/2 - clientlogo.getWidth()/2, MAIN_GROUP_OFFSET_Y + 432, 128, 128);
    	stage.getBatch().end();
    	stage.getBatch().begin();
    	
        font.setColor(Color.WHITE);
      
        stage.getBatch().end();
		
        stage.act();
		stage.draw();
    }
	/*
	 * Initialize textures for connect scene
	 */
    public void initTextures() {
        // fonts
        font = context.getManager().get(context.getAssetObject().regularFont);
        clientlogo = context.getManager().get(context.getAssetObject().clientlogo);
    }
    
	/*
	 * Initialize properties such as team info/resolution, etc.
	 */
    public void fillInfo() {
    }
    
	/*
	 * Initialize listeners for actors of stage
	 */
    public void initListeners() {
    	shipName.setMaxLength(15);
    	shipName.setTextFieldFilter(new TextField.TextFieldFilter() {
			@Override
            public  boolean acceptChar(TextField textField, char c) {
            	if (Character.toString(c).matches("^[a-zA-Z0-9]")) {
                    return true;
                }
                return false;
           }
		});
        shipName.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
            	if(textField.getText().toLowerCase().contains("fatigue")) {
            		incorrectNameLabel.setColor(Color.YELLOW);
            		incorrectNameLabel.setText("Reserved name; please choose another.");
            		incorrectNameLabel.setPosition(Gdx.graphics.getWidth()/2-120, 380);
            	}else{
            		incorrectNameLabel.setText("");
            	}
            }
        });
        buttonConn.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
        				if (shipName.getText().equals("") || shipName.getText().toString().equals("")) {
                    		incorrectNameLabel.setColor(Color.YELLOW);
                    		incorrectNameLabel.setText("Please enter a ship name.");
                    		incorrectNameLabel.setPosition(Gdx.graphics.getWidth()/2-120, 380);
                    		return;
        				}
        				context.setVesselName(shipName.getText());
        				context.setHostURL("localhost");
        				context.setVesselType(ship.getCurrentShipAsInt());
        				context.setTeam(team.getCurrentTeamAsInt());
        	        	ScreenManager.getInstance().showScreen(ScreenEnum.LOADING,context,"Connecting, please wait...");
        	        	graphics.setResizable(false);
        	        	updateProperties();
        	        	if(context.getHostURL().equalsIgnoreCase("localhost")) {
            	    		Constants.PROTOCOL_PORT = 4970;
        	        	}else {
            	    		Constants.PROTOCOL_PORT = UtilMethods.getKeysFromValue(Constants.SERVER_ROOMS, context.getServerRoom());
        	        	}
        			}
            		
            	});
                buttonConn.toggle();
            }});
        backButton.addListener(new ClickListener() {
    		public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
		            	ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
		            	graphics.setResizable(true);
					}
            	});
            }
    	});
        nextButton.addListener(new ClickListener() {
    		public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						ship.getNextShip();
						shipTextLabel = new Label(ship.getNextShipLabel(), skin);
						shipTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipCell.setActor(shipTextLabel);
			            shipImage = new Image(TextureCollection.prepareAltTextureForTeam(ship.getCurrentShip(), Team.forId(team.getCurrentTeamAsInt())));
			        	cell.setActor(shipImage);
					}
            	});
            }
    	});
    	prevButton.addListener(new ClickListener() {
    		public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						ship.getPreviousShip();
						shipTextLabel = new Label(ship.getPreviousShipLabel(), skin);
						shipTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipCell.setActor(shipTextLabel);
			            shipImage = new Image(TextureCollection.prepareAltTextureForTeam(ship.getCurrentShip(), Team.forId(team.getCurrentTeamAsInt())));
			        	cell.setActor(shipImage);
					}
            	});
            }
    	});
    	
    	prevTeamButton.addListener(new ClickListener() {
    		public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						teamTextLabel = new Label(team.getPreviousTeam(), skin);
				        teamCell.setActor(teamTextLabel);
				        teamTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipTextLabel = new Label(ship.getPreviousShipLabel(), skin);
						shipTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipCell.setActor(shipTextLabel);
			            shipImage = new Image(TextureCollection.prepareAltTextureForTeam(ship.getCurrentShip(), Team.forId(team.getCurrentTeamAsInt())));
			        	cell.setActor(shipImage);
					}
            	});
            }
    	});
    	
    	nextTeamButton.addListener(new ClickListener() {
    		public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						teamTextLabel = new Label(team.getNextTeam(), skin);
				        teamCell.setActor(teamTextLabel);
				        teamTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipTextLabel = new Label(ship.getPreviousShipLabel(), skin);
						shipTextLabel.setColor(Team.forString(teamTextLabel.getText().toString()).getAltColor());
						shipCell.setActor(shipTextLabel);
			            shipImage = new Image(TextureCollection.prepareAltTextureForTeam(ship.getCurrentShip(), Team.forId(team.getCurrentTeamAsInt())));
			        	cell.setActor(shipImage);
					}
            	});
            }
    	});
        
    }

	/*
	 * Set position of actors in stage
	 */
    public void setActorPositions(int width) {
    	backButton.setPosition(30, Gdx.graphics.getHeight()-50);
    	buttonConn.setSize(300, 44);
        buttonConn.setPosition(Gdx.graphics.getWidth()/2 - buttonConn.getWidth()/2, 140);
    	shipNameLabel.setPosition(Gdx.graphics.getWidth()/2 - 220, 400);
    	shipName.setPosition(Gdx.graphics.getWidth()/2 - 120, 397);
    	shipName.setWidth(250);
    	shipLabel.setPosition(Gdx.graphics.getWidth()/2 - 220, 300);
    	teamLabel.setPosition(Gdx.graphics.getWidth()/2 - 220, 220);
    	selectionTable.setPosition(Gdx.graphics.getWidth()/2 - 120, 220);
    }

    @Override
    public void dispose() {
    	super.dispose();
    	renderer.dispose();
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.CENTER) {
            if (!incorrectNameLabel.getText().equals("")) {
                if (stage.getKeyboardFocus() != shipName && shipName.getText().isEmpty()) {
                    stage.setKeyboardFocus(shipName);
                } else {
                	performClick(buttonConn);
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public static void performClick(Actor actor) {
        Array<EventListener> listeners = actor.getListeners();
        for(int i=0;i<listeners.size;i++)
        {
            if(listeners.get(i) instanceof ClickListener){
                ((ClickListener)listeners.get(i)).clicked(null, 0, 0);
            }
        }
    }

    public void updateProperties() {
    	Properties prop =new Properties();
    	try {
            prop.load(new FileInputStream("user.config"));
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.account", shipName.getText());
            prop.setProperty("user.last_ship", Integer.toString(ship.getCurrentShipAsInt()));
            prop.setProperty("user.last_team", Integer.toString(team.getCurrentTeamAsInt()));
            prop.store(new FileOutputStream("user.config"),null);
    	}catch (FileNotFoundException e) {
			System.out.println("No config files found on system. Creating config files..");
			
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.account", shipName.getText());
            prop.setProperty("user.last_ship", Integer.toString(ship.getCurrentShipAsInt()));
            prop.setProperty("user.last_team", Integer.toString(team.getCurrentTeamAsInt()));
            try {
				prop.store(new FileOutputStream(new File("user.config")),null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

 
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

	public String getOld_Name() {
		return old_Name;
	}

	public void setOld_Name(String old_Name) {
		this.old_Name = old_Name;
	}
	
	
	public void setScreenRect (int width, int height){
		screenWidth = width;
		screenHeight = height;
	}
	
	@Override
	public void resize (int width, int height) {
		super.resize(width, height);
		setActorPositions(width);
		setScreenRect(width,height);
	}

}