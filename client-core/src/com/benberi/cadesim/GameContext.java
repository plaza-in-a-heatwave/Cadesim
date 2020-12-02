package com.benberi.cadesim;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.client.ClientConnectionCallback;
import com.benberi.cadesim.client.ClientConnectionTask;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketHandler;
import com.benberi.cadesim.client.packet.OutgoingPacket;
import com.benberi.cadesim.client.packet.in.LoginResponsePacket;
import com.benberi.cadesim.client.packet.out.*;
import com.benberi.cadesim.game.cade.Team;
import com.benberi.cadesim.game.entity.EntityManager;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.scene.impl.connect.ConnectScene;
import com.benberi.cadesim.game.scene.impl.connect.ConnectionSceneState;
import com.benberi.cadesim.game.scene.GameScene;
import com.benberi.cadesim.game.scene.SceneAssetManager;
import com.benberi.cadesim.game.scene.TextureCollection;
import com.benberi.cadesim.game.scene.impl.battle.MenuComponent;
import com.benberi.cadesim.game.scene.impl.battle.SeaBattleScene;
import com.benberi.cadesim.game.scene.impl.control.ControlAreaScene;
import com.benberi.cadesim.game.scene.impl.mapeditor.MapEditorMapScene;
import com.benberi.cadesim.game.scene.impl.mapeditor.MapEditorMenuScene;
import com.benberi.cadesim.input.GameInputProcessor;
import com.benberi.cadesim.util.GameToolsContainer;
import com.benberi.cadesim.util.RandomUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameContext {
    private Channel serverChannel;
    public InputMultiplexer inputMultiplexer;
    public boolean clear;
    public boolean isInLobby = true;

    private int shipId = 0;
    private int islandId = 0;
    /**
     * to allow client to display popup messages properly
     */
    private boolean haveServerResponse = false;

    /**
     * Constants to be populated by the server
     */
    private int turnDuration;
    
    public int getTurnDuration() {
    	return turnDuration;
    }
    
    public void setTurnDuration(int value) {
    	turnDuration = value;
    }
	
	private ArrayList<Object> gameSettings = new ArrayList<Object>();

    private SceneAssetManager assetManager;

    /**
     * The main input processor of the game
     */
    private GameInputProcessor input;

    /**
     * The sea battle scene
     */
    private SeaBattleScene seaBattleScene;

    /**
     * The control area scene
     */
    private ControlAreaScene controlArea;
    
    /**
     * The map editor scene
     */
    private MapEditorMapScene mapEditor;
    private MapEditorMenuScene mapEditorMenu;

    /**
     * The texture collection
     */
    private TextureCollection textures;

    /**
     * The entity manager
     */
    private EntityManager entities;

    public String myVessel;
    public int myVesselY;
    
    public int myVesselType;

    /**
     * List of scenes
     */
    private List<GameScene> scenes = new ArrayList<GameScene>();
    
    /**
     * List of maps
     */
    private List<String> maps = new ArrayList<String>();
    public Pixmap[] pixmapArray = new Pixmap[1];
    public String currentMapName;
    /**
     * If connected to server
     */
    private boolean connected = false;
    
    /**
     * If started map editor
     */
    private boolean isInMapEditor = false;

    /**
     * Executors service
     */
    private ExecutorService service = Executors.newSingleThreadExecutor();

    /**
     * Public GSON object
     */
    private GameToolsContainer tools;

    private ClientPacketHandler packets;

    private ConnectScene connectScene;
    public Team myTeam;
    
    public void setTeam(int value) {
    	if(value == 0) {
    		this.myTeam = Team.ATTACKER;
    	}else {
    		this.myTeam = Team.DEFENDER;
    	}
    }
    
    public ChannelPipeline pipeline;

	private boolean clientDisconnected;

	/**
	 * Keep track of how lagged the client is respective to the server.
	 */
	private byte lagCounter = 0;
	
	/**
	 * Set lag counter to arbitrary value.
	 */
	public void setLagCounter(byte value) {
	    lagCounter = value;
	}

	/**
	 * return the lag counter, as-is.
	 *
	 * use this method to inspect the lag counter.
	 */
	public byte getLagCounter() {
	    return lagCounter;
	}

	/**
	 * return the lag counter, incremented.
	 * When responding to lag requests from the server, use this method
	 * to generate a value you can return.
	 */
	public byte getNextLagCounter() {
	    return ++lagCounter;
	}

	/**
	 * A test mode for the client. Simulate a crash scenario where packets are not dispatched.
	 * Use to test resilience to lag/crashes & ensure players are kicked where appropriate.
	 * 
	 * Mode is hard-baked into client, but must be activated with
	 *     setLagTestMode() and the ALLOW_LAG_TEST_MODE constant.
	 */
	private boolean lagTestMode = false;
	@SuppressWarnings("unused")
    public boolean isLagTestMode() {
        return (Constants.ENABLE_LAG_TEST_MODE && lagTestMode);
    }
    @SuppressWarnings("unused")
    public void setLagTestMode(boolean lagTestMode) {
        this.lagTestMode = (Constants.ENABLE_LAG_TEST_MODE && lagTestMode);
    }

    public Stage gameStage;
    public GameContext(BlockadeSimulator main) {
        this.tools = new GameToolsContainer();

        entities = new EntityManager(this);
        // init client
        this.packets = new ClientPacketHandler(this);
        inputMultiplexer = new InputMultiplexer();
        gameStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    /**
     * Create!
     */
    public void create() {
        assetManager = new SceneAssetManager();
        assetManager.loadConnectSceneTextures();
        assetManager.loadAllShipTextures();
        assetManager.loadShipInfo();
        assetManager.loadSounds();
        assetManager.loadSeaBattle();
        assetManager.loadFonts();
        assetManager.loadControl();
        assetManager.manager.finishLoading();

        textures = new TextureCollection(this);
        textures.create();

        this.connectScene = new ConnectScene(this);
        connectScene.create();
    }

    public List<GameScene> getScenes() {
        return this.scenes;
    }
    
    public List<String> getMaps() {
        return this.maps;
    }
    
    public EntityManager getEntities() {
        return this.entities;
    }

    /**
     * Gets the tools container
     * @return {@link #tools}
     */
    public GameToolsContainer getTools() {
        return this.tools;
    }

    /**
     * Handles the incoming packet from the server
     * @param o The incoming packet
     */
    public void handlePacket(Packet o) {
    }
    
    /**
     * Gets the texture collection
     * @return {@link #textures}
     */
    public TextureCollection getTextures() {
         return this.textures;
    }

    /**
     * Gets the packet handler
     * @return {@link #packets}
     */
    public ClientPacketHandler getPacketHandler() {
        return packets;
    }
    
    public void createMapEditorScene() {
    	this.input = new GameInputProcessor(this);
        this.mapEditor = new MapEditorMapScene(this);
        this.mapEditorMenu = new MapEditorMenuScene(this);
        mapEditor.create();
        mapEditor.createEmptyMap();
        scenes.add(mapEditor);
        mapEditorMenu.create();
        scenes.add(mapEditorMenu);
    }
    public void createFurtherScenes(int shipId) {
    	gameStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    	ControlAreaScene.shipId = shipId;
    	this.input = new GameInputProcessor(this);
        inputMultiplexer.addProcessor(this.input);
        this.seaBattleScene = new SeaBattleScene(this);
        seaBattleScene.create();
        this.controlArea = new ControlAreaScene(this);
        controlArea.create();
        //fix crash on server disconnect after dispose has been called
		if(scenes.size() == 0) {
	        scenes.add(controlArea);
	        scenes.add(seaBattleScene);
		}else {
	        scenes.set(0, controlArea);
	        scenes.set(1, seaBattleScene);
		}
		Gdx.graphics.setTitle("GC: " + myVessel + " (" + myTeam + ")");
    }

    public MapEditorMapScene getMapEditor() {
        return (MapEditorMapScene) scenes.get(0);
    }

    public SeaBattleScene getBattleScene() {
        return (SeaBattleScene) scenes.get(1);
    }
    
    public ControlAreaScene getControlScene() {
        return (ControlAreaScene) scenes.get(0);
    }
    
    public MenuComponent getBattleSceneMenu() {
    	if(scenes.get(1) instanceof SeaBattleScene) {
        	SeaBattleScene seaBattle = (SeaBattleScene) scenes.get(1);
            return seaBattle.mainmenu; 
    	}
        return null;
    }
    public boolean isConnected() {
        return connected;
    }
    
    public boolean isInMapEditor() {
        return isInMapEditor;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public Channel getServerChannel() {
        return this.serverChannel;
    }

    /**
     * Sends a packet
     * @param p The packet to send
     */
    public void sendPacket(OutgoingPacket p) {
        // #83 if lag test mode, simulate unresponsiveness
        if (isLagTestMode()) {
            return;
        }
        
        p.encode();
        serverChannel.write(p);
        serverChannel.flush();
    }

    /**
     * Gets the connection scene
     * @return  {@link #connectScene}
     */
    public ConnectScene getConnectScene() {
        return connectScene;
    }

    /**
     * Sends a login packet to the server with the given display name
     * @param display   The display name
     */
    public void sendLoginPacket(String code, String display, int ship, int team) {
        LoginPacket packet = new LoginPacket();
        packet.setVersion(Constants.PROTOCOL_VERSION);
        packet.setCode(code);
        packet.setName(display);
        packet.setShip(ship);
        packet.setTeam(team);
        sendPacket(packet);
        shipId = ship;
    }

    /**
     * Sends a move placement packet
     * @param slot  The slot to place
     * @param move  The move to place
     */
    public void sendSelectMoveSlot(int slot, MoveType move) {
        PlaceMovePacket packet = new PlaceMovePacket();
        packet.setSlot(slot);
        packet.setMove(move.getId());
        sendPacket(packet);
    }
    
    /**
     * sends a move swap packet
     * basically sending 2x sendSelectMoveSlot at once to avoid spamming with packets.
     */
    public void sendSwapMovesPacket(int slot1, int slot2)
    {
        SwapMovesPacket packet = new SwapMovesPacket();
        packet.set(slot1, slot2);
        sendPacket(packet);
    }

    /**
     * Attempts to connect to server
     *
     * @param displayName   The display name
     * @param ip            The IP Address to connect
     * @throws UnknownHostException 
     */
    public void connect(final String displayName, String ip, String code, int ship, int team) throws UnknownHostException {
    	haveServerResponse = false; // reset for next connect
    	if(!RandomUtils.validIP(ip) && RandomUtils.validUrl(ip)) {
    		try {
	    		InetAddress address = InetAddress.getByName(ip); 
	    		ip = address.getHostAddress();
    		} catch(UnknownHostException e) {
    			return;
    		}
    	}
        service.execute(new ClientConnectionTask(this, ip, new ClientConnectionCallback() {
            @Override
            public void onSuccess(Channel channel) {
                serverChannel = channel; // initialize the server channel
                connectScene.setState(ConnectionSceneState.CREATING_PROFILE);
                sendLoginPacket(code, displayName, ship, team); // send login packet
                myVessel = displayName;
                myVesselType = ship;
                myTeam = Team.forId(team);
            }

            @Override
            public void onFailure() {
                // only show if server appears dead
                if (!haveServerResponse) {
                	connectScene.loginFailed();
                }
            }
        }));
    }

    /**
     * Handles a login response form the server
     *
     * @param response  The response code
     */
    public void handleLoginResponse(int response) {
        if (response != LoginResponsePacket.SUCCESS) {
        	getServerChannel().disconnect();
        	haveServerResponse = true;

            switch (response) {
                case LoginResponsePacket.BAD_VERSION:
                    connectScene.setPopup("Outdated client", false);
                    break;
                case LoginResponsePacket.NAME_IN_USE:
                    connectScene.setPopup("Display name already in use", false);
                    break;
                case LoginResponsePacket.BAD_SHIP:
                    connectScene.setPopup("The selected ship is not allowed", false);
                    break;
                case LoginResponsePacket.SERVER_FULL:
                    connectScene.setPopup("The server is full", false);
                    break;
                case LoginResponsePacket.BAD_NAME:
                	connectScene.setPopup("That ship name is not allowed", false);
                	break;
                default:
                    connectScene.setPopup("Unknown login failure", false);
                    break;
            }

            connectScene.setState(ConnectionSceneState.DEFAULT);
        }
        else {
        	createFurtherScenes(shipId);
            try {
				if(ConnectScene.getProperty("user.config","user.volume") != null || ConnectScene.getProperty("user.config","user.volume").matches("[0-9]{3,}")){
					getBattleScene().mainmenu.audio_slider.setValue(Float.parseFloat(ConnectScene.getProperty("user.config","user.volume")));
					getBattleScene().setSound_volume(Float.parseFloat(ConnectScene.getProperty("user.config","user.volume")));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            connectScene.setState(ConnectionSceneState.CREATING_MAP);
        }

    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        Gdx.input.setInputProcessor(input);
        clear = true;
    }
    
    public void setStartedMapEditor(boolean started) {
        this.isInMapEditor = started;
        clear = true;
    }
    
    public boolean isStartedMapEditor() {
        return this.isInMapEditor;
    }

	public GameInputProcessor getInputProcessor() {
    	return input;
    }

	public void dispose() {
		try {
			if (entities != null) {
				entities.dispose();
			}
			getScenes().clear(); // clear other scenes
			connectScene.setup();
			if (getIsInLobby()) {
				System.out.println("Returned to lobby");
			}else {
				connectScene.setPopup("You have disconnected from the server.", true);
			}
			
			// #83 reset any lag test mode if we disconnect.
			if (Constants.ENABLE_LAG_TEST_MODE) {
			    setLagTestMode(false);
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

    public void sendBlockingMoveSlotChanged(int blockingMoveSlot) {
        BlockingMoveSlotChanged packet = new BlockingMoveSlotChanged();
        packet.setSlot(blockingMoveSlot);
        sendPacket(packet);
    }

    public void sendAddCannon(int side, int slot) {
        PlaceCannonPacket packet = new PlaceCannonPacket();
        packet.setSide(side);
        packet.setSlot(slot);
        sendPacket(packet);
    }

    public void sendToggleAuto(boolean auto) {
        AutoSealGenerationTogglePacket packet = new AutoSealGenerationTogglePacket();
        packet.setToggle(auto);
        sendPacket(packet);
    }

    public void sendGenerationTarget(MoveType targetMove) {
        SetSealGenerationTargetPacket packet = new SetSealGenerationTargetPacket();
        packet.setTargetMove(targetMove.getId());
        sendPacket(packet);
    }
    
    public void sendDisengageRequestPacket() {
    	OceansideRequestPacket packet = new OceansideRequestPacket();
    	sendPacket(packet);
    }
    
    public void sendPostMessagePacket(String message, String channel) {
        // #83 enable and disable lag test mode.
        if (Constants.ENABLE_LAG_TEST_MODE) {
            if (message.equals("/lagtestmode")) {
                setLagTestMode(!isLagTestMode());

                getControlScene().getBnavComponent().addNewMessage(
                        "lag test mode", (isLagTestMode()?"ENABLED":"DISABLED") +
                        ". type /lagtestmode to " + (isLagTestMode()?"disable":"enable") + "."
                );
                return;
            }
        }
        
    	PostMessagePacket packet = new PostMessagePacket();
    	packet.setMessage(message);
    	packet.setChannel(channel);
    	sendPacket(packet);
    }
    
    public void sendSettingsPacket() {
    	SendSettingsPacket packet = new SendSettingsPacket();
    	packet.setSettings(getGameSettings());
    	sendPacket(packet);
    }

    public void sendTeamPacket(int teamID) {
    	SetTeamPacket packet = new SetTeamPacket();
    	packet.setTeam(teamID);
    	sendPacket(packet);
    }
    /*
     * When the client (or user) decides to disconnect
     */
    public void disconnect() {
		gameStage.clear();
		inputMultiplexer.clear();
	    maps.clear();
    	setClientInitiatedDisconnect(true); // wedunnit!
        setConnected(false);
        setIsInLobby(true);
        getServerChannel().disconnect();
		getConnectScene().setState(ConnectionSceneState.DEFAULT);
		connectScene.setPopup("Returning to Lobby...", false);
		Gdx.graphics.setTitle("GC:" + Constants.VERSION);
		System.out.println("Client disconnected.");
	    getBattleScene().dispose();
	    getControlScene().dispose();
	    pixmapArray = null;
	    Gdx.graphics.setResizable(true);
    }
    /*
     * When the client (or user) decides to disconnect
     */
    public void exitMapEditor() {
    	Gdx.graphics.setResizable(true);
		setStartedMapEditor(false);
    	setClientInitiatedDisconnect(true); // wedunnit!
        setConnected(false);
        setIsInLobby(true);
		getConnectScene().setState(ConnectionSceneState.DEFAULT);
		getScenes().clear();
		getConnectScene().setup();
		Gdx.graphics.setTitle("GC: v" + Constants.VERSION);
    }
    /*
     * When the server decides to disconnect
     */
    public void handleServersideDisconnect() {
    	if(getServerResponse()) {
    		getServerChannel().disconnect();
    		System.out.println("Login error; handling response.");
    	}else {
	        setConnected(false);
	        setIsInLobby(true);
	        getServerChannel().disconnect();
			getConnectScene().setState(ConnectionSceneState.DEFAULT);
			connectScene.setPopup("Server Disconnected; Returning to Lobby...", false);
			System.out.println("Server disconnected.");
    	}
    }

    public SceneAssetManager getAssetObject() {
        return assetManager;
    }
    
    public AssetManager getManager() {
        return assetManager.manager;
    }
        
    /**
     * Sets isInLobby to boolean
     */
    public void setIsInLobby(boolean bool) {
        isInLobby = bool;
    }
    /**
     * Gets if user is in lobby
     * @return  {@link #boolean}
     */
    public boolean getIsInLobby() {
        return isInLobby;
    }

    /**
     * Gets server response
     */
    public boolean getServerResponse() {
    	return haveServerResponse;
    }

	public boolean clientInitiatedDisconnect() {
		return clientDisconnected;
	}

	public void setClientInitiatedDisconnect(boolean clientDisconnected) {
		this.clientDisconnected = clientDisconnected;
	}

	public int getIslandId() {
		return islandId;
	}

	public void setIslandId(int islandId) {
		this.islandId = islandId;
	}

	public ArrayList<Object> getGameSettings() {
		return gameSettings;
	}
	
	public int getTurnSetting() {
		return ((int)gameSettings.get(0)/10);
	}
	
	public void setTurnSetting(int value) {
		gameSettings.set(0, value);
	}
	
	public int getRoundSetting() {
		return ((int)gameSettings.get(1)/10);
	}
	
	public void setRoundSetting(int value) {
		gameSettings.set(1, value);
	}
	
	public int getRespawnSetting() {
		return (int)gameSettings.get(2);
	}
	
	public void setRespawnSetting(int value) {
		gameSettings.set(2, value);
	}
	
	public String getDisengageSetting() {
		return (String)gameSettings.get(3);
	}
	
	public void setDisengageSetting(String value) {
		gameSettings.set(3, value);
	}
	
	public String getJobberSetting() {
		return (String)gameSettings.get(4);
	}
	
	public void setJobberSetting(String value) {
		gameSettings.set(4, value);
	}
	
	public boolean getCustomMapSetting() {
		return (boolean)gameSettings.get(5);
	}
	
	public void setCustomMapSetting(boolean b) {
		gameSettings.set(5, b);
	}
	
	public String getMapNameSetting() {
		return (String)gameSettings.get(6);
	}
	
	public void setMapNameSetting(String value) {
		gameSettings.set(6, value);
	}
	
	public int[][] getMapSetting() {
		return (int[][])gameSettings.get(7);
	}
	
	public void setMapSetting(int[][] value) {
		gameSettings.set(7, value);
	}
	
	public int getDefaultTurnSetting() {
		return ((int)gameSettings.get(8)/10);
	}
	
	public int getDefaultRoundSetting() {
		return ((int)gameSettings.get(9)/10);
	}
	
	public int getDefaultRespawnSetting() {
		return (int)gameSettings.get(10);
	}
	
	public String getDefaultDisengageSetting() {
		return (String)gameSettings.get(11);
	}
	
	public String getDefaultJobberSetting() {
		return (String)gameSettings.get(12);
	}
}