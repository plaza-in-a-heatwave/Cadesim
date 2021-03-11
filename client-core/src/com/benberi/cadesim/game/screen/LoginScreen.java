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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.AbstractScreen;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;

public class LoginScreen extends AbstractScreen implements InputProcessor {
    private GameContext context;
    final Graphics graphics = Gdx.graphics;

//    private long popupTimestamp;

    /**
     * Batch for opening screen
     */


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

    private TextField accountName;
    private TextField password;

    private Texture clientlogo;
    private Texture mapEditorButtonUp;
    private Texture mapEditorButtonDown;
    private Texture guestButtonUp;
    private Texture guestButtonDown;
    private CheckBox passwordMode;

    public Table settingTable;
    private SelectBox<String> roomLabel;

    private boolean codeURL;
    private boolean isStartup = true;
    
    private String old_Name;

    @SuppressWarnings("unused")
	private String address_info;
    private Label message;
    
    private final int MAIN_GROUP_OFFSET_Y = 20;
    
    private Drawable mapEditorDrawable;
    private Drawable mapEditorDisabledDrawable;
    private Drawable guestDrawable;
    private Drawable guestDisabledDrawable;
    private TextButton loginButton;
    private ImageButton buttonMapEditor;
    private ImageButton buttonGuest;
    private ImageButtonStyle mapEditorButtonStyle;
    private ImageButtonStyle guestButtonStyle;
    
    public int screenWidth;
    public int screenHeight;
    
    private Random random = new Random();
    private String[] url = null;
	private AsyncExecutor executor = new AsyncExecutor(4);
	private AsyncResult<Void> task;

    public LoginScreen(GameContext context) {
    	super();
        this.context = context;
      	
    }
    
    public void buildStage() {
		context.setLobbyScreen(this);
    	port_numbers.clear();
    	room_names.clear();
    	server_codes.clear();
    	greetings.clear();
        mapEditorButtonStyle = new ImageButtonStyle();
        guestButtonStyle = new ImageButtonStyle();
        url = null;

    	initTextures();
    	initGreetings();
    	message = new Label("", skin);
    	passwordMode = new CheckBox(" Show/Hide Password",skin);
        mapEditorButtonStyle.imageUp = mapEditorDrawable;
        mapEditorButtonStyle.imageDown = mapEditorDisabledDrawable;
        mapEditorButtonStyle.imageOver = mapEditorDisabledDrawable;
        
        guestButtonStyle.imageUp = guestDrawable;
        guestButtonStyle.imageDown = guestDisabledDrawable;
        guestButtonStyle.imageOver = guestDisabledDrawable;
        
        //login button
        Skin altskin = new Skin(Gdx.files.internal("skin/glassy/glassy-ui.json"));
        loginButton = new TextButton("LOGON", altskin);
        buttonMapEditor = new ImageButton(mapEditorButtonStyle); //Set the button up
        buttonGuest = new ImageButton(guestButtonStyle); //Set the button up
        
        roomLabel = new SelectBox<String>(skin);
        roomLabel.setSize(220, 14);
    	fillSelectBoxes();
    	
    	fillInfo();
        initListeners();
        
        settingTable = new Table();
        settingTable.add(roomLabel).width(170).growX().row();
        settingTable.padLeft(190).padBottom(100);
        setActorPositions(Gdx.graphics.getWidth());
        addStage();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
    	multiplexer.addProcessor(this);
    	multiplexer.addProcessor(stage);
    	Gdx.input.setInputProcessor(multiplexer);
    	buttonGuest.setDisabled(true);
    	buttonMapEditor.setDisabled(true);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
			    task = executor.submit(new AsyncTask<Void>() {
			         public Void call() {
			            context.loadSeaAssets();
			            return null;
			         }
			    });
			}
		});
    }
    
    public void setStatusMessage(String popupMessage) {
    	message.setText(popupMessage);
    	message.setPosition(Gdx.graphics.getWidth()/2 - message.getText().length() * 3, 170);
    }
    
    
    public void addStage() {
    	stage.addActor(accountName);
    	stage.addActor(password);
    	stage.addActor(settingTable);
        stage.addActor(loginButton);
        stage.addActor(buttonMapEditor); // comment to toggle
        stage.addActor(buttonGuest); // comment to toggle
        stage.addActor(message);
        stage.addActor(passwordMode);
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
    	if(!loginButton.isTouchable()) {
    		loginButton.setTouchable(Touchable.enabled);
    	}
    	
    	if(task != null && task.isDone() && isStartup && context.getManager().update()) {
    		isStartup = false;
    		buttonGuest.setDisabled(false);
    		buttonMapEditor.setDisabled(false);
    	}
        stage.getBatch().setColor(Color.WHITE);
    	stage.getBatch().begin();
    	stage.getBatch().draw(clientlogo, Gdx.graphics.getWidth()/2 - clientlogo.getWidth()/2, MAIN_GROUP_OFFSET_Y + 432, 128, 128);
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
    	
        font.setColor(Color.ORANGE);
        font.draw(stage.getBatch(), "Account:",   Gdx.graphics.getWidth()/2 - 180, MAIN_GROUP_OFFSET_Y + 355);
        font.draw(stage.getBatch(), "Password:", Gdx.graphics.getWidth()/2 - 190, MAIN_GROUP_OFFSET_Y + 320);
        font.draw(stage.getBatch(), "Server:", Gdx.graphics.getWidth()/2 - 170, MAIN_GROUP_OFFSET_Y + 270);
        stage.getBatch().end();
		
        stage.act();
		stage.draw();
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
        
        mapEditorButtonUp = context.getManager().get(context.getAssetObject().mapEditorButtonUp);
        mapEditorButtonDown = context.getManager().get(context.getAssetObject().mapEditorButtonDown);
        mapEditorDrawable = new TextureRegionDrawable(new TextureRegion(mapEditorButtonUp));
        mapEditorDisabledDrawable = new TextureRegionDrawable(new TextureRegion(mapEditorButtonDown));
        
        guestButtonUp = context.getManager().get(context.getAssetObject().guestButtonUp);
        guestButtonDown = context.getManager().get(context.getAssetObject().guestButtonDown);
        guestDrawable = new TextureRegionDrawable(new TextureRegion(guestButtonUp));
        guestDisabledDrawable = new TextureRegionDrawable(new TextureRegion(guestButtonDown));
    }
    
	/*
	 * fill selectboxes with appropriate information
	 */
    public void fillSelectBoxes() {
        String[] rooms = Constants.SERVER_ROOMS.values().toArray(new String[Constants.SERVER_ROOMS.size()]);
        roomLabel.setItems(rooms);
    }

	/*
	 * Initialize properties such as team info/resolution, etc.
	 */
    public void fillInfo() {
        if(Constants.USERPROPERTIES.get("user.username") == null) {
        	accountName = new TextField("User"+Integer.toString(random.nextInt(9999)), skin);
        }else {
        	accountName = new TextField(Constants.USERPROPERTIES.get("user.username"), skin);	
        }
        
        password = new TextField(Constants.SERVER_CODE, skin);
        password.setPasswordCharacter('*');
        password.setPasswordMode(true);
        
        accountName.setWidth(200);
        password.setWidth(200);
    	accountName.setMaxLength(Constants.MAX_NAME_SIZE);
    	password.setMaxLength(Constants.MAX_NAME_SIZE);
    }
    
    public void initListeners() {
    	passwordMode.addListener(new ChangeListener() {
    	    @Override
    	    public void changed (ChangeEvent event, Actor actor) {
    	    	if (passwordMode.isChecked()) {
        	        password.setPasswordMode(false);
    	    	}else {
        	        password.setPasswordMode(true);
    	    	}
    	    }
    	});
        buttonGuest.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	context.setServerRoom(roomLabel.getSelected());
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
		            	stage.clear();
		            	ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, context);
		            	graphics.setResizable(false);
					}
            	});
            }
        });
        buttonMapEditor.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
		            	stage.clear();
		            	ScreenManager.getInstance().showScreen(ScreenEnum.MAPEDITOR,context);
		            	context.setStartedMapEditor(true);
						graphics.setResizable(false);
					}
            	});
            }
        });
        
        loginButton.addListener(new ClickListener() {//runs update if there is one before logging in 
            public void clicked(InputEvent event, float x, float y){
            	Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
                        try {
                            performUpdateCheck();
                            loginButton.toggle();
                        } catch (UnknownHostException e) {
                            return;
                        }
        	        	graphics.setResizable(false);
        			}
            		
            	});
            }});
        
        accountName.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                setOld_Name(accountName.getText());
            }
        });
        roomLabel.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {

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
    	loginButton.setPosition(width/2 - loginButton.getWidth()/2, 205);
    	loginButton.setPosition(Gdx.graphics.getWidth()/2 - loginButton.getWidth()/2, 205);
    	loginButton.setSize(300, 44);
        settingTable.setPosition(width/2 - 103, MAIN_GROUP_OFFSET_Y + 210);
        buttonMapEditor.setPosition(width - 70, Gdx.graphics.getHeight()-50);
        buttonGuest.setPosition(30, Gdx.graphics.getHeight()-50);
        accountName.setPosition(width/2 - accountName.getWidth()/2 , MAIN_GROUP_OFFSET_Y + 330);
        password.setPosition(width/2 - password.getWidth()/2, MAIN_GROUP_OFFSET_Y + 295);
        passwordMode.setPosition(width/2 + 120,  MAIN_GROUP_OFFSET_Y + 295);
    }

    @Override
    public void dispose() {
    	super.dispose();
    	renderer.dispose();
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.CENTER) {
            if (stage.getKeyboardFocus() != accountName && accountName.getText().isEmpty()) {
                stage.setKeyboardFocus(accountName);
            } else {
            	performClick(loginButton);
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
		String updateType = Constants.USERPROPERTIES.get("autoupdate");
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
    

    public static void performClick(Actor actor) {
        Array<EventListener> listeners = actor.getListeners();
        for(int i=0;i<listeners.size;i++)
        {
            if(listeners.get(i) instanceof ClickListener){
                ((ClickListener)listeners.get(i)).clicked(null, 0, 0);
            }
        }
    }
    
    private void performLogin() throws UnknownHostException {
		if (accountName.getText().length() <= 0) {
			setStatusMessage("Client: " + "Please enter a display name.");
		}else {
            // Save current choices for next time
			updateProperties();
			//connect to database
			context.setServerRoom(roomLabel.getSelected());
			context.connectdb(accountName.getText(), password.getText());
		}
    }

    public void updateProperties() {
    	Properties prop =new Properties();
    	try {
            prop.load(new FileInputStream("user.config"));
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.username", accountName.getText());
            prop.setProperty("user.last_room_index", Integer.toString(roomLabel.getSelectedIndex()));
            prop.store(new FileOutputStream("user.config"),null);
    	}catch (FileNotFoundException e) {
			System.out.println("No config files found on system. Creating config files..");
			
            prop.setProperty("user.width", Integer.toString(Gdx.graphics.getWidth()));
            prop.setProperty("user.height", Integer.toString(Gdx.graphics.getHeight()));
            prop.setProperty("user.username", accountName.getText());
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
					put("user.volume", prop.getProperty("user.volume"));
					put("autoupdate", prop.getProperty("autoupdate"));
					put("url", prop.getProperty("url"));
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
					put("user.volume", "0.15");
					put("autoupdate", "yes");
					put("url", prop.getProperty("url"));
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
					put("url", prop.getProperty("url"));
			}};
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
        setStatusMessage("Client: " + "Could not confirm credentials.");
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