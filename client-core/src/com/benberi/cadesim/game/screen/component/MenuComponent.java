package com.benberi.cadesim.game.screen.component;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.vessel.Vessel;
import com.benberi.cadesim.game.screen.SeaBattleScreen;
import com.benberi.cadesim.game.screen.impl.battle.map.BlockadeMap;
import com.benberi.cadesim.util.Team;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

public class MenuComponent extends SeaBattleScreen implements InputProcessor {
	private GameContext context;
    /**
     * Textures
     */
    private Texture menuUp;
    private Texture menuDown;
    private Texture buttonUp;
    private Texture buttonDown;
    
    private BitmapFont font;
    
    // reference coords - menu control
    private int MENU_REF_X       = 0;
    private int MENU_REF_Y       = 0;
    private int MENU_buttonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 36);
    private int TEAM_tableX;
    private int TEAM_tableY;
    private int MENU_tableX     = MENU_REF_X + (Gdx.graphics.getWidth() - 80);
    @SuppressWarnings("unused")
	private int MENU_tableY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 321);
    
    private int MENU_lobbyButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_lobbyButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 273);
    
    private int MENU_settingsButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    
	
    // DISENGAGE shapes
    Rectangle menuButton_Shape;
    Rectangle menuTable_Shape;
    Rectangle teamTable_Shape;
    Rectangle menuLobby_Shape;
    Rectangle menuMap_Shape;

	private SelectBox<String> selectBox;
	private Dialog settingsDialog;
	private String[] mapStrings;
	private int[][] customMap;
	private boolean mapType = false;
	private String mapName = "None";

	Texture texture;
	private ImageButton menuButton;
	private ImageButtonStyle menuButtonStyle;
	private ImageButton settingsButton;
	private ImageButtonStyle buttonStyle;
	private ImageButton lobbyButton;
	private ImageButton teamButton;
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
	//buttons to select AI
	private TextButton aiOff;
	private TextButton aiEasyDifficulty;
	private TextButton aiMediumDifficulty;
	private TextButton aiHardDifficulty;
	//buttons to select job quality
	private TextButton basicQuality;
	private TextButton eliteQuality;
	private TextButton customMapButton;
	//option buttons
	private TextButton proposeButton ;
	private TextButton exitButton;
	private TextButton defaultButton;
	private TextButton attackerTeamButton;
	private TextButton defenderTeamButton;
	private ScrollPane settingsScroller;
	private ScrollPane defenderScroller;
	private ScrollPane attackerScroller;
	private Table table;
	private Table sliderTable;
	public Table teamTable;
	private Table listTables;
	private Table teamButtonTable;
	private Table mapButtonTable;
	private Table disengageTable;
	private Table qualityTable;
	private Table aiTable;
	private Table mapTable;
	private Table selectionTable;
	private Container<Table> tableContainer;
	private Label disengageLabel;
	private Label aiLabel;
	private Label turnLabel;
	private Label roundLabel;
	private Label sinkLabel;
	private Label jobberLabel;
	private List<String> defenderList;
	private String[] defenderNames;
	private List<String> attackerList;
	private String[] attackerNames;
	
	private float audioMax = 0.25f;
	private int turnMax = 60;
	private int roundMax = 7200;
	private int respawnPenaltyMax = 10;

	private int DIALOG_WIDTH_1 = 523;
	private int DIALOG_HEIGHT_1 = 331;
	
	private ButtonGroup<TextButton> disengageBehaviorGroup;
	private ButtonGroup<TextButton> jobberQualityGroup;
	private ButtonGroup<TextButton> aiGroup;
	private Stage stage;
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
	
    public MenuComponent(GameContext context, Stage stage) {
    	super(context);
        this.context = context;
        this.stage = stage;
        Skin sliderskin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        settingsDialog = new Dialog("Game Settings", skin, "dialog");
        menuTable_Shape = new Rectangle(MENU_tableX, 0, 80, 400);
        menuButton_Shape = new Rectangle(MENU_buttonX, 13, 25, 25);
        initImageButtonStyles();
        aiLabel = new Label("AI Difficulty:",skin);
		disengageLabel = new Label("Disengage Behavior:",skin);
		turnLabel = new Label("Turn Duration (seconds):",skin);
		roundLabel = new Label("Round Duration (seconds):",skin);
		sinkLabel = new Label("Sink Penalty (turns):",skin);
		jobberLabel = new Label("Jobber Quality:", skin);
		
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

    	aiGroup = new ButtonGroup<TextButton>();
    	disengageBehaviorGroup = new ButtonGroup<TextButton>();
    	jobberQualityGroup = new ButtonGroup<TextButton>();
    	
    	aiOff = new TextButton("Off",skin);
    	aiOff.getStyle().disabled = aiOff.getStyle().down;
    	aiEasyDifficulty = new TextButton("Easy",skin);
    	aiEasyDifficulty.getStyle().disabled = aiEasyDifficulty.getStyle().down;
    	aiMediumDifficulty = new TextButton("Medium",skin);
    	aiMediumDifficulty.getStyle().disabled = aiMediumDifficulty.getStyle().down;
    	aiHardDifficulty = new TextButton("Hard",skin);
    	aiHardDifficulty.getStyle().disabled = aiHardDifficulty.getStyle().down;
    	
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
    	aiGroup.add(aiOff,aiEasyDifficulty,aiMediumDifficulty,aiHardDifficulty);
    	proposeButton = new TextButton("Propose",skin);
    	exitButton = new TextButton("Exit",skin);
    	defaultButton = new TextButton("Reset defaults",skin);
    	menuButton.setPosition(Gdx.graphics.getWidth() - 36, Gdx.graphics.getHeight() - 40);
    	lobbyButton.setPosition(Gdx.graphics.getWidth() - 76, Gdx.graphics.getHeight() - 75);
    	settingsButton.setPosition(Gdx.graphics.getWidth() - 76, lobbyButton.getY() - 35);
    	teamButton.setPosition(Gdx.graphics.getWidth() - 76, settingsButton.getY() - 35);
    	audio_slider.setSize(10, 100);
    	audio_slider.setPosition(Gdx.graphics.getWidth()-30, teamButton.getY() - 110);
    	audio_slider.setValue(0.05f);
    	stage.addActor(menuButton);
    	stage.addActor(lobbyButton);
    	stage.addActor(settingsButton);
    	stage.addActor(teamButton);
    	stage.addActor(audio_slider);	
    	createDialog();
    	createTeams();
		hideMenu();
    	initListeners();
    }
    
    public void createTeams() {
    	teamTable = new Table();
    	listTables = new Table();
    	teamButtonTable = new Table();
    	
    	attackerTeamButton = new TextButton("Attacker",skin);
    	defenderTeamButton = new TextButton("Defender",skin);
    	
    	attackerList = new List<String>(skin);
    	defenderList = new List<String>(skin);
    	attackerScroller = new ScrollPane(attackerList,skin);
    	defenderScroller = new ScrollPane(defenderList,skin);

    	attackerNames = new String[] {""};
    	defenderNames = new String[] {""};
    	
    	attackerList.setItems(attackerNames);
    	defenderList.setItems(defenderNames);
    	
    	teamButtonTable.add(attackerTeamButton).padBottom(2.0f).padLeft(8f).padRight(10.0f);
    	teamButtonTable.add(defenderTeamButton).padBottom(2.0f);
    	listTables.add(attackerScroller).width(150).height(200).padRight(10.0f);
    	listTables.add(defenderScroller).width(150).height(200);
    	teamTable.add(teamButtonTable).row();
    	teamTable.add(listTables);
    	teamTable.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
    	stage.addActor(teamTable);
    	teamTable.setVisible(false);
        TEAM_tableX     = MENU_REF_X + (Gdx.graphics.getWidth()/2 - 160);
        TEAM_tableY     = MENU_REF_Y + (Gdx.graphics.getHeight()/2 - 125);
        teamTable_Shape = new Rectangle(TEAM_tableX, TEAM_tableY, 350, 250);
    }
	/*
	 * Create settings dialog
	 */
    public void createDialog() {
		table = new Table();
		sliderTable = new Table();
		disengageTable = new Table();
		qualityTable = new Table();
		mapTable = new Table();
		aiTable = new Table();
		mapButtonTable = new Table();
		selectionTable = new Table();
		
		tableContainer = new Container<Table>();
    	tableContainer.setActor(table);
		settingsScroller = new ScrollPane(tableContainer,skin);
    	
		settingsScroller.setScrollingDisabled(true, false);
		settingsScroller.setFadeScrollBars(false);
		
    	//settings table
		table.add(sliderTable).pad(2.0f).row();
		table.add(disengageTable).pad(2.0f).row();
		table.add(qualityTable).pad(2.0f).row();
		table.add(aiTable).pad(2.0f).row();
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
		
		aiTable.add(aiLabel).pad(5.0f).padRight(5.0f);
		aiTable.add(getAIOffButton()).pad(5.0f);
		aiTable.add(getAIEasyDifficultyButton()).pad(5.0f);
		aiTable.add(getAIMediumDifficultyButton()).pad(5.0f);
		aiTable.add(getAIHardDifficultyButton()).pad(5.0f).row();
		
		mapTable.add().row();
		mapButtonTable.add(selectBox).pad(5.0f);
		mapButtonTable.add(customMapButton);
		//buttons
		selectionTable.add(proposeButton);
		selectionTable.add(exitButton);
		selectionTable.add(defaultButton);
		settingsDialog.getContentTable().add(settingsScroller).row();
		settingsDialog.getContentTable().add(selectionTable).row();
		
		//init settings
		settingsDialog.setMovable(true);
		settingsDialog.setResizable(true);
		resizeSettingsDialog();
    }
	/*
	 * Fill list with proper teams
	 */
    public void fillTeamList() {
	    	attackerList.getItems().clear();
	    	defenderList.getItems().clear();
			ArrayList<Vessel> attacker = new ArrayList<Vessel>();
			ArrayList<Vessel> defender = new ArrayList<Vessel>();
			for(Vessel vessel : context.getEntities().listVesselEntities()) {
	    		if(vessel.getTeam() == Team.ATTACKER) {
	    			attacker.add(vessel);
	    		}else {
	    			defender.add(vessel);
	    		}
			}
	    	attackerNames = new String[attacker.size()];
	    	defenderNames = new String[defender.size()];
	    	int i = 0;
	    	for(Vessel vessel : attacker) {
	    		if(vessel.getTeam() == Team.ATTACKER) {
	    			attackerNames[i] = vessel.getName();
	    		}
	    		i++;
	    	}
	    	int j = 0;
	    	for(Vessel vessel : defender) {
	    		if(vessel.getTeam() == Team.DEFENDER) {
	    			defenderNames[j] = vessel.getName();
	    		}
	    		j++;
	    	}
	    	attackerList.setItems(attackerNames);
	    	defenderList.setItems(defenderNames);
    }
	/*
	 * Initialize listeners for actors of stage
	 */
    public void initListeners() {
    	teamButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			if(!teamButton.isDisabled()) {
        			teamButton.setDisabled(true);
        			teamTable.setVisible(true);	
    			}
    		}
    	});
    	attackerTeamButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			context.sendTeamPacket(Team.ATTACKER.getID());
    		}
    	});
    	defenderTeamButton.addListener(new ClickListener() {
    		@Override
    		public void clicked(InputEvent event, float x, float y) {
    			context.sendTeamPacket(Team.DEFENDER.getID());
    		}
    	});
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
        buttonUp = context.getManager().get(context.getAssetObject().buttonUp);
        buttonDown = context.getManager().get(context.getAssetObject().buttonDown);
        menuButtonStyle = new ImageButtonStyle();
        menuButtonStyle.up = new TextureRegionDrawable(new TextureRegion(menuUp));
        menuButtonStyle.down = new TextureRegionDrawable(new TextureRegion(menuDown));
        menuButtonStyle.disabled = new TextureRegionDrawable(new TextureRegion(menuDown));
        menuButton = new ImageButton(menuButtonStyle);
        buttonStyle = new ImageButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUp));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDown));
        buttonStyle.disabled = new TextureRegionDrawable(new TextureRegion(buttonDown));
        lobbyButton = new ImageButton(buttonStyle);
        settingsButton = new ImageButton(buttonStyle);
        teamButton = new ImageButton(buttonStyle);
    }
    
    public void buildStage() {
        font = context.getManager().get(context.getAssetObject().menuFont);
    }
    
    public void show() {
    }

    public void render(float delta) {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true);
    	stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.act();
        stage.draw();
        stage.getBatch().begin();
        if(menuButton.isDisabled()) {
        	//overlay buttons with text
        	font.draw(stage.getBatch(),"Lobby",MENU_lobbyButtonX+25,MENU_lobbyButtonY+220);
        	font.draw(stage.getBatch(),"Game Settings",MENU_settingsButtonX+8,(MENU_lobbyButtonY+220)-35);
        	font.draw(stage.getBatch(),"Select Team",MENU_settingsButtonX+13,(MENU_lobbyButtonY+220)-70);
        }
        stage.getBatch().end();
    }

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
    	        context.setMapNameSetting(selectBox.getSelected());
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
    			stage.setScrollFocus(settingsScroller);
    		}
    		
    		public void exit(InputEvent event, float x, float y, int pointer, Actor actor) {
    			stage.setScrollFocus(null);
    		}
    	});
		getAIOffButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearAI();
        		getCustomMapButton().setDisabled(false);
        		setAIButton("off",true);
        		context.setAISetting("off");
        	}});
    	getAIEasyDifficultyButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearAI();
        		getCustomMapButton().setDisabled(false);
        		setAIButton("easy",true);
        		context.setAISetting("easy");
        	}});
    	getAIMediumDifficultyButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearAI();
        		getCustomMapButton().setDisabled(false);
        		setAIButton("medium",true);
        		context.setAISetting("medium");
        	}});
    	getAIHardDifficultyButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearAI();
        		getCustomMapButton().setDisabled(false);
        		setAIButton("hard",true);
        		context.setAISetting("hard");
        	}});
    	getDisengageOffButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("off", true);
                context.setDisengageSetting("off"); //disengage
        	}});
    	getDisengageRealisticButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("realistic", true);
            	context.setDisengageSetting("realistic");
        	}});
    	getDisengageSimpleButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("simple", true);
            	context.setDisengageSetting("simple");
        	}});
    	getBasicQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
            	clearQuality();
            	getCustomMapButton().setDisabled(false);
            	setQualityButton("basic", true);
            	context.setJobberSetting("basic");
        	}});
    	getEliteQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearQuality();
        		getCustomMapButton().setDisabled(false);
            	setQualityButton("elite", true);
            	context.setJobberSetting("elite");
        	}});
    	
    	proposeButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
				if(!turnText.getText().isEmpty()) {
					context.setTurnSetting(Integer.parseInt(turnText.getText())*10);
				}
				if(!roundText.getText().isEmpty()) {
					context.setRoundSetting(Integer.parseInt(roundText.getText())*10);
				}
				if(!respawnPenaltyText.getText().isEmpty()) {
					context.setRespawnSetting(Integer.parseInt(respawnPenaltyText.getText()));
				}
				context.setDisengageSetting(getCurrentDisengageButton());
				context.setJobberSetting(getCurrentQualityButton());
				context.setCustomMapSetting(isCustomMap()); //map
				context.setMapNameSetting(mapName); //map
				context.setMapSetting(customMap); //map
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
            	context.getBattleScreen().setSound_volume((float)audio_slider.getValue());
            	//set user config volume
				changeProperty("user.config", "user.volume", Float.toString((float)audio_slider.getValue()));
			
            }});
		selectBox.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	try {
                	getCustomMapButton().setDisabled(false);
            		setMapType(false);
            		customMap = null;
            		mapName = selectBox.getSelected();
                	if(settingsScroller != null) {
                		settingsScroller.layout();	
                	}
                	resizeSettingsDialog();
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
    			selectCustomMap();
			}
		});
		selectBox.getScrollPane().addListener(new InputListener() {
    		public void enter(InputEvent event, float x, float y, int pointer, Actor actor) {
    			stage.setScrollFocus(selectBox.getScrollPane());
    		}
    		
    		public void exit(InputEvent event, float x, float y, int pointer, Actor actor) {
    			stage.setScrollFocus(settingsScroller);
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
    	FileChooser fileChooser = new FileChooser(Mode.OPEN);
    	fileChooser.setSize(500, 300);
        fileChooser.setDirectory(System.getProperty("user.home"));
        FileTypeFilter typeFilter = new FileTypeFilter(true); //allow "All Types" mode where all files are shown
        typeFilter.addRule("Text files (*.txt)", "txt");
        fileChooser.setFileTypeFilter(typeFilter);
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setListener(new FileChooserAdapter() {
        	@Override
        	public void selected(Array<FileHandle> file) {
        		File selectedFile = file.first().file();
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
        	}
        });
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
		teamButton.setVisible(true);
		audio_slider.setDisabled(false);
		audio_slider.setVisible(true);
		//only allow user to change team in safe zone
		if(context.myVesselY < 3 || context.myVesselY > 32) {
			teamButton.setDisabled(false);
		}else {
			teamButton.setDisabled(true);
		}
    }
    
    /*
     * Hides the game menu
     */
    public void hideMenu() {
    	menuButton.setDisabled(false);
    	audio_slider.setDisabled(true);
    	lobbyButton.setDisabled(true);
    	settingsButton.setDisabled(true);
    	teamButton.setDisabled(false);
    	audio_slider.setVisible(false);
    	lobbyButton.setVisible(false);
    	settingsButton.setVisible(false);
    	teamButton.setVisible(false);
    }
    
    public void resizeSettingsDialog() {
    	settingsDialog.setSize(DIALOG_WIDTH_1, DIALOG_HEIGHT_1);
    	settingsDialog.setPosition(Gdx.graphics.getWidth()/2 - (settingsDialog.getWidth()/2), Gdx.graphics.getHeight()/2 - (settingsDialog.getHeight()/4));	
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
		settingsDialog.pack();
		settingsScroller.layout();
		resizeSettingsDialog();
    	stage.addActor(settingsDialog);
    	settingsDialog.setVisible(true);
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
    
	/*
	 * Is clicking teams area
	 */
    public boolean isClickingTeamTable(float x, float y) {
        return isPointInRect(x,y,teamTable_Shape);
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
	public boolean scrolled(float amountx, float amounty) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int arg2, int arg3) {
    	if(menuButton.isDisabled()){
    		if(!teamTable.isVisible() && !isClickingMenuTable(x,y)) {
    			hideMenu();	
    		}else if(menuButton.isDisabled() && !isClickingTeamTable(x,y)) {
    			teamTable.setVisible(false);
    			hideMenu();	
    		}
        	return false;
        }
        else {
            return false;
        }
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
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
	
	public void clearAI() {
    	for (TextButton button: aiGroup.getButtons()) {
    		button.setDisabled(false);
    	}
	}
	
	public void setAIButton(String description, boolean toggle) {
		switch (description)
		{
		     case "off":
		    	 getAIOffButton().setDisabled(toggle);
		    	 break;
		     case "easy":
		    	 getAIEasyDifficultyButton().setDisabled(toggle);
		    	 break;
		     case "medium":
		    	 getAIMediumDifficultyButton().setDisabled(toggle);
		    	 break;
		     case "hard":
		    	 getAIHardDifficultyButton().setDisabled(toggle);
		    	 break;
		     default:
		    	 getAIEasyDifficultyButton().setDisabled(toggle);
		    	 break;
		}
	}

	public TextButton getAIOffButton() {
		return aiOff;
	}
	
	public TextButton getAIEasyDifficultyButton() {
		return aiEasyDifficulty;
	}
	
	public TextButton getAIMediumDifficultyButton() {
		return aiMediumDifficulty;
	}
	
	public TextButton getAIHardDifficultyButton() {
		return aiHardDifficulty;
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
	
	public static void changeProperty(String filename, String key, String value){
        Properties prop =new Properties();
        try {
            prop.load(new FileInputStream(filename));
            prop.setProperty(key, value);
            prop.store(new FileOutputStream(filename),null);
		}
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
    }
}
