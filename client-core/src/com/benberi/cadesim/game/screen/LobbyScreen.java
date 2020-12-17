package com.benberi.cadesim.game.screen;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

import org.apache.commons.io.IOUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.AbstractScreen;
import com.benberi.cadesim.util.RandomUtils;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.screen.impl.connect.RoomNumberLabel;
import com.benberi.cadesim.game.screen.impl.connect.ShipTypeLabel;
import com.benberi.cadesim.game.screen.impl.connect.TeamTypeLabel;

public class LobbyScreen extends AbstractScreen implements InputProcessor {
    private GameContext context;
    final Graphics graphics = Gdx.graphics;

    // the connect state
    @SuppressWarnings("unused")
	private long loginAttemptTimestampMillis; // initialised when used
//    private long popupTimestamp;

    /**
     * Batch for opening screen
     */

    /**
     * The shape renderer
     */
    public ShapeRenderer renderer;

    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont notesFont;
    
    // connectscene
    private ArrayList<String> greetings = new ArrayList<String>();
    private ArrayList<String> port_numbers = new ArrayList<String>();
    private ArrayList<String> server_codes = new ArrayList<String>();
    private ArrayList<String> room_names = new ArrayList<String>();
    private java.util.Random prng = new java.util.Random(System.currentTimeMillis());
    private String chosenGreeting;
    private final String CODE_URL = "https://github.com/plaza-in-a-heatwave/Cadesim/issues";
    private final int CODE_URL_WIDTH = 278; // px

    private boolean failed;

    /**
     * The main stage for elements
     */
    public Dialog popup;

    private TextField name;
    private TextField address;
    private TextField code;

    private Texture clientlogo;
    
    private Texture textfieldTexture;
    private Texture loginButtonUp;
    private Texture loginButtonDown;
    private Texture mapEditorButtonUp;
    private Texture mapEditorButtonDown;

    private Texture baghlah;
    private Texture blackship;
    private Texture dhow;
    private Texture fanchuan;
    private Texture grandfrig;
    private Texture junk;
    private Texture lgsloop;
    private Texture longship;
    private Texture merchbrig;
    private Texture merchgal;
    private Texture smsloop;
    private Texture warbrig;
    private Texture warfrig;
    private Texture wargal;
    private Texture xebec;

    public Table settingTable;
    private SelectBox<ShipTypeLabel> shipType;
    private SelectBox<TeamTypeLabel> teamType;
    private SelectBox<RoomNumberLabel> roomLabel;

    private boolean codeURL;
    
    private String old_Name;
    private String room_info;
    private String address_info;
    
    private final int MAIN_GROUP_OFFSET_Y = 20;
    
    private Drawable loginDrawable;
    private Drawable loginDisabledDrawable;
    private Drawable mapEditorDrawable;
    private Drawable mapEditorDisabledDrawable;
    private ImageButton buttonConn;
    private ImageButton buttonMapEditor;
    private ImageButtonStyle mapEditorButtonStyle;
    private ImageButtonStyle loginButtonStyle;
    
    public int screenWidth;
    public int screenHeight;

    private Skin skin;
    
    private Random random = new Random();
    private HashMap<String,String> userProperties;
    private String[] url = null;
    
    TextField.TextFieldStyle style;
    SelectBox.SelectBoxStyle selectBoxStyle;
    
    public LobbyScreen(GameContext context) {
    	super();
        this.context = context;
        lookupUrl();
      	readURLServerConfig();
      	
    }
    
    public void buildStage() {
		context.setLobbyScreen(this);
    	port_numbers.clear();
    	room_names.clear();
    	server_codes.clear();
    	greetings.clear();
        renderer = new ShapeRenderer();
        loginButtonStyle = new ImageButtonStyle();
        mapEditorButtonStyle = new ImageButtonStyle();
        url = null;
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        //styles
        style = new TextField.TextFieldStyle();
        style.fontColor = new Color(0.16f, 0.16f, 0.16f, 1);
        selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.fontColor = new Color(1,1,1, 1);
        selectBoxStyle.listStyle = new List.ListStyle();
        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
    	initTextures();
    	initGreetings();
    	
        loginButtonStyle.imageUp = loginDrawable;
        loginButtonStyle.imageDown = loginDisabledDrawable;
        loginButtonStyle.imageChecked = loginDrawable;
        loginButtonStyle.imageOver = loginDisabledDrawable;
        
        mapEditorButtonStyle.imageUp = mapEditorDrawable;
        mapEditorButtonStyle.imageDown = mapEditorDisabledDrawable;
        mapEditorButtonStyle.imageOver = mapEditorDisabledDrawable;
        
        //login button
        buttonConn = new ImageButton(loginButtonStyle); //Set the button up
        buttonMapEditor = new ImageButton(mapEditorButtonStyle); //Set the button up
        
        style.font = font;
        selectBoxStyle.font = font;
        selectBoxStyle.listStyle.selection.setLeftWidth(5);
        selectBoxStyle.listStyle.font = font;
        selectBoxStyle.background.setLeftWidth(10);
   
        teamType = new SelectBox<>(selectBoxStyle);
        teamType.setSize(200, 44);

        shipType = new SelectBox<>(selectBoxStyle);
        shipType.setSize(200, 44);
        
        roomLabel = new SelectBox<RoomNumberLabel>(selectBoxStyle);
        roomLabel.setSize(200, 44);
    
        createPopup();
    	initProperties();
    	fillSelectBoxes();
    	fillInfo();
        initListeners();
        
        getServerCode(); // initialize server code with currently selected room
        
        settingTable = new Table();
        settingTable.add(teamType).width(170).growX().row();
        settingTable.add(roomLabel).width(170).growX().row();
        settingTable.add(shipType).width(170).growX().row();
        settingTable.padLeft(190).padBottom(100);
        setActorPositions(Gdx.graphics.getWidth());
        addStage();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    	multiplexer.addProcessor(stage);
    	multiplexer.addProcessor(this);
    	Gdx.input.setInputProcessor(multiplexer);
    	
    	if(!Constants.AUTO_UPDATE) { // test purposes
    		Constants.PROTOCOL_PORT = 4970;
    		address.setText("localhost");
    		code.setText("");
    	}
    }
    
    public void createPopup() {
        popup = new Dialog("Client Message:",skin);
        popup.setColor(Color.RED);
    	stage.getBatch().setColor(Color.WHITE);
        popup.show(stage);
        popup.setResizable(false);
        popup.setVisible(false);
    }
    
    public void setPopupMessage(String message) {
    	popup.getContentTable().clear();
		popup.text(message);
    	popup.pack();
    }
    
    public void closePopup() {
    	if(popup.isVisible()) {
        	Timer t = new java.util.Timer();
        	t.schedule( 
        	        new java.util.TimerTask() {
        	            @Override
        	            public void run() {
        	            	popup.setVisible(false);
        	            }
        	        }, 
        	        5000 
        	);	
    	}else {
    		return;
    	}
    }
    
    public void showPopup() {
    	popup.toFront();
    	popup.setX((Gdx.graphics.getWidth()/2) - (popup.getWidth()/2));
    	popup.setVisible(true);
    }
    
    public void addStage() {
    	stage.addActor(name);
    	stage.addActor(address);
    	stage.addActor(code);
    	stage.addActor(settingTable);
        stage.addActor(buttonConn);
        stage.addActor(buttonMapEditor); // comment to toggle
        stage.addActor(popup);
    }
    //gets server code for the specific selected room
    public void getServerCode() {
        if (roomLabel.getSelectedIndex() < port_numbers.size()) { //sanity check
            Constants.PROTOCOL_PORT = Integer.parseInt(port_numbers.get(roomLabel.getSelectedIndex()));
            Constants.SERVER_CODE = server_codes.get(roomLabel.getSelectedIndex());
            code.setText(Constants.SERVER_CODE);
        }
    }
    
    public Color toRGB(int r, int g, int b) {
    	  float RED = r / 255.0f;
    	  float GREEN = g / 255.0f;
    	  float BLUE = b / 255.0f;
    	  return new Color(RED, GREEN, BLUE, 1);
    	 }

    @Override
    public void render(float delta) {
    	closePopup();
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//enable login click if disabled
    	if(!buttonConn.isTouchable()) {
    		buttonConn.setTouchable(Touchable.enabled);
    	}
    	stage.getBatch().begin();
    	stage.getBatch().draw(clientlogo, Gdx.graphics.getWidth()/2 - 60, MAIN_GROUP_OFFSET_Y + 432, 128, 128);
    	stage.getBatch().end();
    	stage.getBatch().begin();
    	titleFont.setColor(toRGB(240,137,13));
    	titleFont.draw(stage.getBatch(), "Global CadeSim", Gdx.graphics.getWidth()/2 - 230, MAIN_GROUP_OFFSET_Y + 420);
    	notesFont.draw(stage.getBatch(), chosenGreeting,       Gdx.graphics.getWidth()/2 + 130, MAIN_GROUP_OFFSET_Y + 399);        		
		notesFont.draw(stage.getBatch(), "Version " + Constants.VERSION + " by Cyclist & Fatigue, based on the Cadesim by Benberi", 15, 75);
    	notesFont.draw(stage.getBatch(), "Inspired by the original Dachimpy Cadesim", 15, 50);
    	notesFont.draw(stage.getBatch(), "Found a bug? Let us know!", 15, 25);
    	
    	if (codeURL) { notesFont.setColor(Color.SKY); }
        notesFont.draw(stage.getBatch(), CODE_URL, 138, 25);
        notesFont.setColor(Color.WHITE);
    	
        font.setColor(Color.WHITE);
        font.draw(stage.getBatch(), "Display name:",   Gdx.graphics.getWidth()/2 - 230, MAIN_GROUP_OFFSET_Y + 370);
        font.draw(stage.getBatch(), "Server address:", Gdx.graphics.getWidth()/2 - 75, MAIN_GROUP_OFFSET_Y + 370);
        font.draw(stage.getBatch(), "Server code:",    Gdx.graphics.getWidth()/2 + 82, MAIN_GROUP_OFFSET_Y + 370);
        stage.getBatch().draw(textfieldTexture, Gdx.graphics.getWidth()/2 - 230, MAIN_GROUP_OFFSET_Y + 295, 140, 49);
        stage.getBatch().draw(textfieldTexture, Gdx.graphics.getWidth()/2 - 75, MAIN_GROUP_OFFSET_Y + 295, 140, 49);
        stage.getBatch().draw(textfieldTexture, Gdx.graphics.getWidth()/2 + 82, MAIN_GROUP_OFFSET_Y + 295, 140, 49);
        stage.getBatch().end();
		
        stage.act();
		stage.draw();
        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().begin();
        
        font.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
        font.draw(stage.getBatch(), "Connect", Gdx.graphics.getWidth()/2 - 35, MAIN_GROUP_OFFSET_Y + 269);
        Texture t;
        t = shipType.getSelected().getType();
        stage.getBatch().draw(t, settingTable.getX() +115, -2); // draw t, whatever it may be
        stage.getBatch().end();
    }

	/*
	 * Greeting list for startup screen
	 */
    public void initGreetings() {
        // greetings
        greetings.add("It simulates blockades!");
        greetings.add("No ships were harmed, honest");
        greetings.add("Hot Pirate On Pirate Blockading Action");
        greetings.add("Job for Keep The Peace!");
        greetings.add("I am a sloop, I do not move!");
        greetings.add("Cyclist Edition");
        greetings.add("Home grown!");
        greetings.add("Blub");
        greetings.add("You sunk my battleship!");
        greetings.add("You'll never guess what happened next...");
        greetings.add("Inconceivable!");
        greetings.add("Every day I'm Simulatin'");
        greetings.add("Matured in oak casks for 24 months");
        greetings.add("Probably SFW");
        greetings.add("Sea monsters are always Kraken jokes");
        greetings.add("Without a shadow of a Trout");
        greetings.add("Just for the Halibut");
        greetings.add("Placing Moves, not Moving Plaice");
        greetings.add("Went to fish frowning comp. Had to Gurnard");
        greetings.add("Just Mullet over");
        greetings.add("Don't tell him, Pike!");
        greetings.add("Tales of Herring Do");
        greetings.add("Needlefish? Get all fish!");
        greetings.add("It's... It's... Eely good");
        greetings.add("Bream me up, Scotty!");
        greetings.add("Living the Bream");
        greetings.add("micro/nano Blockade SIMs available!");
        greetings.add("Written by pirates, for pirates");
        greetings.add("Precariously Perched!");
        greetings.add("Hake it out, Hake it out, ooh whoa");
        greetings.add("It's Turbot-charged!");
        greetings.add("Albatross!");
        greetings.add("You're in for a shark!");
        greetings.add("We Booched It!");
        chosenGreeting = greetings.get(prng.nextInt(greetings.size()));
    }
    
	/*
	 * Initialize textures for connect scene
	 */
    public void initTextures() {
        // fonts
        font = context.getManager().get(context.getAssetObject().regularFont);
        clientlogo = context.getManager().get(context.getAssetObject().clientlogo);
        notesFont = context.getManager().get(context.getAssetObject().notesFont);
        titleFont = context.getManager().get(context.getAssetObject().titleFont);
        textfieldTexture = context.getManager().get(context.getAssetObject().textfieldTexture);

        loginButtonUp = context.getManager().get(context.getAssetObject().loginButton);
        loginDrawable = new TextureRegionDrawable(new TextureRegion(loginButtonUp));
        loginButtonDown = context.getManager().get(context.getAssetObject().loginButtonDown);
        loginDisabledDrawable = new TextureRegionDrawable(new TextureRegion(loginButtonDown));
        
        mapEditorButtonUp = context.getManager().get(context.getAssetObject().mapEditorButtonUp);
        mapEditorButtonDown = context.getManager().get(context.getAssetObject().mapEditorButtonDown);
        mapEditorDrawable = new TextureRegionDrawable(new TextureRegion(mapEditorButtonUp));
        mapEditorDisabledDrawable = new TextureRegionDrawable(new TextureRegion(mapEditorButtonDown));
        style.cursor = new Image(
        		context.getManager().get(context.getAssetObject().cursor)).getDrawable();
        style.selection = new Image(
        		context.getManager().get(context.getAssetObject().selection)).getDrawable();

        baghlah = context.getManager().get(context.getAssetObject().baghlahSkin);
        blackship = context.getManager().get(context.getAssetObject().blackshipSkin);
        dhow = context.getManager().get(context.getAssetObject().dhowSkin);
        fanchuan = context.getManager().get(context.getAssetObject().fanchuanSkin);
        grandfrig = context.getManager().get(context.getAssetObject().grandfrigSkin);
        junk = context.getManager().get(context.getAssetObject().junkSkin);
        lgsloop = context.getManager().get(context.getAssetObject().lgsloopSkin);
        longship = context.getManager().get(context.getAssetObject().longshipSkin);
        merchbrig = context.getManager().get(context.getAssetObject().merchbrigSkin);
        merchgal = context.getManager().get(context.getAssetObject().merchgalSkin);
        smsloop = context.getManager().get(context.getAssetObject().smsloopSkin);
        warbrig = context.getManager().get(context.getAssetObject().warbrigSkin);
        warfrig = context.getManager().get(context.getAssetObject().warfrigSkin);
        wargal = context.getManager().get(context.getAssetObject().wargalSkin);
        xebec = context.getManager().get(context.getAssetObject().xebecSkin);
        
        selectBoxStyle.background = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxBackground)).getDrawable();
        selectBoxStyle.listStyle.selection = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxListSelection)).getDrawable();
        selectBoxStyle.listStyle.background = new Image(
        		context.getManager().get(context.getAssetObject().selectBoxListBackground)).getDrawable();
    }
    
	/*
	 * fill selectboxes with appropriate information
	 */
    public void fillSelectBoxes() {
    	splitRoomInfo();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = new Color(0.16f, 0.16f, 0.16f, 1);
        int numLabels = Vessel.VESSEL_TYPES.size() - (Constants.ENABLE_CHOOSE_BLACKSHIP?0:1);
        ShipTypeLabel[] blob = new ShipTypeLabel[numLabels];
        blob[0]  = new ShipTypeLabel(smsloop,   Vessel.getIdFromName("smsloop"),   "Sloop",       labelStyle); 
        blob[1]  = new ShipTypeLabel(lgsloop,   Vessel.getIdFromName("lgsloop"),   "Cutter",      labelStyle); 
        blob[2]  = new ShipTypeLabel(dhow,      Vessel.getIdFromName("dhow"),      "Dhow",        labelStyle); 
        blob[3]  = new ShipTypeLabel(fanchuan,  Vessel.getIdFromName("fanchuan"),  "Fanchuan",    labelStyle); 
        blob[4]  = new ShipTypeLabel(longship,  Vessel.getIdFromName("longship"),  "Longship",    labelStyle); 
        blob[5]  = new ShipTypeLabel(junk,      Vessel.getIdFromName("junk"),      "Junk",        labelStyle); 
        blob[6]  = new ShipTypeLabel(baghlah,   Vessel.getIdFromName("baghlah"),   "Baghlah",     labelStyle); 
        blob[7]  = new ShipTypeLabel(merchbrig, Vessel.getIdFromName("merchbrig"), "MB",          labelStyle); 
        blob[8]  = new ShipTypeLabel(warbrig,   Vessel.getIdFromName("warbrig"),   "War Brig",    labelStyle); 
        blob[9]  = new ShipTypeLabel(xebec,     Vessel.getIdFromName("xebec"),     "Xebec",       labelStyle); 
        blob[10] = new ShipTypeLabel(merchgal,  Vessel.getIdFromName("merchgal"),  "MG",          labelStyle); 
        blob[11] = new ShipTypeLabel(warfrig,   Vessel.getIdFromName("warfrig"),   "War Frig",    labelStyle); 
        blob[12] = new ShipTypeLabel(wargal,    Vessel.getIdFromName("wargal"),    "War Galleon", labelStyle); 
        blob[13] = new ShipTypeLabel(grandfrig, Vessel.getIdFromName("grandfrig"), "Grand Frig",  labelStyle);
        if (Constants.ENABLE_CHOOSE_BLACKSHIP)
        {
        	blob[numLabels-1] = new ShipTypeLabel(blackship, Vessel.getIdFromName("blackship"), "Black Ship",  labelStyle);
        }

        shipType.setItems(blob);

        TeamTypeLabel[] blob2 = new TeamTypeLabel[2];
        blob2[0] = new TeamTypeLabel("Defender", labelStyle, TeamTypeLabel.DEFENDER);
        blob2[1] = new TeamTypeLabel("Attacker", labelStyle, TeamTypeLabel.ATTACKER);
        
        teamType.setItems(blob2);
        
        RoomNumberLabel[] blob_room = new RoomNumberLabel[port_numbers.size()];
        for (int i = 0; i < port_numbers.size(); ++i) {
        	blob_room[i] = new RoomNumberLabel((CharSequence)room_names.get(i), labelStyle, 0);
        }
        roomLabel.setItems(blob_room);
    }
    
    public void splitRoomInfo() {
		//Split info for each room (Port:Server Code)
		String[] rooms = room_info.split(",");
		for (int i = 0; i < rooms.length; i++) {
			String[] temp_room_info = rooms[i].split(":");
			for (int j = 0; j < temp_room_info.length;j++)
			{
				if (j % 2 == 0) {
					port_numbers.add(temp_room_info[j].replace("\\", ""));
				}
				else {
					
					String[] print = temp_room_info[j].split(";");
					server_codes.add(print[0]);
					room_names.add(print[1]);
				}
			}
		}
    }
    
	/*
	 * Initialize properties such as team info/resolution, etc.
	 */
    public void initProperties() {
    	userProperties = getUserProperties();
    }
	/*
	 * Initialize properties such as team info/resolution, etc.
	 */
    public void fillInfo() {
        if(userProperties.get("user.username") == null) {
        	name = new TextField("User"+Integer.toString(random.nextInt(9999)), style);
        }else {
        	name = new TextField(userProperties.get("user.username"), style);	
        }
        address = new TextField(address_info, style);
        
        // set previous values/defaults from config file
        try 
        {
        	if(userProperties.get("user.last_ship") == null || !(userProperties.get("user.last_ship").matches("[0-9]+"))) {
        		shipType.setSelectedIndex(Vessel.getIdFromName("warfrig"));
        	}else {
        		shipType.setSelectedIndex(Integer.parseInt(userProperties.get("user.last_ship")));
        	}
        }
        catch(IndexOutOfBoundsException e) {
        	shipType.setSelectedIndex(Vessel.getIdFromName("warfrig"));
        }
        
        try
        {
        	if(userProperties.get("user.last_team") == null || 
        			!(userProperties.get("user.last_team").matches("[0-9]+"))) {
        		teamType.setSelectedIndex(0);
        	}else {
        		teamType.setSelectedIndex(Integer.parseInt(userProperties.get("user.last_team")));
        	}
        }
        catch(IndexOutOfBoundsException e)
        {
        	teamType.setSelectedIndex(0);
        }
        
        try {
        	if(userProperties.get("user.last_room_index") == null || 
        			!(userProperties.get("user.last_room_index").matches("[0-9]+"))) {
        		roomLabel.setSelectedIndex(0);
        	}else {
        		roomLabel.setSelectedIndex(Integer.parseInt(userProperties.get("user.last_room_index")));
        	}
        }
        catch (IndexOutOfBoundsException e) {
            roomLabel.setSelectedIndex(0);
        }
        
        code = new TextField(Constants.SERVER_CODE, style);
        code.setPasswordCharacter('*');
        code.setPasswordMode(true);
        
        name.setSize(120, 49);
        address.setSize(120, 49);
        code.setSize(120, 49);
    }
    
	/*
	 * Initialize listeners for actors of stage
	 */
    public void initListeners() {
        buttonMapEditor.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	stage.clear();
            	ScreenManager.getInstance().showScreen(ScreenEnum.MAPEDITOR,context);
            	context.setStartedMapEditor(true);
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						graphics.setResizable(false);
					}
            	});
            }
        });
        
        buttonConn.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {

        			@Override
        			public void run() {
        	        	ScreenManager.getInstance().showScreen(ScreenEnum.LOADING,context,"Connecting, please wait...");
        			}
            		
            	});
                try {
                    performUpdateCheck();
                    buttonConn.toggle();
                	Gdx.app.postRunnable(new Runnable() {
    					@Override
    					public void run() {
    						graphics.setResizable(false);
    					}
                	});
                } catch (UnknownHostException e) {
                    return;
                }
            }});
        
        name.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                setOld_Name(name.getText());
            }
        });
        roomLabel.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    getServerCode();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

	/*
	 * Set position of actors in stage
	 */
    public void setActorPositions(int width) {
    	popup.setX((width/2) - (popup.getWidth()/2));
        buttonConn.setPosition(width/2 - 225, 260);
        settingTable.setPosition(width - 175, 15);
        buttonMapEditor.setPosition(width - 70, Gdx.graphics.getHeight()-50);
        name.setPosition(width/2 - 220 , MAIN_GROUP_OFFSET_Y + 295);
        address.setPosition(width/2 - 68 , MAIN_GROUP_OFFSET_Y + 295);
        code.setPosition(width/2 + 90, MAIN_GROUP_OFFSET_Y + 295);
    }

    @Override
    public void dispose() {
    	super.dispose();
    	renderer.dispose();
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    @Override
    public boolean keyDown(int keycode) {
    	System.out.println("key"+keycode);
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.CENTER) {
            if (!popup.isVisible()) {
                if (stage.getKeyboardFocus() != name && name.getText().isEmpty()) {
                    stage.setKeyboardFocus(name);
                } else if (stage.getKeyboardFocus() != address && address.getText().isEmpty()) {
                	stage.setKeyboardFocus(address);
                } else {
                    try {
                        performUpdateCheck();
                    } catch (UnknownHostException e) {
                        return failed;
                    }
                }
            }
            else {
                popup.setVisible(false);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
    	System.out.println(1);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
    	System.out.println("here");
        return false;
    }

    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (isMouseOverCodeUrl(screenX, Gdx.graphics.getHeight() - screenY))
		{
	    	try {
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(CODE_URL));
			} catch (IOException e) {
				// nvm, couldn't open URL
			}
	    	return true;
		}
		else
		{
			return false;
		}
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public void lookupUrl() {
    	try {
	        String line = null;
	        BufferedReader sc = new BufferedReader(new FileReader("getdown.txt"));
	        while((line = sc.readLine())!=null) {	
				if(line.isEmpty() || line.startsWith("#")) {	
					continue;	
				}	
				//remove spaces	
				line = line.replaceAll("\\s", "");	
				//check getdown.txt for url	
				if(line.startsWith("appbase=")) {	
					url = line.split("=");
					sc.close();
					break;
				}
	        }
    	}catch(Exception e) {
        	System.out.println("Unable to start getdown.jar; run manually. Please delete digest files and re-run. " + e);
        }
    }
    
    private void performUpdateCheck() throws UnknownHostException{

		// schedule update.
		//     enabled  if not defined.
        //     enabled  if defined and == "yes".
		//     disabled if defined and != "yes".
		String updateType = userProperties.get("autoupdate");
		if ((updateType != null) && (!updateType.equalsIgnoreCase("yes")))
		{
			System.out.println("Automatic updates are disabled in user.config.");
			performLogin();
		}
		else {
			// check for updates each run, unless a flag is passed.
	        if (!Constants.AUTO_UPDATE) {
	            System.out.println("Automatic updates are disabled by CLI.");
	            performLogin();
	        }
	        else
	        {
	        	try {
	        		System.out.println("Automatic updates are enabled. Checking for updates...");
	        		lookupUrl();
	                try {
	                	if(!new String(Files.readAllBytes(Paths.get("version.txt"))).matches(IOUtils.toString(new URL(url[1]+"version.txt").openStream(),StandardCharsets.UTF_8)) ) {
		                    System.out.println("Performing update; deleting digest files...");
		                    new File("version.txt").delete();
		                    new File("digest.txt").delete();
		                    new File("digest2.txt").delete();
		                    System.out.println("Performing update; closing client and running getdown...");
		                    new ProcessBuilder("java", "-jar", "getdown.jar").start();
		                    System.exit(0);
	                	}else {
	                		performLogin();
	                	}
	                }catch(IOException e){
	                    System.out.println("File not found. " + e);
	                    //if for some reason version.txt is not there
	                    new File("version.txt").createNewFile();
	                    performUpdateCheck();
	                }
	        	}catch(Exception e) {
	        		System.out.println("Unable to start getdown.jar; run manually. Please delete digest files and re-run. " + e);
	        	}
	        }        
		}
    }
    
    private void performLogin() throws UnknownHostException {
        loginAttemptTimestampMillis = System.currentTimeMillis();

		if (name.getText().length() > Constants.MAX_NAME_SIZE) {
			setPopupMessage("Display name must be less than " + Constants.MAX_NAME_SIZE + " letters.");
		} else if (code.getText().length() > Constants.MAX_CODE_SIZE) {
			setPopupMessage("Server code must be less than " + Constants.MAX_CODE_SIZE + " letters.");
		} else if (name.getText().length() <= 0) {
			setPopupMessage("Please enter a display name.");
		} else if (address.getText().length() <= 0) {
			setPopupMessage("Please enter an IP Address.");
		} else if (!RandomUtils.validIP(address.getText()) && !RandomUtils.validUrl(address.getText())) {
			setPopupMessage("Please enter a valid IP Address or URL.");
		} else {
            // Save current choices for next time
			updateProperties();
	        context.connect(name.getText(), address.getText(), code.getText(), shipType.getSelected().getIndex(), teamType.getSelected().getType());
		}
    }

    public void updateProperties() {
    	Properties prop =new Properties();
    	try {
            prop.load(new FileInputStream("user.config"));
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.username", name.getText());
            prop.setProperty("user.last_ship", Integer.toString(shipType.getSelectedIndex()));
            prop.setProperty("user.last_team", Integer.toString(teamType.getSelectedIndex()));
            prop.setProperty("user.last_room_index", Integer.toString(roomLabel.getSelectedIndex()));
            prop.store(new FileOutputStream("user.config"),null);
    	}catch (FileNotFoundException e) {
			System.out.println("No config files found on system. Creating config files..");
			
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.username", name.getText());
            prop.setProperty("user.last_ship", Integer.toString(shipType.getSelectedIndex()));
            prop.setProperty("user.last_team", Integer.toString(teamType.getSelectedIndex()));
            prop.setProperty("user.last_room_index", Integer.toString(roomLabel.getSelectedIndex()));
            
            try {
				prop.store(new FileOutputStream(new File("user.config")),null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public HashMap<String,String> getUserProperties(){
        Properties prop =new Properties();
        try {
			prop.load(new FileInputStream("user.config"));
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.username", prop.getProperty("user.username"));
					put("user.last_room_index", prop.getProperty("user.last_room_index"));
					put("user.last_team", prop.getProperty("user.last_team"));
					put("user.width", prop.getProperty("user.width"));
					put("user.height", prop.getProperty("user.height"));
					put("user.last_ship", prop.getProperty("user.last_ship"));
					put("user.volume", prop.getProperty("user.volume"));
					put("autoupdate", prop.getProperty("autoupdate"));
			}};
		}catch (FileNotFoundException e) {
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.username", "User"+Integer.toString(random.nextInt(9999)));
					put("user.last_room_index", "0");
					put("user.last_team", "0");
					put("user.width", "800");
					put("user.height", "600");
					put("user.last_ship", "11");
					put("user.volume", "0.15");
					put("autoupdate", "yes");
			}};
		} catch (IOException e) {
			return new HashMap<String,String> (){
				private static final long serialVersionUID = 1L;
				{
					put("user.username", "User"+Integer.toString(random.nextInt(9999)));
					put("user.last_room_index", "0");
					put("user.last_team", "0");
					put("user.width", "800");
					put("user.height", "600");
					put("user.last_ship", "11");
					put("user.volume", "0.15");
					put("autoupdate", "yes");
			}};
		}
    }

    public void readURLServerConfig() {
    	try {
    		String line = null;
    		Scanner sc = new Scanner(new URL(url[1]+"server.config").openStream());
	        while((line = sc.nextLine())!=null) {	
				if(line.isEmpty() || line.startsWith("#")) {	
					continue;	
				}	
				//check getdown.txt for url	
				if(line.startsWith("server.room_locations=")) {	
					room_info = line.split("=")[1];
				}
				if(line.startsWith("server.last_address=")) {	
					address_info = line.split("=")[1];
					sc.close();
					break;
				}
	        }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public boolean isMouseOverCodeUrl(float x, float y)
    {
        return x >= 138 && y >= 10 && x <= (138 + CODE_URL_WIDTH) && y < 31;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int height = Gdx.graphics.getHeight();
        codeURL = isMouseOverCodeUrl(screenX, height - screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    public void loginFailed() {
        setPopupMessage("Could not connect to server.");
        showPopup();
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