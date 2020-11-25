package com.benberi.cadesim.game.scene;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.assets.AssetDescriptor;

public class SceneAssetManager {
	public final AssetManager manager = new AssetManager();
	
	/*
	 * Parameters for fonts
	 */
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterNotes = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterTitle = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterSea = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterControl = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterMessage = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterMenu = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	
	
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterInfoTeam = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterInfoPoints = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterInfoTime = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	public  FreetypeFontLoader.FreeTypeFontLoaderParameter parameterInfoBreak = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	
	public FreetypeFontLoader.FreeTypeFontLoaderParameter parameterMessageFont = 
			new FreetypeFontLoader.FreeTypeFontLoaderParameter();
	
	/*
	 * Font names - used to call the font
	 */
	private String REGULARFONT = "fontRegular.ttf";
	private String TITLEFONT = "fontTitle.ttf";
	private String NOTESFONT = "fontNotes.ttf";
	private String SEAFONT = "fontSea.ttf";
	private String CONTROLFONT = "fontControl.ttf";
	private String CHATFONT = "fontChat.ttf";
	private String MENUFONT = "fontMenu.ttf";
    private String FONTTEAMATTACKER = "fontTeamAttacker.ttf";
    private String FONTTEAMDEFENDER = "fontTeamDefender.ttf";
    private String FONTTEAMATTACKER_POINTS = "fontTeamAttackerPoints.ttf";
    private String FONTTEAMDEFENDER_POINTS = "fontTeamDefenderPoints.ttf";
    private String FONTTIME = "fontTime.ttf";
    private String FONTBREAK = "fontBreak.ttf";
    private String CHATMESSAGEFONT = "fontChatMessage.ttf";
	
    /*
	 * Ship texture location
	 */
	private final static String BAGHLAHSKIN = "skin/ships/baghlah.png";
	private final static String BLACKSHIPSKIN ="skin/ships/blackship.png";
	private final static String DHOWSKIN = "skin/ships/dhow.png";
	private final static String FANCHUANSKIN ="skin/ships/fanchuan.png";
	private final static String GRANDFRIGSKIN = "skin/ships/grandfrig.png";
	private final static String JUNKSKIN ="skin/ships/junk.png";
	private final static String LGSLOOPSKIN = "skin/ships/lgsloop.png";
	private final static String LONGSHIPSKIN ="skin/ships/longship.png";
	private final static String MERCHBRIGSKIN = "skin/ships/merchbrig.png";
	private final static String MERCHGALSKIN ="skin/ships/merchgal.png";
	private final static String SMSLOOPSKIN = "skin/ships/smsloop.png";
	private final static String WARBRIGSKIN ="skin/ships/warbrig.png";
	private final static String WARFRIGSKIN = "skin/ships/warfrig.png";
	private final static String WARGALSKIN ="skin/ships/wargal.png";
	private final static String XEBECSKIN = "skin/ships/xebec.png";
	
	private final static String BAGHLAH = "vessel/baghlah/sail.png";
	private final static String BLACKSHIP ="vessel/blackship/sail.png";
	private final static String DHOW = "vessel/dhow/sail.png";
	private final static String FANCHUAN ="vessel/fanchuan/sail.png";
	private final static String GRANDFRIG = "vessel/grandfrig/sail.png";
	private final static String JUNK ="vessel/junk/sail.png";
	private final static String LGSLOOP = "vessel/lgsloop/sail.png";
	private final static String LONGSHIP ="vessel/longship/sail.png";
	private final static String MERCHBRIG = "vessel/merchbrig/sail.png";
	private final static String MERCHGAL ="vessel/merchgal/sail.png";
	private final static String SMSLOOP = "vessel/smsloop/sail.png";
	private final static String WARBRIG ="vessel/warbrig/sail.png";
	private final static String WARFRIG = "vessel/warfrig/sail.png";
	private final static String WARGAL ="vessel/wargal/sail.png";
	private final static String XEBEC = "vessel/xebec/sail.png";
	
	private final static String BAGHLAH_SINKING = "vessel/baghlah/sink.png";
	private final static String DHOW_SINKING = "vessel/dhow/sink.png";
	private final static String FANCHUAN_SINKING ="vessel/fanchuan/sink.png";
	private final static String GRANDFRIG_SINKING = "vessel/grandfrig/sink.png";
	private final static String JUNK_SINKING = "vessel/junk/sink.png";
	private final static String LGSLOOP_SINKING = "vessel/lgsloop/sink.png";
	private final static String LONGSHIP_SINKING ="vessel/longship/sink.png";
	private final static String MERCHBRIG_SINKING = "vessel/merchbrig/sink.png";
	private final static String MERCHGAL_SINKING ="vessel/merchgal/sink.png";
	private final static String SMSLOOP_SINKING = "vessel/smsloop/sink.png";
	private final static String WARBRIG_SINKING ="vessel/warbrig/sink.png";
	private final static String WARFRIG_SINKING = "vessel/warfrig/sink.png";
	private final static String WARGAL_SINKING ="vessel/wargal/sink.png";
	private final static String XEBEC_SINKING = "vessel/xebec/sink.png";
	
	/*
	 * Map texture location
	 */
	private final static String CELL = "sea/cell.png";
	private final static String SAFE = "sea/safezone.png";
	private final static String SEA = "sea/sea1.png";
	private final static String ALKAID_ISLAND = "sea/alkaid_island.png";
	private final static String PUKRU_ISLAND = "sea/pukru_island.png";
	private final static String DOYLE_ISLAND = "sea/doyle_island.png";
	private final static String ISLE_KERIS_ISLAND = "sea/isle_keris_island.png";
	
	private final static String BIGROCK ="sea/rocks_big.png";
	private final static String SMALLROCK = "sea/rocks_small.png";
	private final static String WHIRLPOOL = "sea/whirl.png";
	private final static String WIND = "sea/wind.png";
	
	private final static String BIGROCK_DISABLED ="sea/rocks_big_disable.png";
	private final static String SMALLROCK_DISABLED = "sea/rocks_small_disable.png";
	private final static String WHIRLPOOL_DISABLED = "sea/whirl_disable.png";
	private final static String WIND_DISABLED = "sea/wind_disable.png";
	/*
	 * Cannon texture location
	 */
	private final static String CANNONBALL_LARGE = "projectile/cannonball_large.png";
    private final static String CANNONBALL_MEDIUM = "projectile/cannonball_medium.png";
    private final static String CANNONBALL_SMALL = "projectile/cannonball_small.png";
    private final static String SPLASH_LARGE = "effects/splash_large.png";
    private final static String SPLASH_SMALL = "effects/splash_small.png";
    private final static String EXPLODE_LARGE = "effects/explode_large.png";
    private final static String EXPLODE_MEDIUM = "effects/explode_medium.png";
    private final static String EXPLODE_SMALL = "effects/explode_small.png";
    private final static String HIT = "effects/hit.png";
    
	/*
	 * Misc texture location
	 */
    private final static String INFOPANEL = "ui/info.png";
    private final static String CONTENDERS = "cade/contender_icons.png";
    private final static String FLAG = "cade/buoy_symbols.png";
    private final static String FLAGTEXTURE = "cade/buoy.png";
    private final static String FLAGTEXTURE_DISABLED = "cade/buoy_disable.png";
    
    private final static String BACKGROUND = "client_bg.png";
    private final static String SMALLBACKGROUND = "client_bg_small.png";
    private final static String TEXTFIELDTEXTURE = "skin/textfield.png";
    private final static String LOGINBUTTON = "skin/login.png";
    private final static String LOGINBUTTONHOVER = "skin/login-hover.png";
    
    private final static String SEABATTLE_CURSOR = "skin/cursor.png";
    private final static String CURSOR = "skin/textfield-cursor.png";
    private final static String SELECTION  = "skin/textfield-selection.png";
    
    private final static String BATTLESELECTION = "skin/battle-textfield-selection.png";
    
    private final static String SELECTBOXBACKGROUND = "skin/selectbg.png";
    private final static String SELECTBOXLISTSELECTION = "skin/selectbg.png";
    private final static String SELECTBOXLISTBACKGROUND  = "skin/select-list-bg.png";
    

    private final static String TITLE = "ui/title.png";
    private final static String RADIOON = "ui/radio-on.png";
    private final static String RADIOOFF = "ui/radio-off.png";
    private final static String RADIOONDISABLE = "ui/radio-on-disable.png";
    private final static String RADIOOFFDISABLE = "ui/radio-off-disable.png";
    private final static String AUTOON = "ui/auto-on.png";
    private final static String AUTOOFF = "ui/auto-off.png";
    private final static String AUTOBACKGROUND = "ui/auto_background.png";
    private final static String SANDTOP = "ui/sand_top.png";
    private final static String SANDBOTTOM = "ui/sand_bot.png";
    private final static String SANDTRICKLE = "ui/sand_trickle.png";
    private final static String CANNONSLOT = "ui/cannonslots.png";
    private final static String MOVES = "ui/move.png";
    private final static String EMPTYMOVES = "ui/move_empty.png";
    private final static String TOOLTIPBACKGROUND = "ui/tooltip_background.png";
    private final static String SHIPHAND = "ui/shiphand.png";
    private final static String HOURGLASS = "ui/hourglass.png";
    private final static String CONTROLBACKGROUND = "ui/moves-background.png";
    private final static String SHIPSTATUS = "ui/status.png";
    private final static String SHIPSTATUSBG = "ui/status-bg.png";
    private final static String MOVEGETTARGET = "ui/sel_border_square.png";
    private final static String CANNONSELECTIONEMPTY = "ui/grapplecannon_empty.png";
    private final static String CANNONSELECTION = "ui/grapplecannon.png";
    private final static String DAMAGE = "ui/grapplecannon.png";
    private final static String BILGE = "ui/bilge.png";
    
    private final static String MENUUP = "ui/settings.png";
    private final static String MENUDOWN = "ui/settings-disabled.png";
    private final static String BUTTONUP = "ui/menu_button.png";
    private final static String BUTTONDOWN = "ui/menu_button-disabled.png";
    
    private final static String DISENGAGEUP = "ui/disengage.png";
    private final static String DISENGAGEDOWN = "ui/disengagePressed.png";
    private final static String DISENGAGEBACKGROUND = "ui/center_background.png";
    
    private final static String CHATBACKGROUND = "ui/chat_background.png";
    private final static String CHATBACKGROUNDFRAME = "ui/chat_background_frame.png";
    private final static String CHATINDICATOR  = "ui/chat_indicator.png";
    private final static String CHATBARBACKGROUND = "ui/chat_bar_background.png";
    private final static String CHATBUTTONSEND = "ui/chat_button_send.png";
    private final static String CHATBUTTONSENDPRESSED = "ui/chat_button_sendPressed.png";
    
    private final static String CHATSCROLLBARUP = "ui/scrollbar_top.png";
    private final static String CHATSCROLLBARUPPRESSED = "ui/scrollbar_topPressed.png";
    private final static String CHATSCROLLBARDOWN = "ui/scrollbar_bottom.png";
    private final static String CHATSCROLLBARDOWNPRESSED = "ui/scrollbar_bottomPressed.png";
    private final static String CHATSCROLLBARMIDDLE = "ui/scrollbar_center.png";
    private final static String CHATSCROLLBARSCROLL = "ui/scrollbar_scroll.png";
    
    private final static String CHATMESSAGEPLAYER = "ui/chat_message_player.png";
    private final static String CHATMESSAGESERVERBROADCAST = "ui/chat_message_server_broadcast.png";
    private final static String CHATMESSAGESERVERPRIVATE = "ui/chat_message_server_private.png";

    private final static String MAPEDITOR = "skin/mapEditor.png";
    private final static String MAPEDITOR_DISABLED ="skin/mapEditor_disabled.png";
    
    /*
     * asset descriptors for textures
     */
    public AssetDescriptor<Texture> seaBattleCursor = new AssetDescriptor<Texture>(SEABATTLE_CURSOR, Texture.class);
    
	public AssetDescriptor<Texture> baghlahSkin = new AssetDescriptor<Texture>(BAGHLAHSKIN, Texture.class);
	public AssetDescriptor<Texture> blackshipSkin = new AssetDescriptor<Texture>(BLACKSHIPSKIN, Texture.class);
	public AssetDescriptor<Texture> dhowSkin = new AssetDescriptor<Texture>(DHOWSKIN, Texture.class);
	public AssetDescriptor<Texture> fanchuanSkin = new AssetDescriptor<Texture>(FANCHUANSKIN, Texture.class);
	public AssetDescriptor<Texture> grandfrigSkin = new AssetDescriptor<Texture>(GRANDFRIGSKIN, Texture.class);
	public AssetDescriptor<Texture> junkSkin = new AssetDescriptor<Texture>(JUNKSKIN, Texture.class);
	public AssetDescriptor<Texture> lgsloopSkin = new AssetDescriptor<Texture>(LGSLOOPSKIN, Texture.class);
	public AssetDescriptor<Texture> longshipSkin = new AssetDescriptor<Texture>(LONGSHIPSKIN, Texture.class);
	public AssetDescriptor<Texture> merchbrigSkin = new AssetDescriptor<Texture>(MERCHBRIGSKIN, Texture.class);
	public AssetDescriptor<Texture> merchgalSkin = new AssetDescriptor<Texture>(MERCHGALSKIN, Texture.class);
	public AssetDescriptor<Texture> smsloopSkin = new AssetDescriptor<Texture>(SMSLOOPSKIN, Texture.class);
	public AssetDescriptor<Texture> warbrigSkin = new AssetDescriptor<Texture>(WARBRIGSKIN, Texture.class);
	public AssetDescriptor<Texture> warfrigSkin = new AssetDescriptor<Texture>(WARFRIGSKIN, Texture.class);
	public AssetDescriptor<Texture> wargalSkin = new AssetDescriptor<Texture>(WARGALSKIN, Texture.class);
	public AssetDescriptor<Texture> xebecSkin = new AssetDescriptor<Texture>(XEBECSKIN, Texture.class);
	public AssetDescriptor<Texture> baghlah = new AssetDescriptor<Texture>(BAGHLAH, Texture.class);
	public AssetDescriptor<Texture> blackship = new AssetDescriptor<Texture>(BLACKSHIP, Texture.class);
	public AssetDescriptor<Texture> dhow  = new AssetDescriptor<Texture>(DHOW, Texture.class);
	public AssetDescriptor<Texture> fanchuan = new AssetDescriptor<Texture>(FANCHUAN, Texture.class);
	public AssetDescriptor<Texture> grandfrig = new AssetDescriptor<Texture>(GRANDFRIG, Texture.class);
	public AssetDescriptor<Texture> junk = new AssetDescriptor<Texture>(JUNK, Texture.class);
	public AssetDescriptor<Texture> lgsloop = new AssetDescriptor<Texture>(LGSLOOP, Texture.class);
	public AssetDescriptor<Texture> longship = new AssetDescriptor<Texture>(LONGSHIP, Texture.class);
	public AssetDescriptor<Texture> merchbrig = new AssetDescriptor<Texture>(MERCHBRIG, Texture.class);
	public AssetDescriptor<Texture> merchgal = new AssetDescriptor<Texture>(MERCHGAL, Texture.class);
	public AssetDescriptor<Texture> smsloop = new AssetDescriptor<Texture>(SMSLOOP, Texture.class);
	public AssetDescriptor<Texture> warbrig = new AssetDescriptor<Texture>(WARBRIG, Texture.class);
	public AssetDescriptor<Texture> warfrig = new AssetDescriptor<Texture>(WARFRIG, Texture.class);
	public AssetDescriptor<Texture> wargal = new AssetDescriptor<Texture>(WARGAL, Texture.class);
	public AssetDescriptor<Texture> xebec = new AssetDescriptor<Texture>(XEBEC, Texture.class);
	public AssetDescriptor<Texture> baghlah_sinking = new AssetDescriptor<Texture>(BAGHLAH_SINKING, Texture.class);
	public AssetDescriptor<Texture> dhow_sinking = new AssetDescriptor<Texture>(DHOW_SINKING, Texture.class);
	public AssetDescriptor<Texture> fanchuan_sinking = new AssetDescriptor<Texture>(FANCHUAN_SINKING, Texture.class);
	public AssetDescriptor<Texture> grandfrig_sinking = new AssetDescriptor<Texture>(GRANDFRIG_SINKING, Texture.class);
	public AssetDescriptor<Texture> junk_sinking = new AssetDescriptor<Texture>(JUNK_SINKING, Texture.class);
	public AssetDescriptor<Texture> lgsloop_sinking = new AssetDescriptor<Texture>(LGSLOOP_SINKING, Texture.class);
	public AssetDescriptor<Texture> longship_sinking = new AssetDescriptor<Texture>(LONGSHIP_SINKING, Texture.class);
	public AssetDescriptor<Texture> merchbrig_sinking = new AssetDescriptor<Texture>(MERCHBRIG_SINKING, Texture.class);
	public AssetDescriptor<Texture> merchgal_sinking = new AssetDescriptor<Texture>(MERCHGAL_SINKING, Texture.class);
	public AssetDescriptor<Texture> smsloop_sinking = new AssetDescriptor<Texture>(SMSLOOP_SINKING, Texture.class);
	public AssetDescriptor<Texture> warbrig_sinking = new AssetDescriptor<Texture>(WARBRIG_SINKING, Texture.class);
	public AssetDescriptor<Texture> warfrig_sinking = new AssetDescriptor<Texture>(WARFRIG_SINKING, Texture.class);
	public AssetDescriptor<Texture> wargal_sinking = new AssetDescriptor<Texture>(WARGAL_SINKING, Texture.class);
	public AssetDescriptor<Texture> xebec_sinking = new AssetDescriptor<Texture>(XEBEC_SINKING, Texture.class);
	
	/*
	 * Map Textures
	 */
	public AssetDescriptor<Texture> cell = new AssetDescriptor<Texture>(CELL, Texture.class);
	public AssetDescriptor<Texture> safe = new AssetDescriptor<Texture>(SAFE, Texture.class);
	public AssetDescriptor<Texture> sea = new AssetDescriptor<Texture>(SEA, Texture.class);
	public AssetDescriptor<Texture> alkaid_island = new AssetDescriptor<Texture>(ALKAID_ISLAND, Texture.class);
	public AssetDescriptor<Texture> pukru_island = new AssetDescriptor<Texture>(PUKRU_ISLAND, Texture.class);
	public AssetDescriptor<Texture> doyle_island = new AssetDescriptor<Texture>(DOYLE_ISLAND, Texture.class);
	public AssetDescriptor<Texture> isle_keris_island = new AssetDescriptor<Texture>(ISLE_KERIS_ISLAND, Texture.class);
	public AssetDescriptor<Texture> whirlpool = new AssetDescriptor<Texture>(WHIRLPOOL, Texture.class);
	public AssetDescriptor<Texture> wind = new AssetDescriptor<Texture>(WIND, Texture.class);
	public AssetDescriptor<Texture> bigrock = new AssetDescriptor<Texture>(BIGROCK, Texture.class);
	public AssetDescriptor<Texture> smallrock = new AssetDescriptor<Texture>(SMALLROCK, Texture.class);
	public AssetDescriptor<Texture> whirlpool_disabled = new AssetDescriptor<Texture>(WHIRLPOOL_DISABLED, Texture.class);
	public AssetDescriptor<Texture> wind_disabled = new AssetDescriptor<Texture>(WIND_DISABLED, Texture.class);
	public AssetDescriptor<Texture> bigrock_disabled = new AssetDescriptor<Texture>(BIGROCK_DISABLED, Texture.class);
	public AssetDescriptor<Texture> smallrock_disabled = new AssetDescriptor<Texture>(SMALLROCK_DISABLED, Texture.class);
	
	public AssetDescriptor<Texture> cannonball_large = new AssetDescriptor<Texture>(CANNONBALL_LARGE, Texture.class);
    public AssetDescriptor<Texture> cannonball_medium = new AssetDescriptor<Texture>(CANNONBALL_MEDIUM, Texture.class);
    public AssetDescriptor<Texture> cannonball_small = new AssetDescriptor<Texture>(CANNONBALL_SMALL, Texture.class);
    public AssetDescriptor<Texture> splash_large = new AssetDescriptor<Texture>(SPLASH_LARGE, Texture.class);
    public AssetDescriptor<Texture> splash_small = new AssetDescriptor<Texture>(SPLASH_SMALL, Texture.class);
    public AssetDescriptor<Texture> explode_large = new AssetDescriptor<Texture>(EXPLODE_LARGE, Texture.class);
    public AssetDescriptor<Texture> explode_medium = new AssetDescriptor<Texture>(EXPLODE_MEDIUM, Texture.class);
    public AssetDescriptor<Texture> explode_small = new AssetDescriptor<Texture>(EXPLODE_SMALL, Texture.class);
    public AssetDescriptor<Texture> hit = new AssetDescriptor<Texture>(HIT, Texture.class);

    public AssetDescriptor<Texture> infoPanel = new AssetDescriptor<Texture>(INFOPANEL, Texture.class);
    public AssetDescriptor<Texture> contenders = new AssetDescriptor<Texture>(CONTENDERS, Texture.class);
    public AssetDescriptor<Texture> flag = new AssetDescriptor<Texture>(FLAG, Texture.class);
    public AssetDescriptor<Texture> flagTexture = new AssetDescriptor<Texture>(FLAGTEXTURE, Texture.class);
    public AssetDescriptor<Texture> flagTexture_disabled = new AssetDescriptor<Texture>(FLAGTEXTURE_DISABLED, Texture.class);
    
    public AssetDescriptor<Texture> background = new AssetDescriptor<Texture>(BACKGROUND, Texture.class);
    public AssetDescriptor<Texture> smallBackground = new AssetDescriptor<Texture>(SMALLBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> textfieldTexture = new AssetDescriptor<Texture>(TEXTFIELDTEXTURE, Texture.class);
    public AssetDescriptor<Texture> loginButton = new AssetDescriptor<Texture>(LOGINBUTTON, Texture.class);
    public AssetDescriptor<Texture> loginButtonDown = new AssetDescriptor<Texture>(LOGINBUTTONHOVER, Texture.class);
    
    public AssetDescriptor<Texture> cursor = new AssetDescriptor<Texture>(CURSOR, Texture.class);
    public AssetDescriptor<Texture> selection = new AssetDescriptor<Texture>(SELECTION, Texture.class);
    public AssetDescriptor<Texture> battleSelection = new AssetDescriptor<Texture>(BATTLESELECTION, Texture.class);
    
    public AssetDescriptor<Texture> selectBoxBackground = new AssetDescriptor<Texture>(SELECTBOXBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> selectBoxListSelection = new AssetDescriptor<Texture>(SELECTBOXLISTSELECTION, Texture.class);
    public AssetDescriptor<Texture> selectBoxListBackground = new AssetDescriptor<Texture>(SELECTBOXLISTBACKGROUND, Texture.class);
   
    public AssetDescriptor<Texture> title = new AssetDescriptor<Texture>(TITLE, Texture.class);
    public AssetDescriptor<Texture> radioOn = new AssetDescriptor<Texture>(RADIOON, Texture.class);
    public AssetDescriptor<Texture> radioOff = new AssetDescriptor<Texture>(RADIOOFF, Texture.class);
    public AssetDescriptor<Texture> radioOnDisable = new AssetDescriptor<Texture>(RADIOONDISABLE, Texture.class);
    public AssetDescriptor<Texture> radioOffDisable = new AssetDescriptor<Texture>(RADIOOFFDISABLE, Texture.class);
    public AssetDescriptor<Texture> autoOn = new AssetDescriptor<Texture>(AUTOON, Texture.class);
    public AssetDescriptor<Texture> autoOff = new AssetDescriptor<Texture>(AUTOOFF, Texture.class);
    public AssetDescriptor<Texture> autoBackground = new AssetDescriptor<Texture>(AUTOBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> sandTop = new AssetDescriptor<Texture>(SANDTOP, Texture.class);
    public AssetDescriptor<Texture> sandBottom = new AssetDescriptor<Texture>(SANDBOTTOM, Texture.class);
    public AssetDescriptor<Texture> sandTrickle = new AssetDescriptor<Texture>(SANDTRICKLE, Texture.class);
    public AssetDescriptor<Texture> cannonSlot = new AssetDescriptor<Texture>(CANNONSLOT, Texture.class);
    public AssetDescriptor<Texture> moves = new AssetDescriptor<Texture>(MOVES, Texture.class);
    public AssetDescriptor<Texture> emptyMoves = new AssetDescriptor<Texture>(EMPTYMOVES, Texture.class);
    public AssetDescriptor<Texture> toolTipBackground = new AssetDescriptor<Texture>(TOOLTIPBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> shipHand = new AssetDescriptor<Texture>(SHIPHAND, Texture.class);
    public AssetDescriptor<Texture> hourGlass = new AssetDescriptor<Texture>(HOURGLASS, Texture.class);
    public AssetDescriptor<Texture> controlBackground = new AssetDescriptor<Texture>(CONTROLBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> shipStatus = new AssetDescriptor<Texture>(SHIPSTATUS, Texture.class);
    public AssetDescriptor<Texture> shipStatusBg = new AssetDescriptor<Texture>(SHIPSTATUSBG, Texture.class);
    public AssetDescriptor<Texture> moveGetTarget = new AssetDescriptor<Texture>(MOVEGETTARGET, Texture.class);
    public AssetDescriptor<Texture> cannonSelectionEmpty = new AssetDescriptor<Texture>(CANNONSELECTIONEMPTY, Texture.class);
    public AssetDescriptor<Texture> cannonSelection = new AssetDescriptor<Texture>(CANNONSELECTION, Texture.class);
    public AssetDescriptor<Texture> damage = new AssetDescriptor<Texture>(DAMAGE, Texture.class);
    public AssetDescriptor<Texture> bilge = new AssetDescriptor<Texture>(BILGE, Texture.class);
    
    public AssetDescriptor<Texture> menuUp = new AssetDescriptor<Texture>(MENUUP, Texture.class);
    public AssetDescriptor<Texture> menuDown = new AssetDescriptor<Texture>(MENUDOWN, Texture.class);
    public AssetDescriptor<Texture> buttonUp = new AssetDescriptor<Texture>(BUTTONUP, Texture.class);
    public AssetDescriptor<Texture> buttonDown = new AssetDescriptor<Texture>(BUTTONDOWN, Texture.class);
    
    public AssetDescriptor<Texture> disengageUp = new AssetDescriptor<Texture>(DISENGAGEUP, Texture.class);
    public AssetDescriptor<Texture> disengageDown = new AssetDescriptor<Texture>(DISENGAGEDOWN, Texture.class);
    public AssetDescriptor<Texture> disengageBackground = new AssetDescriptor<Texture>(DISENGAGEBACKGROUND, Texture.class);
    
    public AssetDescriptor<Texture> chatBackground = new AssetDescriptor<Texture>(CHATBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> chatBackgroundFrame = new AssetDescriptor<Texture>(CHATBACKGROUNDFRAME, Texture.class);
    public AssetDescriptor<Texture> chatIndicator = new AssetDescriptor<Texture>(CHATINDICATOR, Texture.class);
    public AssetDescriptor<Texture> chatBarBackground = new AssetDescriptor<Texture>(CHATBARBACKGROUND, Texture.class);
    public AssetDescriptor<Texture> chatButtonSend = new AssetDescriptor<Texture>(CHATBUTTONSEND, Texture.class);
    public AssetDescriptor<Texture> chatButtonSendPressed = new AssetDescriptor<Texture>(CHATBUTTONSENDPRESSED, Texture.class);
    
    public AssetDescriptor<Texture> chatScrollBarUp = new AssetDescriptor<Texture>(CHATSCROLLBARUP, Texture.class);
    public AssetDescriptor<Texture> chatScrollBarUpPressed = new AssetDescriptor<Texture>(CHATSCROLLBARUPPRESSED, Texture.class);
    public AssetDescriptor<Texture> chatScrollBarDown = new AssetDescriptor<Texture>(CHATSCROLLBARDOWN, Texture.class);
    public AssetDescriptor<Texture> chatScrollBarDownPressed = new AssetDescriptor<Texture>(CHATSCROLLBARDOWNPRESSED, Texture.class);
    public AssetDescriptor<Texture> chatScrollBarMiddle = new AssetDescriptor<Texture>(CHATSCROLLBARMIDDLE, Texture.class);
    public AssetDescriptor<Texture> chatScrollBarScroll = new AssetDescriptor<Texture>(CHATSCROLLBARSCROLL, Texture.class);
    
    public AssetDescriptor<Texture> chatMessagePlayer= new AssetDescriptor<Texture>(CHATMESSAGEPLAYER, Texture.class);
    public AssetDescriptor<Texture> chatMessageServerBroadcast= new AssetDescriptor<Texture>(CHATMESSAGESERVERBROADCAST, Texture.class);
    public AssetDescriptor<Texture> chatMessageServerPrivate= new AssetDescriptor<Texture>(CHATMESSAGESERVERPRIVATE, Texture.class);

    public AssetDescriptor<Texture> mapEditorButtonUp = new AssetDescriptor<Texture>(MAPEDITOR, Texture.class);
    public AssetDescriptor<Texture> mapEditorButtonDown = new AssetDescriptor<Texture>(MAPEDITOR_DISABLED, Texture.class);
	/*
	 * Font asset descriptors
	 */
	public AssetDescriptor<BitmapFont> regularFont;
	public AssetDescriptor<BitmapFont> titleFont;
	public AssetDescriptor<BitmapFont> notesFont;
	public AssetDescriptor<BitmapFont> seaFont;
	public AssetDescriptor<BitmapFont> controlFont;
	public AssetDescriptor<BitmapFont> chatFont;
	public AssetDescriptor<BitmapFont> menuFont;
	
	public AssetDescriptor<BitmapFont> fontTeamAttacker;
	public AssetDescriptor<BitmapFont> fontTeamDefender;
	public AssetDescriptor<BitmapFont> fontTeamAttacker_Points;
	public AssetDescriptor<BitmapFont> fontTeamDefender_Points;
	public AssetDescriptor<BitmapFont> fontTime;
	public AssetDescriptor<BitmapFont> fontBreak;
	
	public AssetDescriptor<BitmapFont> chatMessageFont;
	
	public final String CANNONHIT_SOUND = "sounds/cannonball_hit.ogg";
	public final String CANNONSPLASH_SOUND = "sounds/cannonball_splash.ogg";
	public final String CANNONSPLASH2_SOUND = "sounds/cannonball_splash2.ogg";
	public final String CANNONFIREBIG_SOUND = "sounds/cannon_fire_big.ogg";
	public final String CANNONFIREMEDIUM_SOUND = "sounds/cannon_fire_medium.ogg";
	public final String CANNONFIRESMALL_SOUND = "sounds/cannon_fire_small.ogg";
	public final String ROCKHIT_SOUND = "sounds/rock_damage.ogg";
	public final String MOVE_SOUND = "sounds/ship_moves.ogg";
	public final String MOVE2_SOUND = "sounds/ship_moves2.ogg";
	public final String SHIPSUNK_SOUND = "sounds/ship_sunk.ogg";
	public final String WHIRLPOOL_SOUND = "sounds/whirlpool.ogg";
	public final String WHIRLPOOL_SOUND2 = "sounds/whirlpool2.ogg";
	public final String WIND_SOUND = "sounds/wind.ogg";
	public final String CREAK_SOUND = "sounds/ambient_creak_1.ogg";
	
	public AssetDescriptor<Sound> hit_sound = new AssetDescriptor<Sound>(CANNONHIT_SOUND, Sound.class);
	public AssetDescriptor<Sound> splash_sound = new AssetDescriptor<Sound>(CANNONSPLASH_SOUND, Sound.class);
	public AssetDescriptor<Sound> cannonbig_sound = new AssetDescriptor<Sound>(CANNONFIREBIG_SOUND, Sound.class);
	public AssetDescriptor<Sound> cannonmedium_sound = new AssetDescriptor<Sound>(CANNONFIREMEDIUM_SOUND, Sound.class);
	public AssetDescriptor<Sound> cannonsmall_sound = new AssetDescriptor<Sound>(CANNONFIRESMALL_SOUND, Sound.class);
	public AssetDescriptor<Sound> rockhit_sound = new AssetDescriptor<Sound>(ROCKHIT_SOUND, Sound.class);
	public AssetDescriptor<Sound> move_sound = new AssetDescriptor<Sound>(MOVE_SOUND, Sound.class);
	public AssetDescriptor<Sound> move2_sound = new AssetDescriptor<Sound>(MOVE2_SOUND, Sound.class);
	public AssetDescriptor<Sound> shipsunk_sound = new AssetDescriptor<Sound>(SHIPSUNK_SOUND, Sound.class);
	public AssetDescriptor<Sound> creak_sound = new AssetDescriptor<Sound>(CREAK_SOUND, Sound.class);
	
	public void loadSounds() {
		manager.load(hit_sound);
		manager.load(splash_sound);
		manager.load(cannonbig_sound);
		manager.load(cannonmedium_sound);
		manager.load(cannonsmall_sound);
		manager.load(rockhit_sound);
		manager.load(move_sound);
		manager.load(move2_sound);
		manager.load(shipsunk_sound);
		manager.load(creak_sound);
	}
	
    public void loadSeaBattle() {
    	manager.load(seaBattleCursor);
    	manager.load(sea);
    	manager.load(alkaid_island);
    	manager.load(pukru_island);
    	manager.load(doyle_island);
    	manager.load(isle_keris_island);
    	manager.load(cell);
    	manager.load(safe);
    	manager.load(bigrock);
    	manager.load(smallrock);
    	manager.load(whirlpool);
    	manager.load(wind);
    	manager.load(bigrock_disabled);
    	manager.load(smallrock_disabled);
    	manager.load(whirlpool_disabled);
    	manager.load(wind_disabled);
    	manager.load(infoPanel);
    	manager.load(contenders);
    	manager.load(flag);
    	manager.load(flagTexture);
    	manager.load(flagTexture_disabled);
    	manager.load(menuUp);
    	manager.load(menuDown);
    	manager.load(buttonUp);
    	manager.load(buttonDown);
    	manager.load(battleSelection);	
    }
    
    public void loadShipInfo() {
    	manager.load(cannonball_large);
        manager.load(cannonball_medium);
        manager.load(cannonball_small);
        manager.load(splash_large);
        manager.load(splash_small);
        manager.load(explode_large);
        manager.load(explode_medium);
        manager.load(explode_small);
        manager.load(hit);

    }
    
    public void loadControl() {
        manager.load(title);
        manager.load(radioOn);
        manager.load(radioOff);
        manager.load(radioOnDisable);
        manager.load(radioOffDisable);
        manager.load(autoOn);
        manager.load(autoOff);
        manager.load(autoBackground);
        manager.load(sandTop);
        manager.load(sandBottom);
        manager.load(sandTrickle);
        manager.load(cannonSlot);
        manager.load(moves);
        manager.load(emptyMoves);
        manager.load(toolTipBackground);
        manager.load(shipHand);
        manager.load(hourGlass);
        manager.load(controlBackground);
        manager.load(shipStatus);
        manager.load(shipStatusBg);
        manager.load(moveGetTarget);
        manager.load(cannonSelectionEmpty);
        manager.load(cannonSelection);
        manager.load(damage);
        manager.load(bilge);
        
        manager.load(disengageUp);
        manager.load(disengageDown);
        manager.load(disengageBackground);
        
        manager.load(chatBackground);
        manager.load(chatBackgroundFrame);
        manager.load(chatIndicator);
        manager.load(chatBarBackground);
        manager.load(chatButtonSend);
        manager.load(chatButtonSendPressed);
        
        manager.load(chatScrollBarUp);
        manager.load(chatScrollBarUpPressed);
        manager.load(chatScrollBarDown);
        manager.load(chatScrollBarDownPressed);
        manager.load(chatScrollBarMiddle);
        manager.load(chatScrollBarScroll);
        
        manager.load(chatMessagePlayer);
        manager.load(chatMessageServerBroadcast);
        manager.load(chatMessageServerPrivate);
    }
    
	public void loadConnectSceneTextures() {
		manager.load(background);
		manager.load(smallBackground);
		manager.load(mapEditorButtonUp);
		manager.load(mapEditorButtonDown);
		manager.load(textfieldTexture);
		manager.load(loginButton);
		manager.load(loginButtonDown);
		manager.load(cursor);
		manager.load(selection);
		manager.load(selectBoxBackground);
		manager.load(selectBoxListBackground);
		manager.load(selectBoxListSelection);
		loadSkinShipTexture();
		
	}
	
	public void loadAllShipTextures() {
		loadNormalShipTexture();
    	manager.load(baghlah_sinking);
    	manager.load(dhow_sinking);
    	manager.load(fanchuan_sinking);
    	manager.load(grandfrig_sinking);
    	manager.load(junk_sinking);
    	manager.load(lgsloop_sinking);
    	manager.load(longship_sinking);
    	manager.load(merchbrig_sinking);
    	manager.load(merchgal_sinking);
    	manager.load(smsloop_sinking);
    	manager.load(warbrig_sinking);
    	manager.load(warfrig_sinking);
    	manager.load(wargal_sinking);
    	manager.load(xebec_sinking);
	}
	
    public void loadNormalShipTexture() {
    	manager.load(baghlah);
    	manager.load(blackship);
    	manager.load(dhow);
    	manager.load(fanchuan);
    	manager.load(grandfrig);
    	manager.load(junk);
    	manager.load(lgsloop);
    	manager.load(longship);
    	manager.load(merchbrig);
    	manager.load(merchgal);
    	manager.load(smsloop);
    	manager.load(warbrig);
    	manager.load(warfrig);
    	manager.load(wargal);
    	manager.load(xebec);
	}
    
    private void loadSkinShipTexture() {
    	manager.load(baghlahSkin);
    	manager.load(blackshipSkin);
    	manager.load(dhowSkin);
    	manager.load(fanchuanSkin);
    	manager.load(grandfrigSkin);
    	manager.load(junkSkin);
    	manager.load(lgsloopSkin);
    	manager.load(longshipSkin);
    	manager.load(merchbrigSkin);
    	manager.load(merchgalSkin);
    	manager.load(smsloopSkin);
    	manager.load(warbrigSkin);
    	manager.load(warfrigSkin);
    	manager.load(wargalSkin);
    	manager.load(xebecSkin);
	}
    
	public void loadFonts() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        
        parameter.fontFileName = "font/FjallaOne-Regular.ttf";
        parameter.fontParameters.size = 18;
        parameter.fontParameters.color = new Color(Color.WHITE);
        parameter.fontParameters.shadowColor = new Color(0, 0, 0, 0.8f);
        parameter.fontParameters.shadowOffsetY = 1;
        //load regular font
        regularFont = new AssetDescriptor<BitmapFont>(REGULARFONT, BitmapFont.class, parameter);
        manager.load(regularFont);
        
        parameterNotes.fontFileName = "font/FjallaOne-Regular.ttf";
        parameterNotes.fontParameters.size = 11;
        parameterNotes.fontParameters.color = new Color(Color.WHITE);
        parameterNotes.fontParameters.shadowColor = new Color(0, 0, 0, 0.8f);
        parameterNotes.fontParameters.shadowOffsetX = 0;
        parameterNotes.fontParameters.shadowOffsetY = 0;
        //load note font
        notesFont = new AssetDescriptor<BitmapFont>(NOTESFONT, BitmapFont.class, parameterNotes);
        manager.load(notesFont);
        
        parameterTitle.fontFileName = "font/Open_Sans/OpenSans-SemiBold.ttf";
        parameterTitle.fontParameters.size = 46;
        parameterTitle.fontParameters.color = new Color(Color.WHITE);
        parameterTitle.fontParameters.gamma = 0.9f;
        
        titleFont = new AssetDescriptor<BitmapFont>(TITLEFONT, BitmapFont.class, parameterTitle);
        manager.load(titleFont);
        
        parameterSea.fontFileName = "font/Roboto-Regular.ttf";
        parameterSea.fontParameters.size = 10;
        parameterSea.fontParameters.spaceX = 0;
        parameterSea.fontParameters.shadowColor = new Color(0, 0, 0, 0.5f);
        parameterSea.fontParameters.borderColor = Color.BLACK;
        parameterSea.fontParameters.borderWidth = 1;
        parameterSea.fontParameters.borderStraight = true;
        parameterSea.fontParameters.shadowOffsetY = 1;
        parameterSea.fontParameters.shadowOffsetX = 1;
        
        seaFont = new AssetDescriptor<BitmapFont>(SEAFONT, BitmapFont.class, parameterSea);
        manager.load(seaFont);
        
        parameterControl.fontFileName = "font/Roboto-Regular.ttf";
        parameterControl.fontParameters.size = 12;
        
        controlFont = new AssetDescriptor<BitmapFont>(CONTROLFONT, BitmapFont.class, parameterControl);
        manager.load(controlFont);
        

        parameterMessage.fontFileName = "font/Roboto-Regular.ttf";
        parameterMessage.fontParameters.size = 11;
        
        chatFont = new AssetDescriptor<BitmapFont>(CHATFONT, BitmapFont.class, parameterMessage);
        manager.load(chatFont);
        
        parameterMenu.fontFileName = "font/FjallaOne-Regular.ttf";
        parameterMenu.fontParameters.size = 10;
        parameterMenu.fontParameters.shadowColor = new Color(0, 0, 0, 0.2f);
        parameterMenu.fontParameters.shadowOffsetY = 1;
        parameterMenu.fontParameters.color = Color.BLACK;
        
        menuFont = new AssetDescriptor<BitmapFont>(MENUFONT, BitmapFont.class, parameterMenu);
        manager.load(menuFont);
    
        parameterInfoTeam.fontFileName = "font/FjallaOne-Regular.ttf";
        parameterInfoTeam.fontParameters.size = 14;
        parameterInfoTeam.fontParameters.shadowColor = new Color(0, 0, 0, 0.8f);
        parameterInfoTeam.fontParameters.shadowOffsetY = 1;
        
        fontTeamAttacker = new AssetDescriptor<BitmapFont>(FONTTEAMATTACKER, BitmapFont.class, parameterInfoTeam);
        manager.load(fontTeamAttacker);
        
        fontTeamDefender = new AssetDescriptor<BitmapFont>(FONTTEAMDEFENDER, BitmapFont.class, parameterInfoTeam);
        manager.load(fontTeamDefender);
        
        parameterInfoPoints.fontFileName = "font/FjallaOne-Regular.ttf";
        parameterInfoPoints.fontParameters.size = 13;
        parameterInfoPoints.fontParameters.shadowColor = new Color(0, 0, 0, 0.6f);
        parameterInfoPoints.fontParameters.shadowOffsetY = 1;
        
        fontTeamAttacker_Points = new AssetDescriptor<BitmapFont>(FONTTEAMATTACKER_POINTS, BitmapFont.class, parameterInfoPoints);
        manager.load(fontTeamAttacker_Points);
        
        fontTeamDefender_Points = new AssetDescriptor<BitmapFont>(FONTTEAMDEFENDER_POINTS, BitmapFont.class, parameterInfoPoints);
        manager.load(fontTeamDefender_Points);
        
        parameterInfoTime.fontFileName = "font/BreeSerif-Regular.ttf";
        parameterInfoTime.fontParameters.size = 30;
        parameterInfoTime.fontParameters.color = new Color(1, 230 / 255f, 59 / 255f, 1);
        parameterInfoTime.fontParameters.shadowColor = new Color(0, 0, 0, 0.3f);
        parameterInfoTime.fontParameters.shadowOffsetY = 2;     
        
        fontTime = new AssetDescriptor<BitmapFont>(FONTTIME, BitmapFont.class, parameterInfoTime);
        manager.load(fontTime);
        
        parameterInfoBreak.fontFileName = "font/Roboto-Regular.ttf";
        parameterInfoBreak.fontParameters.size = 13;
        parameterInfoBreak.fontParameters.color = new Color(1, 230 / 255f, 59 / 255f, 1);
        parameterInfoBreak.fontParameters.shadowColor = new Color(0, 0, 0, 0.3f);
        parameterInfoBreak.fontParameters.shadowOffsetY = 2;       
        fontBreak = new AssetDescriptor<BitmapFont>(FONTBREAK, BitmapFont.class, parameterInfoBreak);
        manager.load(fontBreak);
       
        parameterMessageFont.fontFileName = "font/Roboto-Regular.ttf";
        parameterMessageFont.fontParameters.size = 11;
        
        chatMessageFont = new AssetDescriptor<BitmapFont>(CHATMESSAGEFONT, BitmapFont.class, parameterMessageFont);
        manager.load(chatMessageFont);


	}
	
}
