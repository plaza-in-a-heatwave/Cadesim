package com.benberi.cadesim;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.benberi.cadesim.client.ClientConnectionCallback;
import com.benberi.cadesim.client.ClientConnectionTask;
import com.benberi.cadesim.client.codec.util.Packet;
import com.benberi.cadesim.client.packet.ClientPacketHandler;
import com.benberi.cadesim.client.packet.OutgoingPacket;
import com.benberi.cadesim.client.packet.in.LoginResponsePacket;
import com.benberi.cadesim.client.packet.out.*;
import com.benberi.cadesim.game.entity.EntityManager;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.screen.LoginScreen;
import com.benberi.cadesim.game.screen.component.BattleControlComponent;
import com.benberi.cadesim.game.screen.component.MenuComponent;
import com.benberi.cadesim.game.screen.SeaBattleScreen;
import com.benberi.cadesim.util.GameAssetManager;
import com.benberi.cadesim.util.GameToolsContainer;
import com.benberi.cadesim.util.RandomUtils;
import com.benberi.cadesim.util.ScreenEnum;
import com.benberi.cadesim.util.ScreenManager;
import com.benberi.cadesim.util.SqlDatabase;
import com.benberi.cadesim.util.Team;
import com.benberi.cadesim.util.TextureCollection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameContext {

	private Channel serverChannel;
    private ClientConnectionTask connectTask;

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

    private GameAssetManager assetManager;

    /**
     * The sea battle scene
     */
    private SeaBattleScreen seaBattleScreen;
    
    private LoginScreen lobbyScreen;

    private BattleControlComponent controlArea;

    private MenuComponent battleMenu;
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
    public List<String> admins;
    public Map<Integer, String> serverInfo = new HashMap<Integer, String>();
    public String currentServerRoom = "";
    public String accountName = "";
    public String hostURL;
    public int myVesselType;
    public Team myTeam;
    public String serverRoom;
    
    /**
     * List of maps
     */
    private List<String> maps = new ArrayList<String>();
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
    final Graphics graphics = Gdx.graphics;
    
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

    public GameContext(BlockadeSimulator main) {
        this.tools = new GameToolsContainer();

        entities = new EntityManager(this);
        // init client
        this.packets = new ClientPacketHandler(this);
        create();
    }

    /**
     * Create!
     */
    public void create() {
        assetManager = new GameAssetManager();
        assetManager.loadStartup();
        assetManager.loadFonts();
        assetManager.loadConnectSceneTextures();
        assetManager.manager.finishLoading();
    }
    
    public void loadSeaAssets() {
    	assetManager.loadSkinShipTexture();
    	assetManager.loadSeaAssets();
        textures = new TextureCollection(this);
        textures.loadSeaTextures();
    }
    
    public void loadGameAssets() {
        assetManager.loadSeaBattleAssets();
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

    public void createFurtherScenes(int shipId) {
    	GameContext context = this;
    	Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {	
				graphics.setResizable(false);
				ScreenManager.getInstance().showScreen(ScreenEnum.GAME, context);
				graphics.setTitle("GC: " + myVessel + " (" + myTeam + ")");
			}
    		
    	});
    }
    
    public void showBattle() {
    	GameContext context = this;
    	Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {	
				graphics.setResizable(false);
				ScreenManager.getInstance().showScreen(ScreenEnum.GAME, context);
				graphics.setTitle("GC: " + myVessel + " (" + myTeam + ")");
			}
    		
    	});
    }

    public void setLobbyScreen(LoginScreen lobby) {
    	lobbyScreen = lobby;
    }
    
    public LoginScreen getLobbyScreen() {
        return lobbyScreen;
    }
    
    public void setBattleScreen(SeaBattleScreen seaBattle) {
        seaBattleScreen = seaBattle;
    }
    
    public SeaBattleScreen getBattleScreen() {
        return seaBattleScreen;
    }
    
    public void setControl(BattleControlComponent control) {
    	controlArea = control;
    }
    
    public BattleControlComponent getControl() {
    	return controlArea;
    }
    
    public void setBattleMenu(MenuComponent menu) {
    	battleMenu = menu;
    }
    
    public MenuComponent getBattleMenu() {
    	return battleMenu;
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
     * Sends a login packet to the server with the given display name
     * @param display   The display name
     */
    public void sendLoginPacket(String accountName, String display, int ship, int team) {
        LoginPacket packet = new LoginPacket();
        packet.setVersion(Constants.PROTOCOL_VERSION);
        packet.setAccountName(accountName);
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
    public void connectdb(String username, String password) {
    	SqlDatabase.connect(username, password, context); // do something with server info
    }
    
    int response;
    /**
     * Attempts to connect to server
     *
     * @param displayName   The display name
     * @param ip            The IP Address to connect
     * @throws UnknownHostException 
     */
	GameContext context = this;
    public void connect(final String accountName, final String displayName, String ip, int ship, int team) throws UnknownHostException {
    	haveServerResponse = false; // reset for next connect
    	if(!RandomUtils.validIP(ip) && RandomUtils.validUrl(ip)) {
    		try {
	    		InetAddress address = InetAddress.getByName(ip); 
	    		ip = address.getHostAddress();
    		} catch(UnknownHostException e) {
    			return;
    		}
    	}
    	connectTask = new ClientConnectionTask(this, ip, new ClientConnectionCallback() {
            @Override
            public void onSuccess(Channel channel) {
                serverChannel = channel; // initialize the server channel
                sendLoginPacket(accountName, displayName, ship, team); // send login packet
                myVessel = displayName;
                myVesselType = ship;
                myTeam = Team.forId(team);
            }

            @Override
            public void onFailure() {
                // only show if server appears dead
                if (!haveServerResponse) {
                	getLobbyScreen().loginFailed();
                }
            	Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						graphics.setResizable(true);
					}
            	});
            }
        });
        getService().execute(connectTask);
    }

    /**
     * Handles a login response form the server
     *
     * @param response  The response code
     */
    public String handleLoginResponse(int response) {
        if (response != LoginResponsePacket.SUCCESS) {
        	getServerChannel().disconnect();
        	haveServerResponse = true;
            switch (response) {
                case LoginResponsePacket.BAD_VERSION:
                	return "Outdated client.";
                case LoginResponsePacket.NAME_IN_USE:
                	Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
		                	ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, context);
						}
                		
                	});
                	return "Display name already in use.";
                case LoginResponsePacket.BAD_SHIP:
                	Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
		                	ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, context);
						}
                		
                	});
                	return "The selected ship is not allowed.";
                case LoginResponsePacket.SERVER_FULL:
                	ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
                	return "The server is full.";
                case LoginResponsePacket.BAD_NAME:
                	Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
		                	ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, context);
						}
                		
                	});
                	return "That ship name is not allowed.";
                default:
                	ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
                	return "Unknown login failure.";
            }

        }
        else {
        	createFurtherScenes(shipId);
        	String volume = getVolumeProperty();
			if(volume == null){
				System.out.println("null pointer while getting volume");
			}else if(volume != null || volume.matches("[0-9]{3,}")) {
				getBattleScreen().battleMenu.audio_slider.setValue(Float.parseFloat(volume));
				getBattleScreen().setSound_volume(Float.parseFloat(volume));
			}
			return null;
        }
    }

    public String getVolumeProperty(){
    	Properties prop =new Properties();
        try {
			prop.load(new FileInputStream("user.config"));
			return prop.getProperty("user.volume");
        }
		catch(FileNotFoundException e) {
			System.out.println("File not found.");
		}
        catch(IOException e) {
			
		}
        return null;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
        clear = true;
    }
    
    public void setStartedMapEditor(boolean started) {
        this.isInMapEditor = started;
        clear = true;
    }
    
    public boolean isStartedMapEditor() {
        return this.isInMapEditor;
    }

	public void dispose() {
		try {
			if (entities != null) {
				entities.dispose();
			}
			if (getIsInLobby()) {
				System.out.println("Returned to lobby");
			}else {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						getLobbyScreen().setStatusMessage("Server: " + "You have disconnected from the server.");
					}
				});		
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

                getControl().addNewMessage(
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
	    maps.clear();
    	setClientInitiatedDisconnect(true); // wedunnit!
        setConnected(false);
        setIsInLobby(true);
        getServerChannel().disconnect();
        ScreenManager.getInstance().showScreen(ScreenEnum.SELECTION, this);
		Gdx.graphics.setTitle("GC:" + Constants.VERSION);
		System.out.println("Client disconnected.");
		getLobbyScreen().setStatusMessage("Client: " + "Client Disconnected.");
		getBattleScreen().dispose();
	    getControl().dispose();
	    Gdx.graphics.setResizable(true);
    }
    /*
     * When the client (or user) decides to disconnect
     */
    public void exitMapEditor() {
		setStartedMapEditor(false);
    	setClientInitiatedDisconnect(true); // wedunnit!
        setConnected(false);
        setIsInLobby(true);
        Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				graphics.setResizable(true);
				graphics.setTitle("GC: v" + Constants.VERSION);
			}
        	
        });
    }
    /*
     * When the server decides to disconnect
     */
    public void handleServersideDisconnect() {
    	if(getServerResponse()) {
    		getServerChannel().disconnect();
	        getLobbyScreen().setStatusMessage("Server: " + "Login error.");
    		System.out.println("Login error; handling response.");
    	}else {
	        setConnected(false);
	        setIsInLobby(true);
	        getServerChannel().disconnect();
	        getLobbyScreen().setStatusMessage("Server: " + "Server Disconnected.");
    	}
//
//    	Gdx.app.postRunnable(new Runnable() {
//			@Override
//			public void run() {
//		        ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN, context);
//		        graphics.setResizable(true);
//		        getLobbyScreen().setStatusMessage("Server: " + "Server Disconnected.");
//				graphics.setTitle("GC: v" + Constants.VERSION);
//			}
//    	});
    }

    public GameAssetManager getAssetObject() {
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
	
	public int getDefaultTurnSetting() {
		return ((int)gameSettings.get(0)/10);
	}
	
	public int getDefaultRoundSetting() {
		return ((int)gameSettings.get(1)/10);
	}
	
	public int getDefaultRespawnSetting() {
		return (int)gameSettings.get(2);
	}
	
	public String getDefaultDisengageSetting() {
		return (String)gameSettings.get(3);
	}
	
	public String getDefaultJobberSetting() {
		return (String)gameSettings.get(4);
	}
	
	public int getTurnSetting() {
		return ((int)gameSettings.get(5)/10);
	}
	
	public void setTurnSetting(int value) {
		gameSettings.set(5, value);
	}
	
	public int getRoundSetting() {
		return ((int)gameSettings.get(6)/10);
	}
	
	public void setRoundSetting(int value) {
		gameSettings.set(6, value);
	}
	
	public int getRespawnSetting() {
		return (int)gameSettings.get(7);
	}
	
	public void setRespawnSetting(int value) {
		gameSettings.set(7, value);
	}
	
	public String getDisengageSetting() {
		return (String)gameSettings.get(8);
	}
	
	public void setDisengageSetting(String value) {
		gameSettings.set(8, value);
	}
	
	public String getJobberSetting() {
		return (String)gameSettings.get(9);
	}
	
	public void setJobberSetting(String value) {
		gameSettings.set(9, value);
	}
	
	public boolean getCustomMapSetting() {
		return (boolean)gameSettings.get(10);
	}
	
	public void setCustomMapSetting(boolean b) {
		gameSettings.set(10, b);
	}
	
	public String getMapNameSetting() {
		return (String)gameSettings.get(11);
	}
	
	public void setMapNameSetting(String value) {
		gameSettings.set(11, value);
	}
	
	public int[][] getMapSetting() {
		return (int[][])gameSettings.get(12);
	}
	
	public void setMapSetting(int[][] value) {
		gameSettings.set(12, value);
	}
	
	public String getAISetting() {
		return (String)gameSettings.get(13);
	}
	
	public void setAISetting(String value) {
		gameSettings.set(13, value);
	}
	
	public ExecutorService getService() {
		return service;
	}
	
    public ClientConnectionTask getConnectTask() {
		return connectTask;
	}
    
    public void setTeam(int value) {
    	if(value == 0) {
    		this.myTeam = Team.ATTACKER;
    	}else {
    		this.myTeam = Team.DEFENDER;
    	}
    }
    
    public int getTeam() {
    	return this.myTeam.getID();
    }
    
    public void setVesselName(String name) {
    	myVessel = name;
    }
    
    public String getVesselName() {
    	return myVessel;
    }
    
    public void setAccountName(String name) {
    	accountName = name;
    }
    
    public String getAccountName() {
    	return accountName;
    }
    
    public void setHostURL(String url) {
    	hostURL = url;
    }
    
    public String getHostURL() {
    	return hostURL;
    }
    
    public void setVesselType(int vesselType) {
    	myVesselType = vesselType;
    }
    
    public int getVesselType() {
    	return myVesselType;
    }
    
    public String getServerRoom() {
    	return serverRoom;
    }
    
    public void setServerRoom(String room) {
    	serverRoom = room;
    }
}