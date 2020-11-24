package com.benberi.cadesim.game.scene.impl.battle;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.SceneComponent;
import com.benberi.cadesim.game.scene.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.game.scene.impl.connect.ConnectScene;

public class MenuComponent extends SceneComponent<SeaBattleScene> implements InputProcessor {
    /**
     * The context
     */
    private GameContext context;
    /*
     * input processor for stage
     */
    public InputMultiplexer inputMultiplexer;
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
    private Texture settingsUp;
    private Texture settingsDown;
    
    private BitmapFont font;
    
    // reference coords - menu control
    private int MENU_REF_X       = 0;
    private int MENU_REF_Y       = 0;
    private int MENU_buttonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 36);

    private int MENU_tableX     = MENU_REF_X + (Gdx.graphics.getWidth() - 80);
    @SuppressWarnings("unused")
	private int MENU_tableY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 321);
    
    private int MENU_lobbyButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_lobbyButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 273);
    
    private int MENU_settingsButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    
	
    // DISENGAGE shapes
    Rectangle menuButton_Shape;
    Rectangle menuTable_Shape;
    Rectangle menuLobby_Shape;
    Rectangle menuMap_Shape;

    
    private JFileChooser fileChooser;
	public Stage stage;
	private SelectBox<String> selectBox;
	private Dialog settingsDialog;

	public Skin skin;
	private String[] mapStrings;
	private int[][] customMap;
	private boolean mapType = false;
	private String mapName = "None";

	Texture texture;
	private ImageButton menuButton;
	private ImageButtonStyle menuButtonStyle;
	private ImageButton settingsButton;
	private ImageButtonStyle settingsButtonStyle;
	private ImageButton lobbyButton;
	private ImageButtonStyle lobbyButtonStyle;
	//Settings buttons (sliders)
	public Slider audio_slider;
	private Slider turnDuration_slider;
	private Slider roundDuration_slider;
	private Slider sinkPenalty_slider;
	private TextField turnText;
	private TextField roundText;
	private TextField respawnPenaltyText;
	//buttons to select disengage behavior
	private TextButton disengageOff;
	private TextButton disengageRealistic;
	private TextButton disengageSimple;
	//buttons to select job quality
	private TextButton basicQuality;
	private TextButton eliteQuality;
	private TextButton customMapButton;
	//option buttons
	private TextButton proposeButton ;
	private TextButton exitButton;
	private TextButton defaultButton;
	private ScrollPane settingsScroller;
	private Table table;
	private Table sliderTable;
	private Table mapButtonTable;
	private Table disengageTable;
	private Table qualityTable;
	private Table mapTable;
	private Table selectionTable;
	private Container<Table> tableContainer;
	private Cell<?> cell;
	private Label disengageLabel;
	private Label turnLabel;
	private Label roundLabel;
	private Label sinkLabel;
	private Label jobberLabel;
	private Label previewLabel;
	
	private float audioMax = 0.25f;
	private int turnMax = 60;
	private int roundMax = 7200;
	private int respawnPenaltyMax = 10;
	
	private int DIALOG_WIDTH = 550;
	private int DIALOG_HEIGHT = 575;
	
	private int DIALOG_WIDTH_1 = 523;
	private int DIALOG_HEIGHT_1 = 331;
	
	private ButtonGroup<TextButton> disengageBehaviorGroup;
	private ButtonGroup<TextButton> jobberQualityGroup;
	
	/**
	 * Allow other parts of Cadesim to close the dialog
	 */
	public void closeSettingsDialog() {
        settingsDialog.setVisible(false);
        hideMenu();
        customMap = null;
        setMapType(false);
        customMapButton.setDisabled(false);
	}

	/**
	 * Allow other parts of Cadesim to check whether the dialog is open
	 */
	public boolean isSettingsDialogOpen() {
	    return settingsDialog.isVisible();
	}
	
	public Dialog getDialog() {
		return settingsDialog;
	}
	
    protected MenuComponent(GameContext context, SeaBattleScene owner) {
        super(context, owner);
        this.context = context;
    	stage = new Stage();
        batch = new SpriteBatch();
        fileChooser = new JFileChooser();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Skin sliderskin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        settingsDialog = new Dialog("Game Settings", skin, "dialog");
        menuTable_Shape = new Rectangle(MENU_tableX, 0, 80, 200);
        menuButton_Shape = new Rectangle(MENU_buttonX, 13, 25, 25);
        initImageButtonStyles();
       
		disengageLabel = new Label("Disengage Behavior:",skin);
		turnLabel = new Label("Turn Duration (seconds):",skin);
		roundLabel = new Label("Round Duration (seconds):",skin);
		sinkLabel = new Label("Sink Penalty (turns):",skin);
		jobberLabel = new Label("Jobber Quality:", skin);
		previewLabel = new Label("Map preview not available.",skin);
		
		selectBox=new SelectBox<String>(skin);
		audio_slider = new Slider(0.0f, audioMax, 0.01f,true, sliderskin);
		audio_slider.setVisible(false);
    	turnDuration_slider = new Slider(5.0f, (float)turnMax, 5.0f, false, skin);
    	roundDuration_slider = new Slider(30.0f, (float)roundMax, 5.0f, false, skin);
    	sinkPenalty_slider = new Slider(0.0f, (float)respawnPenaltyMax, 1.0f, false, skin);

    	turnText = new TextField("",skin);
    	turnText.setMaxLength(5);
 
    	roundText = new TextField("",skin);
    	roundText.setMaxLength(5);
    	respawnPenaltyText = new TextField("",skin);
    	respawnPenaltyText.setMaxLength(5);

    	
    	disengageBehaviorGroup = new ButtonGroup<TextButton>();
    	jobberQualityGroup = new ButtonGroup<TextButton>();
    	
    	disengageOff = new TextButton("Off",skin);
    	disengageOff.getStyle().disabled = disengageOff.getStyle().down;
    	disengageRealistic = new TextButton("Realistic",skin);
    	disengageRealistic.getStyle().disabled = disengageRealistic.getStyle().down;
    	disengageSimple = new TextButton("Simple",skin);
    	disengageSimple.getStyle().disabled = disengageSimple.getStyle().down;
    	basicQuality = new TextButton("Basic",skin);
    	basicQuality.getStyle().disabled = basicQuality.getStyle().down;
    	eliteQuality = new TextButton("Elite",skin);
    	eliteQuality.getStyle().disabled = eliteQuality.getStyle().down;
    	customMapButton = new TextButton("Choose map...",skin);
    	customMapButton.getStyle().disabled = customMapButton.getStyle().down;
    	disengageBehaviorGroup.add(disengageOff,disengageRealistic,disengageSimple);
    	jobberQualityGroup.add(basicQuality,eliteQuality);
    	proposeButton = new TextButton("Propose",skin);
    	exitButton = new TextButton("Exit",skin);
    	defaultButton = new TextButton("Reset defaults",skin);
    	audio_slider.setSize(10, 100);
    	audio_slider.setPosition(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight()- 220);
    	audio_slider.setValue(0.05f);
    	menuButton.setPosition(MENU_buttonX, Gdx.graphics.getHeight() - 40);
    	lobbyButton.setPosition(MENU_lobbyButtonX, Gdx.graphics.getHeight() - 75);
    	settingsButton.setPosition(MENU_settingsButtonX, lobbyButton.getY() - 35);
    	context.gameStage.addActor(menuButton);
    	context.gameStage.addActor(lobbyButton);
    	context.gameStage.addActor(settingsButton);
    	context.gameStage.addActor(audio_slider);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	fileChooser.setFileFilter(new FileNameExtensionFilter("txt file","txt"));
    	fileChooser.setDialogTitle("Select Map File");
    	createDialog();
		hideMenu();
    	initListeners();
    }

    public void createDialog() {
		table = new Table();
		sliderTable = new Table();
		disengageTable = new Table();
		qualityTable = new Table();
		mapTable = new Table();
		mapButtonTable = new Table();
		selectionTable = new Table();
		
		tableContainer = new Container<Table>();
		settingsScroller = new ScrollPane(tableContainer,skin);

		tableContainer.setActor(table);

		settingsScroller.setScrollingDisabled(true, false);
		settingsScroller.setFadeScrollBars(false);
		table.add(sliderTable).pad(2.0f).row();
		table.add(disengageTable).pad(2.0f).row();
		table.add(qualityTable).pad(2.0f).row();
		table.add(mapTable).pad(2.0f).row();
		table.add(mapButtonTable).pad(2.0f).row();
		//
		sliderTable.add(turnLabel).padLeft(50.0f).padRight(5.0f);
		sliderTable.add(getTurnSlider());
		sliderTable.add(turnText).expandX().padRight(50.0f).row();
		sliderTable.add(roundLabel).padLeft(50.0f).padRight(5.0f);
		sliderTable.add(getRoundSlider());
		sliderTable.add(roundText).expandX().pad(5.0f).padRight(50.0f).row();
		sliderTable.add(sinkLabel).padLeft(50.0f).padRight(5.0f);
		sliderTable.add(getRespawnSlider());
		sliderTable.add(respawnPenaltyText).expandX().pad(5.0f).padRight(50.0f).row();
		//
		disengageTable.add(disengageLabel).pad(5.0f).padRight(5.0f);
		disengageTable.add(getDisengageOffButton()).pad(5.0f);
		disengageTable.add(getDisengageRealisticButton()).pad(5.0f);
		disengageTable.add(getDisengageSimpleButton()).pad(5.0f).row();
		//
		qualityTable.add(jobberLabel).pad(5.0f).padRight(5.0f);
		qualityTable.add(getBasicQualityButton()).pad(5.0f);
		qualityTable.add(getEliteQualityButton()).pad(5.0f).row();
		cell = mapTable.add().colspan(3).expandX();
		mapTable.add().row();
		mapButtonTable.add(selectBox).pad(5.0f);
		mapButtonTable.add(customMapButton);
		selectionTable.add(proposeButton);
		selectionTable.add(exitButton);
		selectionTable.add(defaultButton);
		settingsDialog.getContentTable().add(settingsScroller).row();
		settingsDialog.getContentTable().add(selectionTable).row();
		
		//init settings
		settingsDialog.setMovable(true);
		settingsDialog.setResizable(true);
		settingsDialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    }
	/*
	 * Initialize listeners for actors of stage
	 */
    public void initListeners() {
    	menuButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			showMenu();
    		}
    	});
    	lobbyButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			hideMenu();
    			context.disconnect();
    			hideMenu();
    		}
    	});
    	settingsButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			settingsButton.setDisabled(true);
    			showDialog();
    		}
    	});
    	createDialogListeners();
    }
    
	/*
	 * Initialize image textures for image buttons
	 */
    public void initImageButtonStyles() {
    	menuUp = context.getManager().get(context.getAssetObject().menuUp);
        menuDown = context.getManager().get(context.getAssetObject().menuDown);
        lobbyUp = context.getManager().get(context.getAssetObject().lobbyUp);
        lobbyDown = context.getManager().get(context.getAssetObject().lobbyDown);
        settingsUp = context.getManager().get(context.getAssetObject().lobbyUp);
        settingsDown = context.getManager().get(context.getAssetObject().lobbyDown);
        menuButtonStyle = new ImageButtonStyle();
        menuButtonStyle.up = new TextureRegionDrawable(new TextureRegion(menuUp));
        menuButtonStyle.down = new TextureRegionDrawable(new TextureRegion(menuDown));
        menuButtonStyle.disabled = new TextureRegionDrawable(new TextureRegion(menuDown));
        menuButton = new ImageButton(menuButtonStyle);
        lobbyButtonStyle = new ImageButtonStyle();
        lobbyButtonStyle.up = new TextureRegionDrawable(new TextureRegion(lobbyUp));
        lobbyButtonStyle.down = new TextureRegionDrawable(new TextureRegion(lobbyDown));
        lobbyButtonStyle.disabled = new TextureRegionDrawable(new TextureRegion(lobbyDown));
        lobbyButton = new ImageButton(lobbyButtonStyle);
        settingsButtonStyle = new ImageButtonStyle();
        settingsButtonStyle.up = new TextureRegionDrawable(new TextureRegion(settingsUp));
        settingsButtonStyle.down = new TextureRegionDrawable(new TextureRegion(settingsDown));
        settingsButtonStyle.disabled = new TextureRegionDrawable(new TextureRegion(settingsDown));
        settingsButton = new ImageButton(settingsButtonStyle);
    }
    
    @Override
    public void create() {
        font = context.getManager().get(context.getAssetObject().menuFont);
    }
    
    @Override
    public void update() {
    }

    @Override
    public void render() {
        batch.begin();
        if(menuButton.isDisabled()) {
        	//overlay buttons with text
        	font.draw(batch,"Lobby",MENU_lobbyButtonX+25,MENU_lobbyButtonY+220);
        	font.draw(batch,"Game Settings",MENU_settingsButtonX+8,(MENU_lobbyButtonY+220)-35);
        }
        batch.end();
    }

    @Override
    public void dispose() {
    }
    
    /*
     * Fill dialog selectbox with map names
     */
    public void fillSelectBox() {
    	if(context.getMaps().size() !=0) {
    		mapStrings = new String[context.getMaps().size()]; 
        	for(int j =0;j<context.getMaps().size();j++){
        		mapStrings[j] = context.getMaps().get(j);
        	}
    		selectBox.setItems(mapStrings);
    		selectBox.setMaxListCount(6);
    		if(context.currentMapName != null) {
    			selectBox.setSelected(context.currentMapName);
    	        getContext().setMapNameSetting(selectBox.getSelected());
    		}
    	}  
    }
    
	/*
	 * Sets whether custom map has been selected or not
	 */
	public void setMapType(boolean mapBoolean) {
		this.mapType = mapBoolean;
	}
	
	/*
	 * Sets whether custom map has been selected or not
	 */
	public boolean isCustomMap() {
		return this.mapType;
	}
    /*
     * Initialize/create all listeners for buttons/sliders for dialog window
     */
    public void createDialogListeners() {
		settingsScroller.addListener(new InputListener() {
    		public void enter(InputEvent event, float x, float y, int pointer, Actor actor) {
    			getContext().gameStage.setScrollFocus(settingsScroller);
    		}
    		
    		public void exit(InputEvent event, float x, float y, int pointer, Actor actor) {
    			getContext().gameStage.setScrollFocus(null);
    		}
    	}); 
    	getDisengageOffButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("off", true);
                getContext().setDisengageSetting("off"); //disengage
        	}});
    	getDisengageRealisticButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("realistic", true);
            	getContext().setDisengageSetting("realistic");
        	}});
    	getDisengageSimpleButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("simple", true);
            	getContext().setDisengageSetting("simple");
        	}});
    	getBasicQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
            	clearQuality();
            	getCustomMapButton().setDisabled(false);
            	setQualityButton("basic", true);
            	getContext().setJobberSetting("basic");
        	}});
    	getEliteQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearQuality();
        		getCustomMapButton().setDisabled(false);
            	setQualityButton("elite", true);
            	getContext().setJobberSetting("elite");
        	}});
    	
    	proposeButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
				if(!turnText.getText().isEmpty()) {
					getContext().setTurnSetting(Integer.parseInt(turnText.getText())*10);
				}
				if(!roundText.getText().isEmpty()) {
					getContext().setRoundSetting(Integer.parseInt(roundText.getText())*10);
				}
				if(!respawnPenaltyText.getText().isEmpty()) {
					getContext().setRespawnSetting(Integer.parseInt(respawnPenaltyText.getText()));
				}
				getContext().setDisengageSetting(getCurrentDisengageButton());
				getContext().setJobberSetting(getCurrentQualityButton());
				getContext().setCustomMapSetting(isCustomMap()); //map
				getContext().setMapNameSetting(mapName); //map
				getContext().setMapSetting(customMap); //map
				context.sendSettingsPacket();
		    	settingsDialog.setVisible(false);
		    	showMenu();
			} 
    	});
    	
    	defaultButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		// set sliders
        		getTurnSlider().setValue(context.getDefaultTurnSetting());
        		getRoundSlider().setValue(context.getDefaultRoundSetting());
        		getRespawnSlider().setValue(context.getDefaultRespawnSetting());

				// reset buttons
        		clearDisengageBehavior();
        		clearQuality();
        		
        		setDisengageButton(context.getDefaultDisengageSetting(), true);
        		setQualityButton(context.getDefaultJobberSetting(), true);

            	// selected map
            	selectBox.setSelected(context.currentMapName);

            	// misc
            	getCustomMapButton().setDisabled(false);
			} 
    	});
    	exitButton.addListener(new ClickListener() {
    		@Override
            public void clicked(InputEvent event, float x, float y) {
    			getTurnSlider().setValue(context.getTurnSetting());
    			getRoundSlider().setValue(context.getRoundSetting());
    			getRespawnSlider().setValue(context.getRespawnSetting());
    			closeSettingsDialog();
    			showMenu();
    			
			} 
        });
    	turnText.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					getTurnSlider().setValue((float)Integer.parseInt(turnText.getText()));
				}catch(NumberFormatException e) {
					
				}
			}
		});
		roundText.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					getRoundSlider().setValue((float)Integer.parseInt(roundText.getText()));
				}catch(NumberFormatException e) {
					
				}
			}
		});
		respawnPenaltyText.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					getRespawnSlider().setValue((float)Integer.parseInt(respawnPenaltyText.getText()));
				}catch(NumberFormatException e) {
					
				}
			}
		});
		getTurnSlider().addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	turnText.setText(String.valueOf((int)getTurnSlider().getValue()));
            }});
		getRoundSlider().addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	roundText.setText(String.valueOf((int)getRoundSlider().getValue()));
            }});
		getRespawnSlider().addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	respawnPenaltyText.setText(String.valueOf((int)getRespawnSlider().getValue()));
            }});
		audio_slider.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	context.getBattleScene().setSound_volume((float)audio_slider.getValue());
            	//set user config volume
            	try {
					ConnectScene.changeProperty("user.config", "user.volume", Float.toString((float)audio_slider.getValue()));
				} catch (IOException e) {
					e.printStackTrace();
				}
            }});
		selectBox.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	try {
                	getCustomMapButton().setDisabled(false);
            		setMapType(false);
            		customMap = null;
            		mapName = selectBox.getSelected();
                	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
                	if(pixmap != null) {
	                	setMapPreview(pixmap);
                	}else {
                		clearMapPreview();
                	}
                	if(settingsScroller != null) {
                		settingsScroller.layout();	
                	}
            	}catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        });
		
		getCustomMapButton().addListener(new ClickListener() {
			@Override
            public void clicked(InputEvent event, float x, float y) {
				getCustomMapButton().setDisabled(true);
    			setMapType(true);
    			clearMapPreview();
    			selectCustomMap();
			}
		});
		selectBox.getScrollPane().addListener(new InputListener() {
    		public void enter(InputEvent event, float x, float y, int pointer, Actor actor) {
    			getContext().gameStage.setScrollFocus(selectBox.getScrollPane());
    		}
    		
    		public void exit(InputEvent event, float x, float y, int pointer, Actor actor) {
    			getContext().gameStage.setScrollFocus(settingsScroller);
    		}
    	});
		
    	turnText.setTextFieldFilter(new TextFieldFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) { //filter out letters and include range
				try {
					if(!Character.isDigit(c)) {
						return false;
					}
					if(Integer.parseInt(textField.getText() + c) <= turnMax && Character.isDigit(c)) {
						return true;
					}else if(Integer.parseInt(textField.getText() + c) > turnMax && Character.isDigit(c)) {
						textField.setText(Integer.toString(turnMax));
						getTurnSlider().setValue((float)turnMax);
						return false;
					}else {
						return false;
					}
				}
				catch(Exception e) {
					return true;
				}
			}
    	});
    	roundText.setTextFieldFilter(new TextFieldFilter() { //filter out letters and include range
			@Override
			public boolean acceptChar(TextField textField, char c) {
				try {
					if(!Character.isDigit(c)) {
						return false;
					}
					if(Integer.parseInt(textField.getText() + c) <= roundMax && Character.isDigit(c)) {
						return true;
					}else if(Integer.parseInt(textField.getText() + c) > roundMax && Character.isDigit(c)) {
						textField.setText(Integer.toString(roundMax));
						roundDuration_slider.setValue((float)roundMax);
						return false;
					}else {
						return false;
					}
				}
				catch(Exception e) {
					return true;
				}
			}
    	});
    	respawnPenaltyText.setTextFieldFilter(new TextFieldFilter() { //filter out letters and include range
			@Override
			public boolean acceptChar(TextField textField, char c) {
				try {
					if(!Character.isDigit(c)) {
						return false;
					}
					if(Integer.parseInt(textField.getText() + c) <= respawnPenaltyMax && Character.isDigit(c)) {
						return true;
					}else if(Integer.parseInt(textField.getText() + c) > respawnPenaltyMax && Character.isDigit(c)) {
						textField.setText(Integer.toString(respawnPenaltyMax));
						getRespawnSlider().setValue((float)respawnPenaltyMax);
						return false;
					}else { 
						return false;
					}
				}
				catch(Exception e) {
					return true;
				}
			}
    	});
		
    }
    
	/*
	 * User selects custom map file from their computer
	 */
    public void selectCustomMap() {
    	int[][] finalMap = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];
		int[][] tempTiles = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];
    	if(fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
    		File selectedFile = fileChooser.getSelectedFile();
    		if(selectedFile.length()/1024 < 5) {
    			int x = 0;
    	        int y = 0;
    	        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))) {
    	            String line;
    	            while ((line = br.readLine()) != null) {
    	                String[] split = line.split(",");
    	                for (String tile : split) {
    	                	tempTiles[x][y] = Integer.parseInt(tile);
    	                    x++;
    	                }
    	                x = 0;
    	                y++;
    	            }
    	            int x1 = 0;
    	            int y1 = 0;

    	            for (int i = 0; i < tempTiles.length; i++) {
    	                for (int j = tempTiles[i].length - 1; j > -1; j--) {
    	                    finalMap[x1][y1] = tempTiles[i][j];
    	                    y1++;
    	                }
    	                y1 = 0;
    	                x1++;
    	            }
    	        	customMap = finalMap;
    	        	mapName = selectedFile.getName();
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    		}else {
    			selectCustomMap();
    		}
    	}else {
    		return;
    	}
    }
    
    /*
     * Clears map preview with label
     */
    public void clearMapPreview() {
    	if(settingsDialog != null && cell != null && previewLabel != null) {
    		cell.setActor(previewLabel);	
    	}
    }
    
    /*
     * Shows the game menu
     */
    public void showMenu() {
		menuButton.setDisabled(true);
		lobbyButton.setDisabled(false);
		settingsButton.setDisabled(false);
		lobbyButton.setVisible(true);
		settingsButton.setVisible(true);
		audio_slider.setDisabled(false);
		audio_slider.setVisible(true);
    }
    
    /*
     * Hides the game menu
     */
    public void hideMenu() {
    	menuButton.setDisabled(false);
    	audio_slider.setDisabled(true);
    	lobbyButton.setDisabled(true);
    	settingsButton.setDisabled(true);
    	audio_slider.setVisible(false);
    	lobbyButton.setVisible(false);
    	settingsButton.setVisible(false);
    }
    /*
     * Sets map preview to selected map screenshot 
    */
    public void setMapPreview(Pixmap pixmap) {
    	if(settingsDialog != null && cell != null) {
        	cell.setActor(new Image(new Texture(pixmap)));
    	}
    }
    
    public void resizeSettingsDialog() {
    	if(selectBox.getSelectedIndex() != -1) {
        	settingsDialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        	settingsDialog.setPosition(Gdx.graphics.getWidth()/2 - (settingsDialog.getWidth()/2), Gdx.graphics.getHeight()/2 - (settingsDialog.getHeight()/4));
    	}else {
    		settingsDialog.setSize(DIALOG_WIDTH_1, DIALOG_HEIGHT_1);
    		settingsDialog.setPosition(Gdx.graphics.getWidth()/2 - (settingsDialog.getWidth()/2), Gdx.graphics.getHeight()/2 - (settingsDialog.getHeight()/4));	
    	}
    }
    /*
     * Create the actual popup dialog
     */
    public void showDialog() {
		fillSelectBox();
		getTurnSlider().setValue(context.getTurnSetting());
		getRoundSlider().setValue(context.getRoundSetting());
		getRespawnSlider().setValue(context.getRespawnSetting());
		turnText.setText(Integer.toString(context.getTurnSetting()));
		roundText.setText(Integer.toString(context.getRoundSetting()));
		respawnPenaltyText.setText(Integer.toString(context.getRespawnSetting()));
    	for (TextButton button: disengageBehaviorGroup.getButtons()) {
    		if(button.getText().toString().toLowerCase().equals(context.getDisengageSetting())) {
    			button.setDisabled(true);
    		}
    	}
    	
    	for (TextButton button: jobberQualityGroup.getButtons()) {
    		if(button.getText().toString().toLowerCase().equals(context.getJobberSetting())) {
    			button.setDisabled(true);
    		}
    	}
    	settingsDialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		settingsDialog.show(context.gameStage);
		settingsDialog.setVisible(true);
		if(selectBox.getSelectedIndex() != -1) {
	    	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
	    	if(pixmap != null) {
	    		setMapPreview(pixmap);
	    	}else {
	    		clearMapPreview();
	    	}
		}
		settingsDialog.pack();
		settingsScroller.layout();
		
    }
    
    @Override
    public boolean handleClick(float x, float y, int button) {
    	if(menuButton.isDisabled() && !isClickingMenuTable(x,y)){
    		hideMenu();
        	return false;
        }
        else {
            return false;
        }
    	
    }
    /**
     * Helper method, flip array horizontally
     */
    public static int[][] flipArray(int[][] theArray) {
        for(int i = 0; i < (theArray.length / 2); i++) {
            int[] temp = theArray[i];
            theArray[i] = theArray[theArray.length - i - 1];
            theArray[theArray.length - i - 1] = temp;
        }
        return theArray;
    }
    
    /**
     * Helper method, flipv vertically
     */
    public static int[][] inverseArray(int[][] matrix)
    {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] transposedMatrix = new int[n][m];

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < m; j++)
            {
                transposedMatrix[i][j] = matrix[j][i];
            }
        }

        return transposedMatrix;
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

	/*
	 * Is clicking designated menu area
	 */
    public boolean isClickingMenuTable(float x, float y) {
        return isPointInRect(x,y,menuTable_Shape);
    }
    
	/*
	 * Is clicking designated menu button
	 */
    public boolean isClickingMenuButton(float x, float y) {
        return isPointInRect(x,y,menuButton_Shape);
    }
    
    @Override
    public boolean handleDrag(float x, float y, float ix, float iy) {
        return false;
    }

    @Override
    public boolean handleRelease(float x, float y, int button) {
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

	/*
	 * Getter and setters for textfields/sliders
	 */
	public Slider getTurnSlider() {
		return turnDuration_slider;
	}
	
	public Slider getRoundSlider() {
		return roundDuration_slider;
	}
	
	public Slider getRespawnSlider() {
		return sinkPenalty_slider;
	}
	
	public String getCurrentDisengageButton() {
    	for (TextButton button: disengageBehaviorGroup.getButtons()) {
    		if(button.isDisabled()) {
    			return (button.getText()).toString().toLowerCase();
    		}
    	}
    	return null;
	}
	
	public void setDisengageButton(String description, boolean toggle) {
		switch (description)
		{
		     case "simple":
		    	 getDisengageSimpleButton().setDisabled(toggle);
		    	 break;
		     case "realistic":
		    	 getDisengageRealisticButton().setDisabled(toggle);
		    	 break;
		     case "off":
		    	 getDisengageOffButton().setDisabled(toggle);
		    	 break;
		     default:
		    	 getDisengageOffButton().setDisabled(toggle);
		    	 break;
		}
	}
	
	public void clearDisengageBehavior() {
    	for (TextButton button: disengageBehaviorGroup.getButtons()) {
    		button.setDisabled(false);
    	}
	}
	
	public TextButton getDisengageOffButton() {
		return disengageOff;
	}
	
	public TextButton getDisengageRealisticButton() {
		return disengageRealistic;
	}
	
	public TextButton getDisengageSimpleButton() {
		return disengageSimple;
	}
	
	public String getCurrentQualityButton() {
    	for (TextButton button: jobberQualityGroup.getButtons()) {
    		if(button.isDisabled()) {
    			return (button.getText()).toString().toLowerCase();
    		}
    	}
    	return null;
	}
	
	public void setQualityButton(String description, boolean toggle) {
		switch (description)
		{
		     case "basic":
		    	 getBasicQualityButton().setDisabled(toggle);
		    	 break;
		     case "elite":
		    	 getEliteQualityButton().setDisabled(toggle);
		    	 break;
		     default:
		    	 getBasicQualityButton().setDisabled(toggle);
		    	 break;
		}
	}
	
	public void clearQuality() {
    	for (TextButton button: jobberQualityGroup.getButtons()) {
    		button.setDisabled(false);
    	}
	}
	
	public TextButton getBasicQualityButton() {
		return basicQuality;
	}
	
	public TextButton getEliteQualityButton() {
		return eliteQuality;
	}
	
	public TextButton getCustomMapButton() {
		return customMapButton;
	}
	
	public void setCustomMapButton(int value) {
		switch (value)
		{
			case 0:
				getCustomMapButton().setDisabled(false);
				break;
			case 1:
				getCustomMapButton().setDisabled(true);
				break;
			default:
				getCustomMapButton().setDisabled(false);
				break;
		}
	}
}
