package com.benberi.cadesim.game.scene.impl.control;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.scene.SceneComponent;
import com.benberi.cadesim.game.scene.impl.control.hand.HandMove;
import com.benberi.cadesim.game.scene.impl.control.hand.impl.BigShipHandMove;
import com.benberi.cadesim.game.scene.impl.control.hand.impl.SmallShipHandMove;

public class BattleControlComponent extends SceneComponent<ControlAreaScene> implements InputProcessor {
    /**
     * The context
     */
    private GameContext context;

    /**
     * Left moves
     */
    private int leftMoves;

    /**
     * Right moves
     */
    private int rightMoves;

    /**
     * Forward moves
     */
    private int forwardMoves;

    /**
     * The available shoots
     */
    private int cannons = 0;

    /**
     * The selected moves
     */
    private HandMove[] movesHolder;

    /**
     * Radio button states
     */
    private boolean[] radioButtons = new boolean[3];

    private void enableRadio(int radio) {
        for (int i=0; i<radioButtons.length; i++) {
            if (i == radio) {
                radioButtons[i] = true;
            } else {
                radioButtons[i] = false;
            }
        }
    }

    // should we lock our controls during animation phase?
    private boolean lockedDuringAnimate = false;
    public void setLockedDuringAnimate(boolean value) {
        lockedDuringAnimate = value;
    }
    public boolean isLockedDuringAnimate() {
        return lockedDuringAnimate;
    }

    private final int TOOLTIP_SHOW_THRESHOLD = 500; // delay before showing tooltip
    private int leftMovesByTurn[]    = { 0, 0, 0, 0, 0 }; // moves added from right to left
    private int forwardMovesByTurn[] = { 0, 0, 0, 0, 0 }; // "
    private int rightMovesByTurn[]   = { 0, 0, 0, 0, 0 }; // "
    private int selectedTooltip = -1; // -1 if invalid; >=0 if valid; index to arrays above
    private long selectedTooltipTime = System.currentTimeMillis(); // time it's been selected

    /**
     * helper method to activate/deactivate tooltip depending on mouse position
     * @param x screenx
     * @param y screeny
     */
    private void handleTooltipActivation(float x, float y)
    {
        // if mouse is over tokens, we want to show a tooltip
        int slot = getSlotForPosition(x, y);
        int oldSelectedTooltip = selectedTooltip;
        selectedTooltip = (slot == 4 || slot == 5 || slot == 6)?(slot - 4):-1;
        if (selectedTooltip != -1 && oldSelectedTooltip != selectedTooltip)
        {
            selectedTooltipTime = System.currentTimeMillis();
        }
    }

    /**
     * helper method to update moves received this turn
     */
    public void updateMoveHistoryWithNewMoves(int lefts, int forwards, int rights)
    {
        leftMovesByTurn[4]    += lefts;
        forwardMovesByTurn[4] += forwards;
        rightMovesByTurn[4]   += rights;
    }

    public void resetMoveHistory() {
        // set all equal to 0
        for (int i=0; i<5; i++) {
        leftMovesByTurn[i]    = 0;
        forwardMovesByTurn[i] = 0;
            rightMovesByTurn[i]   = 0;
        }
    }

    /**
     * helper method to handle move history update after turn ends
     */
    public void updateMoveHistoryAfterTurn()
    {
        for (int i=0; i<4; i++) // leftshift elements 0,1,2,3 by 1 place
        {
            leftMovesByTurn[i]    = leftMovesByTurn[i+1];
            forwardMovesByTurn[i] = forwardMovesByTurn[i+1];
            rightMovesByTurn[i]   = rightMovesByTurn[i+1];
        }
        // clear for next round
        leftMovesByTurn[4] = 0;
        forwardMovesByTurn[4] = 0;
        rightMovesByTurn[4] = 0;
    }

    /**
     * Batch renderer for sprites and textures
     */
    private SpriteBatch batch;

    /**
     * Shape renderer for shapes, used for damage/bilge and such
     */
    private ShapeRenderer shape;

    /**
     * Font for texts/messages
     */
    private BitmapFont font;

    /**
     * The target move
     */
    private MoveType targetMove;

    /**
     * The damage of the vessel
     */
    private float damageAmount;

    /**
     * The bilge of the vessel
     */
    private float bilgeAmount;

    /**
     * If the move selection is automatic or not
     */
    private boolean auto = true;

    /**
     * The turn time
     */
    private int time = 0;

    /**
     * modifier to calculate button placement
     */
    int absheight = Gdx.graphics.getHeight(); // absolute height

    /**
     * create a ChatBar
     */
    private ChatBar chatBar;
    
    /**
     * Stage for the chatContainer
     */
    private Stage chatContainerStage;

    /**
     * add to the chat buffer
     */
    public void addNewMessage(String sender, String message) {
    	if (message.length() <= CHAT_MESSAGE_MAX_LENGTH)
    	{
    		if (sender.equals(Constants.serverBroadcast))
            {
            	displayMessage(message, chatMessageServerBroadcast);
            }
        	else if (sender.equals(Constants.serverPrivate))
        	{
        		displayMessage(message, chatMessageServerPrivate);
        	}
        	else
        	{
        		displayMessage(sender + ": \"" + message + "\"", chatMessagePlayer);
        	}
    	}
    	
    }

    /**
     * Textures
     */
    private Texture shiphand;
    private Texture moves;
    private Texture emptyMoves;
    private Texture tooltipBackground;
    private TextureRegion leftMoveTexture;
    private TextureRegion rightMoveTexture;
    private TextureRegion forwardMoveTexture;
    private TextureRegion blockingMoveTexture; // for ships that only are 3 moves
    private TextureRegion emptyLeftMoveTexture;
    private TextureRegion emptyRightMoveTexture;
    private TextureRegion emptyForwardMoveTexture;
    private Texture sandTopTexture;
    private Texture sandBottomTexture;
    private TextureRegion sandBottom;
    private TextureRegion sandTop;

    private TextureRegion emptyCannon;
    private TextureRegion cannon;

    private Texture sandTrickleTexture;
    private TextureRegion sandTrickle;

    private Texture hourGlass;
    private Texture cannonSlots;
    private TextureRegion cannonLeft;
    private TextureRegion cannonRight;
    private TextureRegion emptyCannonLeft;
    private TextureRegion emptyCannonRight;
    private Texture controlBackground;
    private Texture disengageBackground;
    private Texture chatBackground;
    private Texture chatBackgroundFrame;
    private Texture shipStatus;
    private Texture shipStatusBg;
    private TextureRegion damage;
    private TextureRegion bilge;
    private Texture moveGetTargetTexture;
    private TextureRegion moveTargetSelAuto;
    private TextureRegion moveTargetSelForce;
    private Texture title;
    private Texture radioOn;
    private Texture radioOff;
    private Texture radioOnDisable;
    private Texture radioOffDisable;
    private Texture autoOn;
    private Texture autoOff;
    private Texture autoBackground;

    private Texture cannonSelection;
    private Texture cannonSelectionEmpty;

    private Texture disengageUp;
    private Texture disengageDown;

    private Texture chatIndicator;
    private Texture chatBarBackground;
    private Texture chatButtonSend;
    private Texture chatButtonSendPressed;

    private Texture chatScrollBarUp;
    private Texture chatScrollBarUpPressed;
    private Texture chatScrollBarDown;
    private Texture chatScrollBarDownPressed;
    private Texture chatScrollBarMiddle;
    private Texture chatScrollBarScroll;
    
    private Texture chatMessagePlayer;
    private Texture chatMessageServerBroadcast;
    private Texture chatMessageServerPrivate;
    
    // references for drawing chat/scroll
    Container<Table> chatContainer;
    Label.LabelStyle chatLabelStylePlayer;
    Label.LabelStyle chatLabelStyleServerBroadcast;
    Label.LabelStyle chatLabelStyleServerPrivate;
    Table chatTable;
    float containerTopY;
    float containerBottomY;
    
    private int TOOLTIP_BACKGROUND_X_OFFSET = 0; // from one of the token piece
    private int TOOLTIP_BACKGROUND_Y_OFFSET = -32; // "
    private int TOOLTIP_TEXT_X_OFFSET       = TOOLTIP_BACKGROUND_X_OFFSET + 4; // from background
    private int TOOLTIP_TEXT_Y_OFFSET       = TOOLTIP_BACKGROUND_X_OFFSET - 19; // "

    // reference coords - MOVES control
    private int MOVES_REF_X             = 0;
    private int MOVES_REF_Y             = 75;

    private int MOVES_backgroundX       = MOVES_REF_X + 5;
    private int MOVES_backgroundY       = MOVES_REF_Y - 67;

    private int MOVES_titleX            = MOVES_REF_X + 68;
    private int MOVES_titleY            = MOVES_REF_Y + 76;

    private int MOVES_autoX             = MOVES_REF_X + 60;
    private int MOVES_autoY             = MOVES_REF_Y + 33;

    private int MOVES_autoBackgroundX   = MOVES_REF_X + 77;
    private int MOVES_autoBackgroundY   = MOVES_REF_Y + 28;

    private int MOVES_autoTextX         = MOVES_REF_X + 29;
    private int MOVES_autoTextY         = MOVES_REF_Y + 46; // text from top edge

    private int MOVES_cannonsX          = MOVES_REF_X + 50;
    private int MOVES_cannonsY          = MOVES_REF_Y + 3;

    private int MOVES_cannonsTextX      = MOVES_REF_X + 57;
    private int MOVES_cannonsTextY      = MOVES_REF_Y - 4;  // text from top edge

    private int MOVES_leftX             = MOVES_REF_X + 82;
    private int MOVES_forwardX          = MOVES_REF_X + 114;
    private int MOVES_rightX            = MOVES_REF_X + 146;
    private int MOVES_leftY             = MOVES_REF_Y + 0;
    private int MOVES_forwardY          = MOVES_REF_Y + 0;
    private int MOVES_rightY            = MOVES_REF_Y + 0;

    private int MOVES_leftRadioX        = MOVES_REF_X + 90;
    private int MOVES_forwardRadioX     = MOVES_REF_X + 122;
    private int MOVES_rightRadioX       = MOVES_REF_X + 154;
    private int MOVES_leftRadioY        = MOVES_REF_Y + 36;
    private int MOVES_forwardRadioY     = MOVES_REF_Y + 36;
    private int MOVES_rightRadioY       = MOVES_REF_Y + 36;

    private int MOVES_leftSelectX       = MOVES_REF_X + 78;
    private int MOVES_forwardSelectX    = MOVES_REF_X + 110;
    private int MOVES_rightSelectX      = MOVES_REF_X + 142;
    private int MOVES_leftSelectY       = MOVES_REF_Y - 4;
    private int MOVES_forwardSelectY    = MOVES_REF_Y - 4;
    private int MOVES_rightSelectY      = MOVES_REF_Y - 4;

    private int MOVES_leftMovesTextX    = MOVES_REF_X + 90;
    private int MOVES_forwardMovesTextX = MOVES_REF_X + 122;
    private int MOVES_rightMovesTextX   = MOVES_REF_X + 154;
    private int MOVES_leftMovesTextY    = MOVES_REF_Y - 4; // text from top edge
    private int MOVES_forwardMovesTextY = MOVES_REF_Y - 4; // "
    private int MOVES_rightMovesTextY   = MOVES_REF_Y - 4; // "

    private int MOVES_shiphandX         = MOVES_REF_X + 200;
    private int MOVES_shiphandY         = MOVES_REF_Y - 57;

    // general moveSlot
    private int MOVES_moveSlotX             = MOVES_REF_X + 216;

    // specific moveSlot
    private int MOVES_moveSlot0X            = MOVES_moveSlotX;
    private int MOVES_moveSlot0Y            = MOVES_REF_Y + 54;
    private int MOVES_moveSlot1X            = MOVES_moveSlotX;
    private int MOVES_moveSlot1Y            = MOVES_REF_Y + 20;
    private int MOVES_moveSlot2X            = MOVES_moveSlotX;
    private int MOVES_moveSlot2Y            = MOVES_REF_Y - 14;
    private int MOVES_moveSlot3X            = MOVES_moveSlotX;
    private int MOVES_moveSlot3Y            = MOVES_REF_Y - 48;

    // general cannons
    private int MOVES_cannonLeftSlotBigX    = MOVES_REF_X + 184;
    private int MOVES_cannonRightSlotSmallX = MOVES_REF_X + 245;
    private int MOVES_cannonLeftSlotSmallX  = MOVES_cannonLeftSlotBigX + 15;
    private int MOVES_cannonRightSlotBigX   = MOVES_cannonRightSlotSmallX + 15;
    private int MOVES_cannonSlot0Y          = MOVES_REF_Y + 59;
    private int MOVES_cannonSlot1Y          = MOVES_REF_Y + 25;
    private int MOVES_cannonSlot2Y          = MOVES_REF_Y - 9;
    private int MOVES_cannonSlot3Y          = MOVES_REF_Y - 43;

    // specific cannons
    private int MOVES_cannonLeftSlot0X      = MOVES_cannonLeftSlotBigX;
    private int MOVES_cannonLeftSlot0Y      = MOVES_cannonSlot0Y;
    private int MOVES_cannonRightSlot0X     = MOVES_cannonRightSlotSmallX;
    private int MOVES_cannonRightSlot0Y     = MOVES_cannonSlot0Y;
    private int MOVES_cannonLeftSlot1X      = MOVES_cannonLeftSlotBigX;
    private int MOVES_cannonLeftSlot1Y      = MOVES_cannonSlot1Y;
    private int MOVES_cannonRightSlot1X     = MOVES_cannonRightSlotSmallX;
    private int MOVES_cannonRightSlot1Y     = MOVES_cannonSlot1Y;
    private int MOVES_cannonLeftSlot2X      = MOVES_cannonLeftSlotBigX;
    private int MOVES_cannonLeftSlot2Y      = MOVES_cannonSlot2Y;
    private int MOVES_cannonRightSlot2X     = MOVES_cannonRightSlotSmallX;
    private int MOVES_cannonRightSlot2Y     = MOVES_cannonSlot2Y;
    private int MOVES_cannonLeftSlot3X      = MOVES_cannonLeftSlotBigX;
    private int MOVES_cannonLeftSlot3Y      = MOVES_cannonSlot3Y;
    private int MOVES_cannonRightSlot3X     = MOVES_cannonRightSlotSmallX;
    private int MOVES_cannonRightSlot3Y     = MOVES_cannonSlot3Y;

    // hourglass
    int MOVES_hourGlassX     = MOVES_REF_X + 290;
    int MOVES_hourGlassY     = MOVES_REF_Y - 52;
    int MOVES_sandTrickleX   = MOVES_REF_X + 303;
    int MOVES_sandTrickleY   = MOVES_REF_Y - 47;
    int MOVES_sandTopX       = MOVES_REF_X + 294;
    int MOVES_sandTopY       = MOVES_REF_Y - 5;
    int MOVES_sandBottomX    = MOVES_REF_X + 294;
    int MOVES_sandBottomY    = MOVES_REF_Y - 49;

    // ship status/ship status background
    int MOVES_shipStatusBackgroundX = MOVES_REF_X + 282;
    int MOVES_shipStatusBackgroundY = MOVES_REF_Y + 47;
    int MOVES_shipStatusX           = MOVES_shipStatusBackgroundX;
    int MOVES_shipStatusY           = MOVES_shipStatusBackgroundY;
    int MOVES_shipDamageX           = MOVES_shipStatusBackgroundX + 20;
    int MOVES_shipDamageY           = MOVES_REF_Y + 69;
    int MOVES_shipBilgeX            = MOVES_shipStatusBackgroundX + 23;
    int MOVES_shipBilgeY            = MOVES_REF_Y + 69;

    // MOVES shapes
    Rectangle MOVES_shape_auto                = new Rectangle(MOVES_autoX,             MOVES_autoY,             17, 17);
    Rectangle MOVES_shape_placingLeftCannons  = new Rectangle(MOVES_cannonLeftSlot3X,  MOVES_cannonLeftSlot3Y - 5,  32, 130);
    Rectangle MOVES_shape_placingRightCannons = new Rectangle(MOVES_cannonRightSlot3X, MOVES_cannonLeftSlot3Y - 5,  32, 130);

    Rectangle MOVES_shape_moveSlot0           = new Rectangle(MOVES_moveSlot0X,        MOVES_moveSlot0Y,        28, 28);
    Rectangle MOVES_shape_moveSlot1           = new Rectangle(MOVES_moveSlot1X,        MOVES_moveSlot1Y,        28, 28);
    Rectangle MOVES_shape_moveSlot2           = new Rectangle(MOVES_moveSlot2X,        MOVES_moveSlot2Y,        28, 28);
    Rectangle MOVES_shape_moveSlot3           = new Rectangle(MOVES_moveSlot3X,        MOVES_moveSlot3Y,        28, 28);

    Rectangle MOVES_shape_leftToken           = new Rectangle(MOVES_leftX,             MOVES_leftY,             28, 28);
    Rectangle MOVES_shape_forwardToken        = new Rectangle(MOVES_forwardX,          MOVES_forwardY,          28, 28);
    Rectangle MOVES_shape_rightToken          = new Rectangle(MOVES_rightX,            MOVES_rightY,            28, 28);

    // cannon hitboxes have Y -/+ 5 because in an actual Sea Battle you can
    // load a gun by clicking on a 28x28 square around it (regardless of
    // whether it's a large/small ship).
    Rectangle MOVES_shape_cannonLeftSlot0     = new Rectangle(MOVES_cannonLeftSlot0X,  MOVES_cannonLeftSlot0Y  - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonLeftSlot1     = new Rectangle(MOVES_cannonLeftSlot1X,  MOVES_cannonLeftSlot1Y  - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonLeftSlot2     = new Rectangle(MOVES_cannonLeftSlot2X,  MOVES_cannonLeftSlot2Y  - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonLeftSlot3     = new Rectangle(MOVES_cannonLeftSlot3X,  MOVES_cannonLeftSlot3Y  - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonRightSlot0    = new Rectangle(MOVES_cannonRightSlot0X, MOVES_cannonRightSlot0Y - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonRightSlot1    = new Rectangle(MOVES_cannonRightSlot1X, MOVES_cannonRightSlot1Y - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonRightSlot2    = new Rectangle(MOVES_cannonRightSlot2X, MOVES_cannonRightSlot2Y - 5, 32, 18+10);
    Rectangle MOVES_shape_cannonRightSlot3    = new Rectangle(MOVES_cannonRightSlot3X, MOVES_cannonRightSlot3Y - 5, 32, 18+10);

    Rectangle MOVES_shape_leftRadio           = new Rectangle(MOVES_leftRadioX,        MOVES_leftRadioY,        13, 13);
    Rectangle MOVES_shape_forwardRadio        = new Rectangle(MOVES_forwardRadioX,     MOVES_forwardRadioY,     13, 13);
    Rectangle MOVES_shape_rightRadio          = new Rectangle(MOVES_rightRadioX,       MOVES_rightRadioY,       13, 13);

    Rectangle MOVES_shape_placingMoves        = new Rectangle(MOVES_moveSlot3X,        MOVES_moveSlot3Y,        28, (4 * 28) + (3 * 5));
    Rectangle MOVES_shape_pickingMoves        = new Rectangle(MOVES_cannonsX,          MOVES_cannonsY,          (4 * 28) + (3 * 4), 28);

    // reference coords - DISENGAGE control
    private int DISENGAGE_REF_X       = 0;
    private int DISENGAGE_REF_Y       = 0;
    private int DISENGAGE_backgroundX = DISENGAGE_REF_X + 5+336+5;
    private int DISENGAGE_backgroundY = DISENGAGE_REF_Y + 8;
    private int DISENGAGE_buttonX     = DISENGAGE_REF_X + 5+336+5 + 30;
    private int DISENGAGE_buttonY     = DISENGAGE_REF_Y + 8 + 24;

    // DISENGAGE shapes
    Rectangle DISENGAGE_shape_clickingDisengage   = new Rectangle(DISENGAGE_buttonX, DISENGAGE_buttonY, 77, 16);

    // reference coords - CHAT control
    private int CHAT_REF_X              = 0;
    private int CHAT_REF_Y              = 0;

    // CHAT
    private int CHAT_backgroundX        = CHAT_REF_X + 489;
    private int CHAT_backgroundY        = CHAT_REF_Y + 8;
    
    private int CHAT_backgroundFrameX   = CHAT_REF_X + 489;
    private int CHAT_backgroundFrameY   = CHAT_REF_Y + 0;

    private int CHAT_indicatorX         = CHAT_REF_X + 492;
    private int CHAT_indicatorY         = CHAT_REF_Y + 8;

    private int CHAT_boxX               = CHAT_REF_X + 593;
    private int CHAT_boxY               = CHAT_REF_Y + 7;

    private int CHAT_chatBarBackgroundX = CHAT_REF_X + 590;
    private int CHAT_chatBarBackgroundY = CHAT_REF_X + 7;
    
    private int CHAT_buttonSendX        = CHAT_REF_X + 723;
    private int CHAT_buttonSendY        = CHAT_REF_Y + 7;
    
    private int CHAT_windowX            = CHAT_REF_X + 496;
    private int CHAT_windowY            = CHAT_REF_Y + 39;//42;

    private int CHAT_scrollBarUpX       = CHAT_REF_X + 781;
    private int CHAT_scrollBarUpY       = CHAT_REF_Y + 163;
    private int CHAT_scrollBarDownX     = CHAT_REF_X + 781;
    private int CHAT_scrollBarDownY     = CHAT_REF_Y + 38;
    private int CHAT_scrollBarMiddleX   = CHAT_REF_X + 781;
    private int CHAT_scrollBarMiddleY   = CHAT_REF_Y + 50;
    
    private int CHAT_scrollBarScrollX   = CHAT_REF_X + 781;
    private int CHAT_scrollBarScrollY   = CHAT_REF_Y + 48; 

    // CHAT shapes
    Rectangle CHAT_shape_clickingSend   = new Rectangle(CHAT_buttonSendX, CHAT_buttonSendY, 45, 16);
    Rectangle CHAT_shape_chatBox        = new Rectangle(CHAT_boxX, CHAT_boxY, 122, 17);
    Rectangle CHAT_shape_scrollingUp    = new Rectangle(CHAT_scrollBarUpX, CHAT_scrollBarUpY, 12, 12);
    Rectangle CHAT_shape_scrollingDown  = new Rectangle(CHAT_scrollBarDownX, CHAT_scrollBarDownY, 12, 12);
    Rectangle CHAT_shape_messageWindow  = new Rectangle(CHAT_windowX, CHAT_windowY, 280, 130);
    Rectangle CHAT_shape_scrollBar = new Rectangle(CHAT_scrollBarScrollX, CHAT_scrollBarScrollY, 12, 30);
    
    /**
     * state of buttons. true if pushed, false if not.
     */
    private boolean disengageButtonIsDown = false; // initial
    private boolean sendChatButtonIsDown    = false; // initial
    private boolean scrollUpButtonIsDown    = false; // initial (confusing!)
    private boolean scrollDownButtonIsDown  = false; // initial (confusing!)
    
    private long scrollButtonDownOnTime     = 0;     // when was scroll button last pressed
    private long scrollButtonDownRepeatTime = 0;     // scroll button last repeated at this time
    /**
     * Max length for a chat message
     */
    private static final int CHAT_MESSAGE_MAX_LENGTH = 240;
    
    /**
     * Max number of messages before clearing old ones
     * Messages are created inefficiently and use quite a lot of memory.
     * Once this number rolls over, memory usage will stabilize.
     */
    private static final int CHAT_MAX_NUMBER_OF_MESSAGES = 100;
    
    /**
     * size of scroll increment (px) when scrolling (mouse or button)
     */
    private static final int CHAT_WINDOW_BOTTOM_PAD       = 3;
    private static final int CHAT_WINDOW_SCROLL_INCREMENT = 14 + CHAT_WINDOW_BOTTOM_PAD;
    
    /**
     * time threshold for button to start scrolling (ms)
     * i.e. scroll button pressed once, scrolls once
     * if held down, after <this many> millis, it starts to repeat
     */
    private static final int CHAT_WINDOW_BUTTON_SCROLL_TIME_THRESHOLD = 333;
    
    /**
     * time threshold for button to keep repeat scrolling (ms)
     * i.e. if scroll button is repeating (after being held down),
     * dont scroll on every render. scroll on this interval
     */
    private static final int CHAT_WINDOW_BUTTON_SCROLL_REPEAT_THRESHOLD = 50;
    
    /**
     * maximum/minimum scroll height
     */
    private final int CHAT_MAXIMUM_SCROLL_Y = CHAT_scrollBarScrollY + 86;
    private final int CHAT_MINIMUM_SCROLL_Y = CHAT_scrollBarScrollY;

    private int blockingMoveSlot = 3;         // initial value; no effect if !isBigShip

    private boolean isBigShip = false;    // big == 3 moves, not big == 4 moves
    private boolean isDoubleShot = false; // has 2 cannons per side

    private boolean  isDragging;          //       are we dragging
    private MoveType startDragMove;       // what  are we dragging
    private int      startDragSlot;       // where are we dragging from
    private Vector2 draggingPosition;
    
    private boolean  draggingScroll = false; // keeps scrollbar locked until release

    protected BattleControlComponent(GameContext context, ControlAreaScene owner, boolean big, boolean doubleShot) {
        super(context, owner);
        isDoubleShot = doubleShot;
        isBigShip    = big;
        if (isDoubleShot) {
            movesHolder = new BigShipHandMove[4];
        }
        else {
            movesHolder = new SmallShipHandMove[4];
        }

        for (int i = 0; i < movesHolder.length; i++) {
            movesHolder[i] = createMove(isDoubleShot);
        }

        radioButtons = new boolean[3];
        enableRadio(1);

        this.context = context;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        font = context.getManager().get(context.getAssetObject().controlFont);

        title = context.getManager().get(context.getAssetObject().title);
        radioOn = context.getManager().get(context.getAssetObject().radioOn);
        radioOff = context.getManager().get(context.getAssetObject().radioOff);
        radioOnDisable = context.getManager().get(context.getAssetObject().radioOnDisable);
        radioOffDisable = context.getManager().get(context.getAssetObject().radioOffDisable);
        autoOn = context.getManager().get(context.getAssetObject().autoOn);
        autoOff = context.getManager().get(context.getAssetObject().autoOff);
        autoBackground = context.getManager().get(context.getAssetObject().autoBackground);

        sandTopTexture = context.getManager().get(context.getAssetObject().sandTop);
        sandBottomTexture = context.getManager().get(context.getAssetObject().sandBottom);
        
        sandTop = new TextureRegion(sandTopTexture, 19, 43);
        sandBottom= new TextureRegion(sandBottomTexture, 19, 43);
        
        sandTrickleTexture = context.getManager().get(context.getAssetObject().sandTrickle);
        sandTrickle = new TextureRegion(sandTrickleTexture, 0, 0, 1, 43);

        cannonSlots = context.getManager().get(context.getAssetObject().cannonSlot);
        moves = context.getManager().get(context.getAssetObject().moves);
        emptyMoves = context.getManager().get(context.getAssetObject().emptyMoves);
        tooltipBackground = context.getManager().get(context.getAssetObject().toolTipBackground);
        shiphand = context.getManager().get(context.getAssetObject().shipHand);
        hourGlass = context.getManager().get(context.getAssetObject().hourGlass);
        controlBackground = context.getManager().get(context.getAssetObject().controlBackground);
        shipStatus = context.getManager().get(context.getAssetObject().shipStatus);
        shipStatusBg = context.getManager().get(context.getAssetObject().shipStatusBg);
        moveGetTargetTexture = context.getManager().get(context.getAssetObject().moveGetTarget);
        cannonSelectionEmpty = context.getManager().get(context.getAssetObject().cannonSelectionEmpty);
        cannonSelection = context.getManager().get(context.getAssetObject().cannonSelection);
        damage = new TextureRegion(
        		context.getManager().get(context.getAssetObject().damage));
        bilge = new TextureRegion(
        		context.getManager().get(context.getAssetObject().bilge));
        damage.flip(false, true);
        bilge.flip(false, true);

        emptyCannon = new TextureRegion(cannonSelectionEmpty, 25, 0, 25, 25);
        cannon = new TextureRegion(cannonSelection, 25, 0, 25, 25);

        damage.setRegionWidth(17);
        bilge.setRegionWidth(17);

        leftMoveTexture = new TextureRegion(moves, 0, 0, 28, 28);
        forwardMoveTexture = new TextureRegion(moves, 28, 0, 28, 28);
        rightMoveTexture = new TextureRegion(moves, 56, 0, 28, 28);
        blockingMoveTexture = new TextureRegion(moves, 84, 0, 28, 28);

        emptyLeftMoveTexture = new TextureRegion(emptyMoves, 0, 0, 28, 28);
        emptyForwardMoveTexture = new TextureRegion(emptyMoves, 28, 0, 28, 28);
        emptyRightMoveTexture = new TextureRegion(emptyMoves, 56, 0, 28, 28);

        emptyCannonLeft = new TextureRegion(cannonSlots, 0, 0, 16, 18);
        emptyCannonRight = new TextureRegion(cannonSlots, 16, 0, 16, 18);

        cannonLeft = new TextureRegion(cannonSlots, 32, 0, 16, 18);
        cannonRight = new TextureRegion(cannonSlots, 48, 0, 16, 18);

        moveTargetSelForce = new TextureRegion(moveGetTargetTexture, 0, 0, 36, 36);
        moveTargetSelAuto = new TextureRegion(moveGetTargetTexture, 36, 0, 36, 36);

        disengageUp = context.getManager().get(context.getAssetObject().disengageUp);
        disengageDown = context.getManager().get(context.getAssetObject().disengageDown);
        disengageBackground = context.getManager().get(context.getAssetObject().disengageBackground);

        chatBackground = context.getManager().get(context.getAssetObject().chatBackground);
        chatBackgroundFrame = context.getManager().get(context.getAssetObject().chatBackgroundFrame);
        chatIndicator  = context.getManager().get(context.getAssetObject().chatIndicator);
        chatBarBackground = context.getManager().get(context.getAssetObject().chatBarBackground);
        chatButtonSend = context.getManager().get(context.getAssetObject().chatButtonSend);
        chatButtonSendPressed = context.getManager().get(context.getAssetObject().chatButtonSendPressed);
        
        chatMessagePlayer = context.getManager().get(context.getAssetObject().chatMessagePlayer);
        chatMessageServerBroadcast = context.getManager().get(context.getAssetObject().chatMessageServerBroadcast);
        chatMessageServerPrivate = context.getManager().get(context.getAssetObject().chatMessageServerPrivate);
        
        // stage for chatContainer
        chatContainerStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        
        // instantiate the ChatBar
        setChatBar(new ChatBar(context, CHAT_MESSAGE_MAX_LENGTH, CHAT_shape_chatBox));
        
        // make a container to encapsulate the table. this is what we will scroll.
        chatContainer = new Container<Table>();
        chatContainer.setPosition(CHAT_windowX, CHAT_windowY);
        chatContainer.align(Align.bottomLeft);
        chatContainer.fillX();
        
        // style of the chat messages (backgrounds applied later)
        chatLabelStylePlayer = new LabelStyle(
        		context.getManager().get(context.getAssetObject().chatMessageFont),new Color(0f,0f,0f,1f));
        chatLabelStyleServerBroadcast = new LabelStyle(
        		context.getManager().get(context.getAssetObject().chatMessageFont),new Color(0f,0f,0f,1f));
        chatLabelStyleServerPrivate = new LabelStyle(
        		context.getManager().get(context.getAssetObject().chatMessageFont),new Color(0f,0f,0f,1f));
        
        // create a table to maintain order of messages
        chatTable = new Table();
        chatTable.align(Align.bottomLeft);
        
        // add table to container, and add container to a new stage
        // separate stage added so the two are drawn in separate batches
        // (the chat window requires significant portions to be drawn afterwards)
        chatContainer.setActor(chatTable);
        chatContainerStage.addActor(chatContainer);
        
        // update top/bottom
        containerTopY = CHAT_windowY;
        containerBottomY = CHAT_windowY;
        
        // set chat view and scroll to default positions
        resetChatView();

        // get scroll bar textures
        chatScrollBarUp = context.getManager().get(context.getAssetObject().chatScrollBarUp);
        chatScrollBarUpPressed = context.getManager().get(context.getAssetObject().chatScrollBarUpPressed);
        chatScrollBarDown = context.getManager().get(context.getAssetObject().chatScrollBarDown);
        chatScrollBarDownPressed = context.getManager().get(context.getAssetObject().chatScrollBarDownPressed);
        chatScrollBarMiddle = context.getManager().get(context.getAssetObject().chatScrollBarMiddle);
        chatScrollBarScroll = context.getManager().get(context.getAssetObject().chatScrollBarScroll);

        // initialise
        setDamagePercentage(0);
        setBilgePercentage(0);
    }
    
    /**
     * display a message in the chat
     */
    public void displayMessage(String message, Texture messageTexture) {
    	// create the data
        chatTable.row().padBottom(CHAT_WINDOW_BOTTOM_PAD);
        Label chat1;
        
        // define the background textures based on the message source
        // is it broadcast, private, regular?
        TextureRegion tr;
        Label.LabelStyle ls = new LabelStyle(
        		context.getManager().get(context.getAssetObject().chatMessageFont),new Color(0f,0f,0f,1f));
        chat1 = new Label(message, ls);

        // background width will vary depending on the width of the label
        chat1.setWrap(true);
        if (chat1.getWidth() >= CHAT_shape_messageWindow.width)
        {
        	// use whole thing
        	tr = new TextureRegion(messageTexture, 0, 0, 282, 24);
        	chatTable.add(chat1).width(CHAT_shape_messageWindow.width).align(Align.left);
        }
        else
        {
        	// use width plus some constant (10 padding either side of inside lines)
        	tr = new TextureRegion(messageTexture, 0, 0, (int)chat1.getWidth() + 10, 24);
        	chatTable.add(chat1).width(chat1.getWidth() + 10).align(Align.left);
        }
        
    	
    	// set the background
    	ls.background = new Image(tr).getDrawable();
        
        // reset the style with new background
        chat1.setStyle(ls);
        	
        // handle chat if it has grown too big?
        if (chatTable.getCells().size > CHAT_MAX_NUMBER_OF_MESSAGES) {
            @SuppressWarnings("unchecked")
			Cell<Label> cell = chatTable.getCells().first();
            cell.getActor().remove();                     // rm actor
            chatTable.getCells().removeValue(cell, true); // rm lingering physical presence
            chatTable.invalidate();
        }
        
        // update the top and bottom positional markers of the chat window
        containerTopY = CHAT_shape_messageWindow.y + CHAT_shape_messageWindow.height - chatTable.getHeight()-3;
        containerBottomY = CHAT_shape_messageWindow.y;
        
        updateScrollPosition();
        
        // jump scroll to bottom
        resetChatView();
    }
    /**
     * clear chat table contents
     */
	public void clearChat() {
		while (chatTable.getCells().size > 0) {
			@SuppressWarnings("unchecked")
			Cell<Label> cell = chatTable.getCells().first();
			cell.getActor().getStyle().font.dispose();
			cell.getActor().remove();
			chatTable.getCells().removeValue(cell, true);
		}
	}
    /**
     * given a change in chatbox position, update the scrollbar position
     * note: only called when not dragging scrollbar
     */
    public void updateScrollPosition() {        
        // calculate the fraction, then pos = scrollbottom + (f * (scrolltop - scrollbottom))
        float pos = containerBottomY - chatContainer.getY();
        float f = pos / Math.abs(containerBottomY - containerTopY);
        
        // set the scrollbar to 
        CHAT_shape_scrollBar.y = CHAT_scrollBarScrollY + (int) (f * (CHAT_MAXIMUM_SCROLL_Y - CHAT_MINIMUM_SCROLL_Y));
        
        // snap the scrollbar to bounds if it's exceeded its bounds
        if (CHAT_shape_scrollBar.y > CHAT_MAXIMUM_SCROLL_Y) {
        	CHAT_shape_scrollBar.y = CHAT_MAXIMUM_SCROLL_Y;
        }
        else if (CHAT_shape_scrollBar.y < CHAT_MINIMUM_SCROLL_Y) {
        	CHAT_shape_scrollBar.y = CHAT_MINIMUM_SCROLL_Y;
        }
    }
    
    /**
     * move both chatbox and scrollbar to bottom ("reset")
     */
    public void resetChatView() {
        // reset scroll to bottom
        CHAT_shape_scrollBar.y = CHAT_scrollBarScrollY;
        
        // reset message window to bottom
        chatContainer.setPosition(CHAT_windowX, CHAT_windowY);
    }
    
    /** 
     * given a change in scrollbar position, update the chatbox position
     * note: only called when dragging scrollbar
     */
    public void updateChatPosition() {
        // calculate the fraction
        float f = (float)(CHAT_shape_scrollBar.y - CHAT_MINIMUM_SCROLL_Y) / (float)(CHAT_MAXIMUM_SCROLL_Y - CHAT_MINIMUM_SCROLL_Y);
        
        // update the container
        chatContainer.setY(
            CHAT_shape_messageWindow.y +
            (int) (-1 * (f * Math.abs(containerBottomY - containerTopY + CHAT_shape_messageWindow.y)))
        );
        
        // adjust if have exceeded bounds when scrolling
        snapChatToBounds();
    }
    
    private void snapChatToBounds() {
    	// adjust if have exceeded bounds when scrolling
        if ((chatContainer.getY() + chatTable.getHeight()-3) <= ((CHAT_shape_messageWindow.y + CHAT_shape_messageWindow.height))) {
            // at the top
        	int top = ((CHAT_shape_messageWindow.y + CHAT_shape_messageWindow.height));
        	int diffFromTop = top - (int)(chatContainer.getY() + chatTable.getHeight()-3);
        	chatContainer.setPosition(
        			chatContainer.getX(),
        			chatContainer.getY() + diffFromTop
        	);
        }
        else if (chatContainer.getY() >= CHAT_shape_messageWindow.y)
        {
            // at the bottom
        	chatContainer.setPosition(
    			chatContainer.getX(),
    			CHAT_shape_messageWindow.y
        	);
        }
    }
    
    /**
     * scroll the chat in some direction.
     */
    public void scrollChat(boolean scrollUp) {
    	// dont update if we're not higher than a full chunk of text yet
        if (chatTable.getHeight() > CHAT_shape_messageWindow.height)
        {
        	// somewhere scrolling inbetween the top and bottom
            chatContainer.setPosition(
                chatContainer.getX(),
                chatContainer.getY() + (CHAT_WINDOW_SCROLL_INCREMENT) * (scrollUp?-1:1)
            );
            
            // adjust if have exceeded bounds when scrolling
            snapChatToBounds();
            
            // adjust the scrollbar accordingly
            updateScrollPosition();
        }
    }

    public HandMove createMove(boolean doubleShot) {
        if (doubleShot) {
            return new BigShipHandMove();
        }
        return new SmallShipHandMove();
    }

    @Override
    public void update() {
        int turnDuration = getContext().getTurnDuration();

        double ratio = (double) 43 / (double) turnDuration;

        sandTop.setRegionY(43 - (int) Math.round(time * ratio));
        sandTop.setRegionHeight((int) Math.round(time * ratio));

        ratio =  (double) 43 / (double) turnDuration;
        sandBottom.setRegionY(43 - (int) Math.round((turnDuration - time) * ratio));
        sandBottom.setRegionHeight((int) Math.round((turnDuration - time) * ratio));
    }

    @Override
    public void render() {
        renderMoveControl();
        renderDisengage();
        renderChatBackground();
        chatContainerStage.act();
        chatContainerStage.draw();
        renderChat();
        getChatBar().spin();
    }
    
    public void reset() {
		targetMove = MoveType.FORWARD;
        enableRadio(1);

        auto=true;
        resetPlacedMovesAfterTurn();        // reset the moves placed
        resetMoveHistory(); // reset the tooltip counts to zero
        updateMoveHistoryWithNewMoves(leftMoves, forwardMoves, rightMoves); // set tooltip most recent to current moves available
    }

    @Override
    public void dispose() {
    }


    @Override
    public boolean handleClick(float x, float y, int button) {
    	startDragSlot = getSlotForPosition(x, y);
        if ((!disengageButtonIsDown) && isClickingDisengage(x,y)) {
            disengageButtonIsDown = true;
            return true;
        }
        else if ((!sendChatButtonIsDown) && isClickingSend(x,y)) {
            sendChatButtonIsDown = true;
            return true;
        }
        else if ((!scrollUpButtonIsDown) && isClickingScrollUp(x,y)) {
            scrollUpButtonIsDown = true;
            scrollChat(true); // just once, render scrolls the rest
            scrollButtonDownOnTime = System.currentTimeMillis();
            return true;
        }
        else if ((!scrollDownButtonIsDown) && isClickingScrollDown(x,y)) {
        	scrollButtonDownOnTime = System.currentTimeMillis();
        	scrollChat(false); // just once, render scrolls the rest
            scrollDownButtonIsDown = true;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * return whether point is in rect or not.
     *
     * note: compensates for Y weirdness by using absheight
     * so just use normal cartesian space (x==0,y==0 bottom left)
     *
     * @param inX   input x
     * @param inY   input y
     * @param rectX rect origin x
     * @param rectY rect origin y
     * @param rectW rect width
     * @param rectH rect height
     * @return
     */
    private boolean isPointInRect(float inX, float inY, Rectangle rect) {
        return
            (inX >= rect.x) &&
            (inX < (rect.x + rect.width)) &&
            (inY >= (absheight - rect.y - rect.height)) &&
            (inY < (absheight - rect.y));
    }

    private boolean isTogglingAuto(float x, float y) {
        return isPointInRect(x,y,MOVES_shape_auto);
    }

    private boolean isPlacingLeftCannons(float x, float y) {
        return isPointInRect(x,y,MOVES_shape_placingLeftCannons);
    }

    private boolean isPlacingRightCannons(float x, float y) {
        return isPointInRect(x,y,MOVES_shape_placingRightCannons);
    }

    private boolean isClickingDisengage(float x, float y) {
        return isPointInRect(x,y,DISENGAGE_shape_clickingDisengage);
    }

    private boolean isClickingSend(float x, float y) {
        return isPointInRect(x,y,CHAT_shape_clickingSend);
    }

    private boolean isClickingScrollUp(float x, float y) {
        return isPointInRect(x,y,CHAT_shape_scrollingUp);
    }

    private boolean isClickingScrollDown(float x, float y) {
        return isPointInRect(x,y,CHAT_shape_scrollingDown);
    }
    
    private boolean isDraggingScrollBar(float x, float y) {
        return isPointInRect(x,y,CHAT_shape_scrollBar);
    }

    private boolean isPlacingMoves(float x, float y) {
        return isPointInRect(x,y,MOVES_shape_placingMoves);
    }

    private boolean isPickingMoves(float x, float y) {
        // cannons, L,F,R
        return isPointInRect(x,y,MOVES_shape_pickingMoves);
    }

    private int getSlotForPosition(float x, float y) {
        if (isPlacingMoves(x,y)) {
            if (isPointInRect(x,y,MOVES_shape_moveSlot0)) {
                return 0;
            }
            else if (isPointInRect(x,y,MOVES_shape_moveSlot1)) {
                return 1;
            }
            else if (isPointInRect(x,y,MOVES_shape_moveSlot2)) {
                return 2;
            }
            else if (isPointInRect(x,y,MOVES_shape_moveSlot3)) {
                return 3;
            }
        }
        else if (isPickingMoves(x,y)) {
            if (isPointInRect(x,y,MOVES_shape_leftToken)) {
                return 4;
            }
            else if (isPointInRect(x,y,MOVES_shape_forwardToken)) {
                return 5;
            }
            else if (isPointInRect(x,y,MOVES_shape_rightToken)) {
                return 6;
            }
        }
        else if (isPlacingLeftCannons(x,y)) {
        	if (isPointInRect(x,y,MOVES_shape_cannonLeftSlot0)) {
                return 7;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonLeftSlot1)) {
                return 8;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonLeftSlot2)) {
                return 9;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonLeftSlot3)) {
                return 10;
            }
        }
        else if (isPlacingRightCannons(x,y))
        {
        	if (isPointInRect(x,y,MOVES_shape_cannonRightSlot0)) {
                return 11;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonRightSlot1)) {
                return 12;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonRightSlot2)) {
                return 13;
            }
            else if (isPointInRect(x,y,MOVES_shape_cannonRightSlot3)) {
            	return 14;
            }
        }

        // default return
        return -1;
    }

    @Override
    public boolean handleDrag(float x, float y, float ix, float iy) {
        boolean controlsLocked = context.getBattleScene().getInformation().getIsBreak() || isLockedDuringAnimate();
        if (!isDragging && !controlsLocked) {
            if (startDragSlot != -1) { // cant start dragging from an invalid region
                switch (startDragSlot) {
                case 4:
                case 5:
                case 6:
                    // drag only if there are moves
                    MoveType m = MoveType.forId(startDragSlot - 3);
                    if (hasMove(m)) {
                        startDragMove = m;
                        isDragging = true;
                    }
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                	// not dragging - these are cannon slots
                	break;
                default:
                    startDragMove  = movesHolder[startDragSlot].getMove();
                    isDragging = true;
                    break;
                }
            }
        }

        if (isDragging) {
            draggingPosition = new Vector2(x, y);
        }

        // if we drag off a token select piece,
        // deactivate the tooltip
        if (selectedTooltip != -1)
        {
            selectedTooltip = -1;
        }

        // if we drag off disengage,
        // deactivate it with no penalty to the user.
        if (disengageButtonIsDown) {
            if (!isClickingDisengage(x, y)) {
                disengageButtonIsDown = false;
            }
        }

        // if we drag off chatButtonSend,
        // deactivate it with no penalty to the user.
        if (sendChatButtonIsDown) {
            if (!isClickingSend(x, y)) {
                sendChatButtonIsDown = false;
            }
        }

        // if we drag off chatScrollBarUp,
        // deactivate it with no penalty to the user.
        if (scrollUpButtonIsDown) {
            if (!isClickingScrollUp(x, y)) {
                scrollUpButtonIsDown = false;
            }
        }

        // if we drag off chatScrollBarDown,
        // deactivate it with no penalty to the user.
        if (scrollDownButtonIsDown) {
            if (!isClickingScrollDown(x, y)) {
                scrollDownButtonIsDown = false;
            }
        }
        
        // handle if we are clicking scroll (one time only)
        if ((!draggingScroll) && isDraggingScrollBar(x, y)) {
            draggingScroll = true;
        }

        // handle drag of scrollbar (while down)
        if (draggingScroll) {
        	CHAT_shape_scrollBar.y -= iy;
            if (CHAT_shape_scrollBar.y > CHAT_MAXIMUM_SCROLL_Y) {
                CHAT_shape_scrollBar.y = CHAT_MAXIMUM_SCROLL_Y;
            }
            else if (CHAT_shape_scrollBar.y < CHAT_MINIMUM_SCROLL_Y)
            {
                CHAT_shape_scrollBar.y = CHAT_MINIMUM_SCROLL_Y;
            }
        	
        	// dont update chat table if we're not higher than a full chunk of text yet
            if (chatTable.getHeight() > CHAT_shape_messageWindow.height)
            {
	            updateChatPosition();
            }
        }

        return false;
    }

    @Override
    public boolean handleRelease(float x, float y, int button) {
        boolean controlsLocked = context.getBattleScene().getInformation().getIsBreak() || isLockedDuringAnimate();

        if (isDragging && (!controlsLocked)) {
            isDragging = false;
            int endDragSlot = getSlotForPosition(x, y);
            if (startDragSlot < 0) {
                // no-op bugfix - startDragSlot may be -1
            }
            if (endDragSlot == -1) { // dragged to nothing

                if (startDragSlot <= 3) {
                    getContext().sendSelectMoveSlot(startDragSlot, MoveType.NONE);
                }
            }
            else if (startDragSlot == endDragSlot)
            {
                // no-op, can't drag to self
            }
            else if (
                    (startDragSlot <= 3) &&
                    (movesHolder[startDragSlot].getMove() == MoveType.NONE) &&
                    blockingMoveSlot != startDragSlot)
            {
                // no-op, can't drag a None token to anything
            }
            else
            {
                if (startDragSlot <= 3 && endDragSlot <= 3) { // drag from place to place; swap
                    // update move swap - blockingMove slot is "None"
                    getContext().sendSwapMovesPacket(
                        endDragSlot,
                        startDragSlot
                    );

                    // update blockingMove location
                    if (isBigShip) {
                        if ((blockingMoveSlot == startDragSlot) && endDragSlot <= 3) { // cant drag blockingMove off to new piece
                            blockingMoveSlot = endDragSlot;
                            getContext().sendBlockingMoveSlotChanged(blockingMoveSlot);
                        } else if ((blockingMoveSlot == endDragSlot) && startDragSlot <= 3) { // cant drag new piece onto blockingMove
                            blockingMoveSlot = startDragSlot;
                            getContext().sendBlockingMoveSlotChanged(blockingMoveSlot);
                        }
                    }
                } else if (startDragSlot > 3 && startDragSlot <= 6 && endDragSlot <= 3) { // moving from available to placed; replace
                    // if there's anything there already, replace it, apart from a blockingMove
                    if ((!isBigShip) || (blockingMoveSlot != endDragSlot)) {
                        getContext().sendSelectMoveSlot(endDragSlot, MoveType.forId(startDragSlot-3));
                    }
                } else if (startDragSlot <= 3) { // started on something, but dragged it to something unhandled
                    getContext().sendSelectMoveSlot(startDragSlot, MoveType.NONE);
                }
                
            }

            draggingPosition = null;
        } else if((!controlsLocked)) { //fix adding moves
        	isDragging = false; // bugfix locked controls
            if (disengageButtonIsDown && isClickingDisengage(x, y)) {
                getContext().sendDisengageRequestPacket();
                disengageButtonIsDown = false;
            }
            else if (sendChatButtonIsDown && isClickingSend(x, y)) {
                getChatBar().sendChat();
                sendChatButtonIsDown = false;
            }
            else if (scrollUpButtonIsDown && isClickingScrollUp(x,y)) {
                scrollUpButtonIsDown = false;
            }
            else if (scrollDownButtonIsDown && isClickingScrollDown(x,y)) {
                scrollDownButtonIsDown = false;
            }
            else if (isTogglingAuto(x, y)) {
                if (auto) {
                    auto = false;
                }
                else {
                    auto = true;
                }
                getContext().sendToggleAuto(auto);
            }
            else if ((startDragSlot >=0) && (startDragSlot <=3) && isPlacingMoves(x, y)) {
                int slot = getSlotForPosition(x,y);
                switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                    handleMovePlace(slot, button);
                    break;
                default:
                    break;
                }
            }
            else if (isPlacingLeftCannons(x, y)) {

                if ((startDragSlot == 7) && isPointInRect(x,y,MOVES_shape_cannonLeftSlot0))
                {
                    getContext().sendAddCannon(0,0);
                }
                else if ((startDragSlot == 8) && isPointInRect(x,y,MOVES_shape_cannonLeftSlot1))
                {
                    getContext().sendAddCannon(0,1);
                }
                else if ((startDragSlot == 9) && isPointInRect(x,y,MOVES_shape_cannonLeftSlot2))
                {
                    getContext().sendAddCannon(0,2);
                }
                else if ((startDragSlot == 10) && isPointInRect(x,y,MOVES_shape_cannonLeftSlot3))
                {
                    getContext().sendAddCannon(0,3);
                }
            }
            else if (isPlacingRightCannons(x, y)) {
                if ((startDragSlot == 11) && isPointInRect(x,y,MOVES_shape_cannonRightSlot0))
                {
                    getContext().sendAddCannon(1,0);
                }
                else if ((startDragSlot == 12) && isPointInRect(x,y,MOVES_shape_cannonRightSlot1))
                {
                    getContext().sendAddCannon(1,1);
                }
                else if ((startDragSlot == 13) && isPointInRect(x,y,MOVES_shape_cannonRightSlot2))
                {
                    getContext().sendAddCannon(1,2);
                }
                else if ((startDragSlot == 14) && isPointInRect(x,y,MOVES_shape_cannonRightSlot3))
                {
                    getContext().sendAddCannon(1,3);
                }
            }
            else if (!auto){
                // can either click on the radio button or the move
                if (
                        (isPointInRect(x,y,MOVES_shape_leftRadio)) ||
                        (isPointInRect(x,y,MOVES_shape_leftToken))
                ) {
                    targetMove = MoveType.LEFT;
                    getContext().sendGenerationTarget(targetMove);
                }
                else if (
                        (isPointInRect(x,y,MOVES_shape_forwardRadio)) ||
                        (isPointInRect(x,y,MOVES_shape_forwardToken))
                ) {
                    targetMove = MoveType.FORWARD;
                    getContext().sendGenerationTarget(targetMove);
                }
                else if (
                        (isPointInRect(x,y,MOVES_shape_rightRadio)) ||
                        (isPointInRect(x,y,MOVES_shape_rightToken))
                ) {
                     targetMove = MoveType.RIGHT;
                     getContext().sendGenerationTarget(targetMove);
                }
            }
            else if (controlsLocked) {
                // pass - cant set moves on break
            }
        }

        // undo any scroll drag effect
        if (draggingScroll) {
            draggingScroll = false;
        }

        // activate or deactivate tooltips depending on where mouse is
        handleTooltipActivation(x,y);

        return false;
    }

    private void handleMovePlace(int position, int button) {
        if (isBigShip && (position == blockingMoveSlot)) {
            return;
        }
        if (isDragging) {
            return;
        }
        HandMove move = movesHolder[position];
        if (move.getMove() == MoveType.NONE) {
            if (button == Input.Buttons.LEFT) {
                if (leftMoves > 0) {
                    placeMove(position, MoveType.LEFT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.LEFT);
                }
                else if (forwardMoves > 0) {
                    placeMove(position, MoveType.FORWARD, true);
                    getContext().sendSelectMoveSlot(position, MoveType.FORWARD);
                }
                else if (rightMoves > 0) {
                    placeMove(position, MoveType.RIGHT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.RIGHT);
                }
            }
            else if (button == Input.Buttons.MIDDLE) {
                if (forwardMoves > 0) {
                    placeMove(position, MoveType.FORWARD, true);
                    getContext().sendSelectMoveSlot(position, MoveType.FORWARD);
                }
                else if (rightMoves > 0) {
                    placeMove(position, MoveType.RIGHT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.RIGHT);
                }
                else if (leftMoves > 0) {
                    placeMove(position, MoveType.LEFT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.LEFT);
                }
            }
            else if (button == Input.Buttons.RIGHT) {
                if (rightMoves > 0) {
                    placeMove(position, MoveType.RIGHT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.RIGHT);
                }
                else if (forwardMoves > 0) {
                    placeMove(position, MoveType.FORWARD, true);
                    getContext().sendSelectMoveSlot(position, MoveType.FORWARD);
                }
                else if (leftMoves > 0) {
                    placeMove(position, MoveType.LEFT, true);
                    getContext().sendSelectMoveSlot(position, MoveType.LEFT);
                }
            }
        }
        else {
            if (button == Input.Buttons.LEFT) {
                MoveType next = move.getMove().getNext();
                if (hasMove(next)) {
                    placeMove(position, next, true);
                    getContext().sendSelectMoveSlot(position, next);
                }
                else if (hasMove(next.getNext())) {
                    placeMove(position, next.getNext(), true);
                    getContext().sendSelectMoveSlot(position, next.getNext());
                }
                else if (hasMove(next.getNext().getNext())) {
                    placeMove(position, next.getNext().getNext(), true);
                    getContext().sendSelectMoveSlot(position, next.getNext().getNext());
                }
            }
            else if (button == Input.Buttons.RIGHT) {
                MoveType prev = move.getMove().getPrevious();
                if (hasMove(prev)) {
                    placeMove(position, prev, true);
                    getContext().sendSelectMoveSlot(position, prev);
                }
                else if (hasMove(prev.getPrevious())) {
                    placeMove(position, prev.getPrevious(), true);
                    getContext().sendSelectMoveSlot(position, prev.getPrevious());
                }
                else if (hasMove(prev.getPrevious().getPrevious())) {
                    placeMove(position, prev.getPrevious().getPrevious(), true);
                    getContext().sendSelectMoveSlot(position, prev.getPrevious().getPrevious());
                }
            }
        }

    }

    private boolean hasMove(MoveType move) {
        switch (move) {
            case LEFT:
                return leftMoves > 0;
            case RIGHT:
                return rightMoves > 0;
            case FORWARD:
                return forwardMoves > 0;
            default:
                return true;
        }
    }

    /**
     * Sets the turn time
     * @param time  The turn time in seconds
     */
    public void setTime(int time) {
        this.time = time;
        int sandX = sandTrickle.getRegionX();
        sandX++;
        if (sandX > 8) {
            sandX = 0;
        }

        sandTrickle.setRegionX(sandX);
        sandTrickle.setRegionWidth(1);
    }

    /**
     * Sets the damage percentage
     * @param d The percentage to set out of 100
     */
    public void setDamagePercentage(int d) {
        if (d > 100) {
            d = 100;
        }
        damageAmount = d;
    }

    /**
     * Sets the bilge percentage
     * @param b The percentage to set out of 100
     */
    public void setBilgePercentage(int b) {
        if (b > 100) {
            b = 100;
        }
        bilgeAmount = b;
    }

    /**
     * Sets the available moves to use
     * @param left      The amount of left movements
     * @param forward   The amount of forward movements
     * @param right     The amount of right movements
     */
    public void setMoves(int left, int forward, int right) {
        leftMoves = left;
        forwardMoves = forward;
        rightMoves = right;
    }

    /**
     * Sets the available cannonballs to use
     * @param cannonballs The number of available cannonballs for use
     */
    public void setLoadedCannonballs(int cannonballs) {
        cannons = cannonballs;
    }

    private void renderMoveControl() {
        batch.begin();

        // The yellow BG for tokens and moves and hourglass
        batch.draw(controlBackground, MOVES_backgroundX, MOVES_backgroundY);

        drawMoveHolder();
        drawShipStatus();
        drawTimer();
        drawMovesSelect();
        TextureRegion t = blockingMoveTexture; // initial, prevent crashes
        batch.draw(title, MOVES_titleX, MOVES_titleY);
        if (isDragging && startDragSlot != -1) {
            if (isBigShip && (startDragSlot == blockingMoveSlot)) {
                t = blockingMoveTexture;
            } else {
                t = getTextureForMove(startDragMove);
            }

            if ((startDragMove != MoveType.NONE) || (isBigShip && (startDragSlot == blockingMoveSlot))) {
                batch.draw(t, draggingPosition.x - t.getRegionWidth() / 2, Gdx.graphics.getHeight() - draggingPosition.y - t.getRegionHeight() / 2);
            }
        }

        // show tooltips if can
        if (selectedTooltip != -1 && ((System.currentTimeMillis() - selectedTooltipTime)) >= TOOLTIP_SHOW_THRESHOLD)
        {
            int[] tooltip;
            int tokenOffsetX;
            int tokenOffsetY;
            switch (selectedTooltip)
            {
            case 0:
                tooltip      = leftMovesByTurn;
                tokenOffsetX = MOVES_leftSelectX;
                tokenOffsetY = MOVES_leftSelectY;
                break;
            case 1:
                tooltip = forwardMovesByTurn;
                tokenOffsetX = MOVES_forwardSelectX;
                tokenOffsetY = MOVES_forwardSelectY;
                break;
            case 2:
                tooltip =  rightMovesByTurn;
                tokenOffsetX = MOVES_rightSelectX;
                tokenOffsetY = MOVES_rightSelectY;
                break;
            default:
                tooltip =  forwardMovesByTurn;
                tokenOffsetX = MOVES_forwardSelectX;
                tokenOffsetY = MOVES_forwardSelectY;
                break;
                // error case but safe default
            }

            String tooltipText =
                tooltip[0] + ", " +
                tooltip[1] + ", " +
                tooltip[2] + ", " +
                tooltip[3] + ", " +
                tooltip[4];

            // draw
            batch.draw(
                    tooltipBackground,
                    TOOLTIP_BACKGROUND_X_OFFSET + tokenOffsetX,
                    TOOLTIP_BACKGROUND_Y_OFFSET + tokenOffsetY
            );
            font.draw(
                    batch,
                    tooltipText,
                    TOOLTIP_TEXT_X_OFFSET + tokenOffsetX,
                    TOOLTIP_TEXT_Y_OFFSET + tokenOffsetY
            );
        }
        else
        {
            // no-op
        }

        batch.end();
    }

    /**
     * background for disengage button / pirates aboard
     */
    private void renderDisengage() {
        batch.begin();
        batch.draw(disengageBackground, DISENGAGE_backgroundX, DISENGAGE_backgroundY);
        batch.draw((disengageButtonIsDown)?disengageDown:disengageUp, DISENGAGE_buttonX, DISENGAGE_buttonY);
        batch.end();
    }
    
    private void renderChatBackground() {
    	batch.begin();
    	batch.draw(chatBackground, CHAT_backgroundX, CHAT_backgroundY);
    	batch.end();
    }

    private void renderChat() {
        batch.begin();
        batch.draw(chatBackgroundFrame, CHAT_backgroundFrameX, CHAT_backgroundFrameY);
        batch.draw(chatIndicator,  CHAT_indicatorX, CHAT_indicatorY);
        batch.draw(chatBarBackground, CHAT_chatBarBackgroundX, CHAT_chatBarBackgroundY);
        batch.draw(chatScrollBarScroll, CHAT_shape_scrollBar.x, CHAT_shape_scrollBar.y);
        batch.draw(sendChatButtonIsDown?chatButtonSendPressed:chatButtonSend, CHAT_buttonSendX, CHAT_buttonSendY);
        batch.draw(scrollUpButtonIsDown?chatScrollBarUpPressed:chatScrollBarUp, CHAT_scrollBarUpX, CHAT_scrollBarUpY);
        batch.draw(scrollDownButtonIsDown?chatScrollBarDownPressed:chatScrollBarDown, CHAT_scrollBarDownX, CHAT_scrollBarDownY);
        batch.draw(chatScrollBarMiddle, CHAT_scrollBarMiddleX, CHAT_scrollBarMiddleY);
        
        // if we're scrolling, do the scroll
        // but only if the button's been held down more than the threshold
        long now = System.currentTimeMillis();
        long scrollDuration = now - scrollButtonDownOnTime;
        if (scrollUpButtonIsDown && (scrollDuration > CHAT_WINDOW_BUTTON_SCROLL_TIME_THRESHOLD)) {
        	// if we're repeating scroll... dont repeat every render.
        	// only repeat once every n millis
        	if ((now - scrollButtonDownRepeatTime) > CHAT_WINDOW_BUTTON_SCROLL_REPEAT_THRESHOLD)
        	{
        		scrollButtonDownRepeatTime = now;
        		scrollChat(true);
        	}
        }
        else if (scrollDownButtonIsDown && (scrollDuration > CHAT_WINDOW_BUTTON_SCROLL_TIME_THRESHOLD)) {
        	if ((now - scrollButtonDownRepeatTime) > CHAT_WINDOW_BUTTON_SCROLL_REPEAT_THRESHOLD)
        	{
        		scrollButtonDownRepeatTime = now;
        		scrollChat(false);
        	}
        }
        
        batch.end();
    }

    private void drawMoveHolder() {
        // The hand bg
        batch.draw(shiphand, MOVES_shiphandX, MOVES_shiphandY);

        // get cannonheights for each slot
        ArrayList<Integer> cannonHeights = new ArrayList<Integer>();
        cannonHeights.add(MOVES_cannonSlot0Y);
        cannonHeights.add(MOVES_cannonSlot1Y);
        cannonHeights.add(MOVES_cannonSlot2Y);
        cannonHeights.add(MOVES_cannonSlot3Y);

        // get moveheights for each slot
        ArrayList<Integer> moveHeights = new ArrayList<Integer>();
        moveHeights.add(MOVES_moveSlot0Y);
        moveHeights.add(MOVES_moveSlot1Y);
        moveHeights.add(MOVES_moveSlot2Y);
        moveHeights.add(MOVES_moveSlot3Y);

        for (int i = 0; i < movesHolder.length; i++) {
            // helper variables
            HandMove move = movesHolder[i];
            boolean[] left = move.getLeft();
            boolean[] right = move.getRight();
            int cH = cannonHeights.get(i);
            int mH =   moveHeights.get(i);

            // rendering: draw left (guns AB |__| CD - place A, then B)
            // (must be in this order to create blur together)
            // clicking: when clicked they must appear in order B, A
            batch.draw((left[0])?cannonLeft:emptyCannonLeft, MOVES_cannonLeftSlotSmallX, cH); // left
            if (isDoubleShot) {
                batch.draw((left[0] && left[1])?cannonLeft:emptyCannonLeft, MOVES_cannonLeftSlotBigX, cH); // left
            }

            // rendering: draw right (guns AB |__| CD - place D, then C)
            // (must be in this order to create blur together)
            // clicking: when clicked they must appear in order C, D
            if (isDoubleShot)
            {
                batch.draw((right[0])?cannonRight:emptyCannonRight, MOVES_cannonRightSlotSmallX, cH); // right
                batch.draw((right[0] && right[1])?cannonRight:emptyCannonRight, MOVES_cannonRightSlotBigX, cH); // right
            }
            else
            {
            	batch.draw((right[0])?cannonRight:emptyCannonRight, MOVES_cannonRightSlotSmallX, cH); // right
            }
            

            // draw moves and manauver
            if (isBigShip && (i == blockingMoveSlot)) {
                batch.draw(blockingMoveTexture, MOVES_moveSlotX, mH);
            }
            else
            {
                if (move.getMove() != MoveType.NONE) {
                    Color color = batch.getColor();
                    if (move.isMoveTemp()) {
                        batch.setColor(0.5F, 0.5F, 0.5F, 1F);
                    }
                    else {
                        batch.setColor(color.r, color.g, color.b, 1f);
                    }

                    batch.draw(getTextureForMove(move.getMove()), MOVES_moveSlotX, mH);
                    batch.setColor(color.r, color.g, color.b, 1f);
                }
            }
        }
    }

    private TextureRegion getTextureForMove(MoveType type) {
        switch (type) {
            case LEFT:
                return leftMoveTexture;
            case RIGHT:
                return rightMoveTexture;
            default:
            case FORWARD:
                return forwardMoveTexture;
        }
    }

    /**
     * Draws the movement selection section
     */
    @SuppressWarnings("incomplete-switch")
	private void drawMovesSelect() {
        // auto, cannons
        batch.draw(autoBackground, MOVES_autoBackgroundX, MOVES_autoBackgroundY);
        font.draw(batch, "Auto", MOVES_autoTextX, MOVES_autoTextY);
        batch.draw(auto?autoOn:autoOff,            MOVES_autoX,    MOVES_autoY);
        batch.draw((cannons>0)?cannon:emptyCannon, MOVES_cannonsX, MOVES_cannonsY);
        font.draw(batch, "x" + Integer.toString(cannons), MOVES_cannonsTextX, MOVES_cannonsTextY);

        // moves
        batch.draw((leftMoves == 0)?emptyLeftMoveTexture:leftMoveTexture, MOVES_leftX, MOVES_leftY);
        batch.draw((forwardMoves == 0)?emptyForwardMoveTexture:forwardMoveTexture, MOVES_forwardX, MOVES_forwardY);
        batch.draw((rightMoves == 0)?emptyRightMoveTexture:rightMoveTexture,  MOVES_rightX, MOVES_rightY);
        // radios
        Texture onTex  = auto?radioOnDisable:radioOn;
        Texture offTex = auto?radioOffDisable:radioOff;
        batch.draw(radioButtons[0]?onTex:offTex, MOVES_leftRadioX,    MOVES_leftRadioY);
        batch.draw(radioButtons[1]?onTex:offTex, MOVES_forwardRadioX, MOVES_forwardRadioY);
        batch.draw(radioButtons[2]?onTex:offTex, MOVES_rightRadioX,   MOVES_rightRadioY);

        // move targeting
        TextureRegion sel = auto ? moveTargetSelAuto : moveTargetSelForce;
        if (targetMove != null) {
            switch(targetMove) {
                case LEFT:
                    batch.draw(sel, MOVES_leftSelectX, MOVES_leftSelectY);
                    enableRadio(0);
                    break;
                case FORWARD:
                    batch.draw(sel, MOVES_forwardSelectX, MOVES_forwardSelectY);
                    enableRadio(1);
                    break;
                case RIGHT:
                    batch.draw(sel, MOVES_rightSelectX, MOVES_rightSelectY);
                    enableRadio(2);
                    break;
            }
        }

        // misc labels
        font.setColor(Color.BLACK);
        font.draw(batch, "x" + Integer.toString(leftMoves   ), MOVES_leftMovesTextX,    MOVES_leftMovesTextY);
        font.draw(batch, "x" + Integer.toString(forwardMoves), MOVES_forwardMovesTextX, MOVES_forwardMovesTextY);
        font.draw(batch, "x" + Integer.toString(rightMoves  ), MOVES_rightMovesTextX,   MOVES_rightMovesTextY);
    }

    /**
     * Draws the sand clock
     */
    private void drawTimer() {
        batch.draw(hourGlass, MOVES_hourGlassX, MOVES_hourGlassY);
        batch.draw(sandTrickle,MOVES_sandTrickleX, MOVES_sandTrickleY );
        batch.draw(sandTop, MOVES_sandTopX, MOVES_sandTopY);
        batch.draw(sandBottom, MOVES_sandBottomX, MOVES_sandBottomY);
    }

    /**
     * Draws ship status
     *
     * Ship damage, Ship bilge, etc
     */
    private void drawShipStatus() {
        batch.draw(shipStatusBg, MOVES_shipStatusBackgroundX, MOVES_shipStatusBackgroundY);

        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // The values for damage and water are hard-coded here, they
        // should come from your code

        float redstuff = damageAmount / 100f;
        float redStart = 90.0f + 180.0f * (1.0f - redstuff);
        float redLength = 180.0f * redstuff;

        float bluestuff = bilgeAmount / 100;

        float blueStart = 270.0f;
        float blueLength = 180.0f * bluestuff;

        shape.setColor(new Color(131 / 255f, 6 / 255f, 0f, .7f));
        shape.arc(MOVES_shipDamageX, MOVES_shipDamageY, 16.50f, redStart, redLength);
        shape.setColor(new Color(0f, 207 / 255f, 249f, .7f));
        shape.arc(MOVES_shipBilgeX, MOVES_shipBilgeY, 16.50f, blueStart, blueLength);
        shape.end();

        batch.begin();

        batch.draw(shipStatus, MOVES_shipStatusX, MOVES_shipStatusY);
    }

    public void placeMove(int slot, MoveType move, boolean temp) {
        HandMove hm = movesHolder[slot];
        hm.setMove(move);
        hm.setMoveTemporary(temp);
    }

    public void resetPlacedMovesAfterTurn() {
        for (int i = 0; i < movesHolder.length; i++) {
            movesHolder[i].setMove(MoveType.NONE);
            movesHolder[i].resetLeft();
            movesHolder[i].resetRight();

            // fix stuck moves that might appear after a turn completes
            getContext().sendSelectMoveSlot(i, MoveType.NONE);
        }

        // reset slider
        if (isBigShip) {
	        blockingMoveSlot = 3;
	        getContext().sendBlockingMoveSlotChanged(3);
        }

        // fix stuck buttons if they were clicked across a turn
        // with no penalty to the user
        if (disengageButtonIsDown) {
            disengageButtonIsDown = false;
        }
        if (sendChatButtonIsDown) {
            sendChatButtonIsDown = false;
        }
        if (scrollUpButtonIsDown) {
            scrollUpButtonIsDown = false;
        }
        if (scrollDownButtonIsDown) {
            scrollDownButtonIsDown = false;
        }
    }

    /**
     * sets cannons both on server and on client
     * --not currently used but maybe useful in future--
     */
    public void resetCannons()
    {
        for (int i=0; i<4; i++) {
            // count number of cannons we have set on each side
            boolean l[]  = movesHolder[i].getLeft();
            boolean r[] = movesHolder[i].getRight();
            
            int left  = (l[0]?1:0);
            int right = (r[0]?1:0);
            if (isDoubleShot)
            {
            	left  += (l[1]?1:0);
            	right += (r[1]?1:0);
            }

            // calculate number of cannons we need to 'add' to cancel this
            // (we can only add cannons until rollover)
            // so 3-left for big, 2-left for small
            // then send update n times as necessary
            int rolloverThreshold = isDoubleShot?3:2;
            if (left > 0) {
                for (int j=0; j<(rolloverThreshold - left); j++) {
                    getContext().sendAddCannon(0, i);
                }
                setCannons(0, i, 0);
            }
            if (right > 0) {
                for (int j=0; j<(rolloverThreshold - right); j++) {
                    getContext().sendAddCannon(1, i);
                }
                setCannons(1, i, 0);
            }
        }
    }

    /**
     * resets local copy of cannons only - does not send anything to server
     */
    public void setCannons(int side, int slot, int amount) {
        if (side == 0) {
            movesHolder[slot].resetLeft();
            for (int i = 0; i < amount; i++)
                movesHolder[slot].addLeft();
        }
        else if (side == 1) {
            movesHolder[slot].resetRight();
            for (int i = 0; i < amount; i++)
                movesHolder[slot].addRight();
        }
    }

    public void setMoveSealTarget(MoveType moveSealTarget) {
        targetMove = moveSealTarget;
    }

    public void setMovePlaces(byte[] moves, byte[] left, byte[] right) {
        for (int slot = 0; slot < 4; slot++) {
            HandMove move = movesHolder[slot];
            move.setMoveTemporary(false);
            move.resetRight();
            move.resetLeft();

            move.setMove(MoveType.forId(moves[slot]));
            for (int i = 0; i < left[slot]; i++) {
                move.addLeft();
            }
            for (int i = 0; i < right[slot]; i++) {
                move.addRight();
            }
        }
    }

    public ChatBar getChatBar() {
		return chatBar;
	}
	public void setChatBar(ChatBar chatBar) {
		this.chatBar = chatBar;
	}
	@Override
    public boolean keyDown(int keycode) {
        return getChatBar().keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return getChatBar().keyUp(keycode);
    }

    @Override
    /**
     * keytyped fires on keyDown, then subsequently if key remains down.
     */
    public boolean keyTyped(char character) {
        return getChatBar().keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
    public boolean scrolled(int amount) {
    	scrollChat(amount < 0); // 
    	return true;
    }

    @Override
    public boolean handleMouseMove(float x, float y) {
        handleTooltipActivation(x, y);
        return false;
    }
}
