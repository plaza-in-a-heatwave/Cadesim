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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.scene.SceneComponent;
import com.benberi.cadesim.game.scene.impl.battle.map.BlockadeMap;

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
    private Texture mapUp;
    private Texture mapDown;
    
    private BitmapFont font;
    
    // reference coords - menu control
    private int MENU_REF_X       = 0;
    private int MENU_REF_Y       = 0;
    private int MENU_buttonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 36);
    private int MENU_buttonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 238);

    private int MENU_tableX     = MENU_REF_X + (Gdx.graphics.getWidth() - 80);
    @SuppressWarnings("unused")
	private int MENU_tableY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 321);
    
    private int MENU_lobbyButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_lobbyButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 273);
    
    private int MENU_mapsButtonX     = MENU_REF_X + (Gdx.graphics.getWidth() - 76);
    private int MENU_mapsButtonY     = MENU_REF_Y + (Gdx.graphics.getHeight() - 308);
    
	
    // DISENGAGE shapes
    Rectangle menuButton_Shape;
    Rectangle menuTable_Shape;
    Rectangle menuLobby_Shape;
    Rectangle menuMap_Shape;

    /**
     * state of buttons. true if pushed, false if not.
     */
    private boolean menuButtonIsDown = false; // initial
	private boolean menuLobbyIsDown = false; // initial
	private boolean menuMapsIsDown = false; // initial
    
    private JFileChooser fileChooser;
	public Stage stage;
	private SelectBox<String> selectBox;
	private InputProcessor input;
	private Dialog dialog;
	public Skin skin;
	private String[] mapStrings;
	private int[][] customMap;
	private boolean mapBoolean = false;
	private String mapName = "None";

	Texture texture;
	
	//Settings buttons (sliders)
	public Slider audio_slider;
	private Slider turnDuration_slider;
	private Slider roundDuration_slider;
	private Slider sinkPenalty_slider;
	private TextField turnText;
	private TextField roundText;
	private TextField sinkPenaltyText;
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
	
	private Table table;
	private Table sliderTable;
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
	private int sinkPenaltyMax = 10;
	
	private ButtonGroup<TextButton> disengageBehaviorGroup;
	private ButtonGroup<TextButton> jobberQualityGroup;
	
	/**
	 * Allow other parts of Cadesim to close the dialog
	 */
	public void closeSettingsDialog() {
	    //sanity check if user chooses 0 for round/turn duration
        if(context.getProposedRoundDuration() == 0) {
            context.setProposedRoundDuration(1);
        }else if(context.getProposedTurnDuration() == 0) {
            context.setProposedTurnDuration(1);
        }
        dialog.getContentTable().clear();
        dialog.setVisible(false);
        Gdx.input.setInputProcessor(input);
        menuButtonIsDown = false;
        menuLobbyIsDown = false;
        menuMapsIsDown = false;
        customMap = null;
        setMapBoolean(false);
        customMapButton.setDisabled(false);
	}

	/**
	 * Allow other parts of Cadesim to check whether the dialog is open
	 */
	public boolean isSettingsDialogOpen() {
	    return dialog.isVisible();
	}

    protected MenuComponent(GameContext context, SeaBattleScene owner) {
        super(context, owner);
        this.context = context;
    	stage = new Stage();
        batch = new SpriteBatch();
        fileChooser = new JFileChooser();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Skin sliderskin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        dialog = new Dialog("Game Settings", skin, "dialog");
        menuButton_Shape = new Rectangle(MENU_buttonX, 13, 25, 25);
        menuTable_Shape = new Rectangle(MENU_tableX, 30, 80, 200);
        menuLobby_Shape = new Rectangle(MENU_lobbyButtonX, 50, 70, 22);
        menuMap_Shape = new Rectangle(MENU_mapsButtonX, 80, 70, 22);
        
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
    	sinkPenalty_slider = new Slider(0.0f, (float)sinkPenaltyMax, 1.0f, false, skin);
    	turnDuration_slider.setValue((float)context.getProposedTurnDuration());
    	roundDuration_slider.setValue((float)context.getProposedRoundDuration());
    	sinkPenalty_slider.setValue((float)context.getProposedSinkPenalty());
    	turnText = new TextField("",skin);
    	turnText.setMaxLength(5);
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
    	roundText = new TextField("",skin);
    	roundText.setMaxLength(5);
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
    	sinkPenaltyText = new TextField("",skin);
    	sinkPenaltyText.setMaxLength(5);
    	sinkPenaltyText.setTextFieldFilter(new TextFieldFilter() { //filter out letters and include range
			@Override
			public boolean acceptChar(TextField textField, char c) {
				try {
					if(!Character.isDigit(c)) {
						return false;
					}
					if(Integer.parseInt(textField.getText() + c) <= sinkPenaltyMax && Character.isDigit(c)) {
						return true;
					}else if(Integer.parseInt(textField.getText() + c) > sinkPenaltyMax && Character.isDigit(c)) {
						textField.setText(Integer.toString(sinkPenaltyMax));
						getSinkPenaltySlider().setValue((float)sinkPenaltyMax);
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
    	
    	turnText.setText(String.valueOf(context.getProposedTurnDuration()));
    	roundText.setText(String.valueOf(context.getProposedRoundDuration()));
    	sinkPenaltyText.setText(String.valueOf(context.getProposedSinkPenalty()));
    	
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
    	for (TextButton button: disengageBehaviorGroup.getButtons()) {
    		if(button.getText().toString().toLowerCase().equals(context.getProposedDisengageBehavior())) {
    			button.setDisabled(true);
    		}
    	}
    	
    	for (TextButton button: jobberQualityGroup.getButtons()) {
    		if(button.getText().toString().toLowerCase().equals(context.getProposedJobberQuality())) {
    			button.setDisabled(true);
    		}
    	}
    	
    	proposeButton = new TextButton("Propose",skin);
    	exitButton = new TextButton("Exit",skin);
    	defaultButton = new TextButton("Reset defaults",skin);
    	audio_slider.setSize(10, 100);
    	audio_slider.setPosition(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight()- 220);
    	stage.addActor(audio_slider);
		createDialogListeners();
    }

    @Override
    public void create() {
        menuUp = context.getManager().get(context.getAssetObject().menuUp);
        menuDown = context.getManager().get(context.getAssetObject().menuDown);
        lobbyUp = context.getManager().get(context.getAssetObject().lobbyUp);
        lobbyDown = context.getManager().get(context.getAssetObject().lobbyDown);
        mapUp = context.getManager().get(context.getAssetObject().mapsUp);
        mapDown = context.getManager().get(context.getAssetObject().mapsDown);
        font = context.getManager().get(context.getAssetObject().menuFont);
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
        	font.draw(batch,"Game Settings",MENU_mapsButtonX+8,MENU_mapsButtonY+21);
        }
        batch.end();
        
        stage.act();
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void dispose() {
    }
    
    public void setup() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(context.getInputProcessor());
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
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
    			context.setProposedMapName(selectBox.getSelected());
    		}
    	}  
    }
    
	public void setMapBoolean(boolean mapBoolean) {
		this.mapBoolean = mapBoolean;
	}
	
    /*
     * Initialize/create all listeners for buttons/sliders
     */
    public void createDialogListeners() {
    	getDisengageOffButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("off", true);
            	context.setProposedDisengageBehavior("off");
        	}});
    	getDisengageRealisticButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("realistic", true);
            	context.setProposedDisengageBehavior("realistic");
        	}});
    	getDisengageSimpleButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearDisengageBehavior();
        		getCustomMapButton().setDisabled(false);
            	setDisengageButton("simple", true);
            	context.setProposedDisengageBehavior("simple");
        	}});
    	getBasicQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
            	clearQuality();
            	getCustomMapButton().setDisabled(false);
            	setQualityButton("basic", true);
            	context.setProposedJobberQuality("basic");
        	}});
    	getEliteQualityButton().addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		clearQuality();
        		getCustomMapButton().setDisabled(false);
            	setQualityButton("elite", true);
            	context.setProposedJobberQuality("elite");
        	}});
    	
    	proposeButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        		//sanity check if user chooses 0 for round/turn duration
        		if(context.getProposedRoundDuration() == 0) {
        			context.setProposedRoundDuration(1);
        		}else if(context.getProposedTurnDuration() == 0) {
        			context.setProposedTurnDuration(1);
        		}
				if(!roundText.getText().isEmpty()) {
					context.setProposedRoundDuration(Integer.parseInt(roundText.getText()));
				}
				if(!turnText.getText().isEmpty()) {
					context.setProposedTurnDuration(Integer.parseInt(turnText.getText()));
				}
				if(!sinkPenaltyText.getText().isEmpty()) {
					context.setProposedSinkPenalty(Integer.parseInt(sinkPenaltyText.getText()));
				}
				context.setProposedSinkPenalty(Integer.parseInt(sinkPenaltyText.getText()));
				context.setProposedSinkPenalty(Integer.parseInt(sinkPenaltyText.getText()));
            	context.setProposedMapName(selectBox.getSelected());
        		dialog.getContentTable().clear();
        		context.sendSettingsPacket(customMap,mapBoolean, mapName);
		    	dialog.setVisible(false);
	    		menuButtonIsDown = false;
	    		menuLobbyIsDown = false;
	        	menuMapsIsDown = false;  
			} 
    	});
    	
    	defaultButton.addListener(new ClickListener() {
        	@Override
            public void clicked(InputEvent event, float x, float y) {
        	    // set boxes
				turnText.setText(String.valueOf(context.getDefaultTurnDuration()));
        		roundText.setText(String.valueOf(context.getDefaultRoundDuration()));
        		sinkPenaltyText.setText(String.valueOf(context.getDefaultSinkPenalty()));

        		// set sliders
        		getTurnSlider().setValue((float)context.getDefaultTurnDuration());
        		getRoundSlider().setValue((float)context.getDefaultRoundDuration());
        		getSinkPenaltySlider().setValue((float)context.getDefaultSinkPenalty());

				// reset buttons
        		clearDisengageBehavior();
        		clearQuality();
        		
        		setDisengageButton(context.getDefaultDisengageBehavior(), true);
        		setQualityButton(context.getDefaultJobberQuality(), true);

            	// selected map
            	selectBox.setSelected(context.currentMapName);

            	// misc
            	getCustomMapButton().setDisabled(false);
			} 
    	});
    	exitButton.addListener(new ClickListener() {
    		@Override
            public void clicked(InputEvent event, float x, float y) {
    			getTurnSlider().setValue((float)context.getProposedTurnDuration());
    			getRoundSlider().setValue((float)context.getProposedRoundDuration());
    			getSinkPenaltySlider().setValue((float)context.getProposedSinkPenalty());
    			closeSettingsDialog();
    			
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
		sinkPenaltyText.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				try {
					getSinkPenaltySlider().setValue((float)Integer.parseInt(sinkPenaltyText.getText()));
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
		getSinkPenaltySlider().addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	sinkPenaltyText.setText(String.valueOf((int)getSinkPenaltySlider().getValue()));
            }});
		audio_slider.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	context.getBattleScene().setSound_volume((float)audio_slider.getValue());
            }});
		selectBox.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            	try {
                	getCustomMapButton().setDisabled(false);
            		setMapBoolean(false);
            		customMap = null;
                	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
                	if(pixmap != null) {
	                	setMapPreview(pixmap);
                	}else {
                		clearMapPreview();
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
    			setMapBoolean(true);
    			clearMapPreview();
    			selectCustomMap();
			}
		});
    }
    
    public void selectCustomMap() {
		int[][] tempTiles = new int[BlockadeMap.MAP_WIDTH][BlockadeMap.MAP_HEIGHT];
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	fileChooser.setFileFilter(new FileNameExtensionFilter("txt file","txt"));
    	fileChooser.setDialogTitle("Select Map File");
    	int result = fileChooser.showOpenDialog(null);
    	if(result == JFileChooser.APPROVE_OPTION) {
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
    	            tempTiles = flipArray(tempTiles);
    	        	customMap = tempTiles;
    	        	mapName = selectedFile.getName();
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    		}else {
    			selectCustomMap();
    		}
    	}
    }
    
    /*
     * Clears map preview with label
     */
    public void clearMapPreview() {
    	if(dialog != null && cell != null && previewLabel != null) {
            dialog.setSize(413, 331);
    		dialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2 - 100);
    		cell.setActor(previewLabel);	
    	}
    }
    
    /*
     * Sets map preview to selected map screenshot 
    */
    public void setMapPreview(Pixmap pixmap) {
    	if(dialog != null && cell != null) {
    		Texture textureMap = new Texture(pixmap);
        	Image map = new Image(textureMap);
        	cell.setActor(map);
        	dialog.setSize(650, 575);
        	dialog.setPosition(Gdx.graphics.getWidth()/2-300, Gdx.graphics.getHeight()/2 - 280);
    	}
    }
    
    /*
     * Fill tables in the given popup dialog (game setings)
     */
    public void fillTable() {
		table = new Table();
		sliderTable = new Table();
		disengageTable = new Table();
		qualityTable = new Table();
		mapTable = new Table();
		selectionTable = new Table();
		tableContainer = new Container<Table>();
		tableContainer.setActor(table);
		table.add(sliderTable).pad(2.0f).row();
		table.add(disengageTable).pad(2.0f).row();
		table.add(qualityTable).pad(2.0f).row();
		table.add(mapTable).pad(2.0f).row();
		table.add(selectionTable).pad(2.0f).row();
		//
		sliderTable.add(turnLabel);
		sliderTable.add(getTurnSlider());
		sliderTable.add(turnText).width(60).pad(5.0f).row();
		sliderTable.add(roundLabel);
		sliderTable.add(getRoundSlider());
		sliderTable.add(roundText).width(60).pad(5.0f).row();
		sliderTable.add(sinkLabel);
		sliderTable.add(getSinkPenaltySlider());
		sliderTable.add(sinkPenaltyText).width(60).pad(5.0f).row();
		//
		disengageTable.add(disengageLabel).pad(5.0f);
		disengageTable.add(getDisengageOffButton()).pad(5.0f);
		disengageTable.add(getDisengageRealisticButton()).pad(5.0f);
		disengageTable.add(getDisengageSimpleButton()).pad(5.0f).row();
		//
		qualityTable.add(jobberLabel).pad(5.0f);
		qualityTable.add(getBasicQualityButton()).pad(5.0f);
		qualityTable.add(getEliteQualityButton()).pad(5.0f).row();
		cell = mapTable.add().colspan(3);
		mapTable.add().row();
		mapTable.add(selectBox).colspan(1);
		mapTable.add(customMapButton).colspan(2);
		selectionTable.add(proposeButton);
		selectionTable.add(exitButton);
		selectionTable.add(defaultButton);
		try {
	    	Pixmap pixmap = context.pixmapArray[selectBox.getSelectedIndex()];
	    	if(pixmap != null) {
	        	Texture textureMap = new Texture(pixmap);
	        	Image map = new Image(textureMap);
	        	cell.setActor(map);
	        	dialog.setSize(650, 575);
	        	dialog.setPosition(Gdx.graphics.getWidth()/2-300, Gdx.graphics.getHeight()/2 - 280);
	    	}else {
	    		dialog.setSize(413, 331);
	    		dialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2 - 100);
	    		cell.setActor(previewLabel);
	    	}
			dialog.getContentTable().add(tableContainer).row();
		}catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    /*
     * Create the actual popup dialog
     */
    public void createDialog() {
		dialog.setMovable(true);
		dialog.setResizable(true);
		dialog.setVisible(true);
		fillSelectBox();
		fillTable();
		stage.addActor(dialog);
		dialog.show(stage);
		if(cell.getActor() instanceof Label) {
			dialog.setSize(413, 331);
    		dialog.setPosition(Gdx.graphics.getWidth()/2-200, Gdx.graphics.getHeight()/2 - 100);
		}else {
			dialog.setSize(650, 575);
			dialog.setPosition(Gdx.graphics.getWidth()/2-300, Gdx.graphics.getHeight()/2 - 280);
		}
		input = Gdx.input.getInputProcessor();
		Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public boolean handleClick(float x, float y, int button) {
    	if ((!menuButtonIsDown) && isClickingMenuButton(x,y)) {
            menuButtonIsDown = true;
            setup();
            audio_slider.setVisible(true);
            return true;
        }
        else if(menuButtonIsDown && !isClickingMenuTable(x,y)){
        	menuButtonIsDown = false;
        	menuLobbyIsDown = false;
        	menuMapsIsDown = false;
        	audio_slider.setVisible(false);
        	return false;
        }
    	else if(menuButtonIsDown && isClickingLobbyButton(x,y)) {
    		menuLobbyIsDown = true;
    		context.disconnect();
    		return true;
    	}
    	else if(menuButtonIsDown && isClickingMapsButton(x,y)) {
    		menuMapsIsDown = true;
    		createDialog();
    		return true;
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

    public boolean isClickingMenuButton(float x, float y) {
        return isPointInRect(x,y,menuButton_Shape);
    }
    
    public boolean isClickingMenuTable(float x, float y) {
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

	public Slider getTurnSlider() {
		return turnDuration_slider;
	}
	
	public Slider getRoundSlider() {
		return roundDuration_slider;
	}
	
	public Slider getSinkPenaltySlider() {
		return sinkPenalty_slider;
	}
	
	public TextButton getCurrentDisengageButton() {
    	for (TextButton button: disengageBehaviorGroup.getButtons()) {
    		if(button.isDisabled()) {
    			return button;
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
	
	public TextButton getCurrentQualityButton() {
    	for (TextButton button: jobberQualityGroup.getButtons()) {
    		if(button.isDisabled()) {
    			return button;
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

	public boolean isMenuButtonIsDown() {
		return menuButtonIsDown;
	}
}
