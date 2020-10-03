package com.benberi.cadesim.game.scene.impl.control;

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.benberi.cadesim.GameContext;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

/**
 * A class to represent the textfield.
 * Couldn't get input multiplexing to work so wrote a
 * subset of handlers for basic input functionality
 *
 */
public class ChatBar {
	private static final int KEY_REPEAT_THRESHOLD_MILLIS = 500;
    private int  keyDown     = -1;  // keycode if down? (or -1 for none)
    private int characterDown = -1; // charactercode if down? (or -1 for none)
    private long keyDownTimeMillis = 0;  // when did key go down?
    private boolean modifierDown = false; // ctrl or mac key
    private int maxMessageLength;
    private TextField textfield;
    private BitmapFont messageFont;
    private GameContext context;
    private Clipboard clipboard;
    private Stage stage;
    
    /**
     * constructor instantiates a ChatBar.
     * @param context            the context
     * @param CHAT_shape_chatBox dimensions of the ChatBar to draw itself
     */
    public ChatBar(GameContext context, int maxMessageLength, Rectangle CHAT_shape_chatBox) {
    	// context
    	this.context = context;
    	this.maxMessageLength = maxMessageLength;
    	
    	// stage
    	stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));;
    	
        // setup clipboard
    	
        clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
        
        // setup fonts
        messageFont = context.getManager().get(context.getAssetObject().chatFont);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = messageFont;
        style.fontColor = new Color(0.16f, 0.16f, 0.16f, 1);

        // configure chat bar
        // TODO how to change font color when selected
        //style.focusedFontColor = new Color(215f, 201f, 79f, 1);
        style.cursor = new Image(
        		context.getManager().get(context.getAssetObject().cursor)).getDrawable();
        style.cursor.setMinWidth(1f);
        style.selection = new Image(
        		context.getManager().get(context.getAssetObject().battleSelection)).getDrawable();
        setTextfield(new TextField("", style));
        getTextfield().setSize(CHAT_shape_chatBox.width, CHAT_shape_chatBox.height);
        getTextfield().setPosition(CHAT_shape_chatBox.x, CHAT_shape_chatBox.y);
        getTextfield().setColor(235f, 240f, 242f, 255f);
        getTextfield().setDisabled(false);
        getTextfield().setVisible(true);
        getTextfield().setFocusTraversal(true);
        getTextfield().setBlinkTime(0.5f);
        
        // add to stage
        stage.addActor(getTextfield());
        stage.setKeyboardFocus(getTextfield());
    }
    
    /**
     * combine the routines which should run within the update() method
     */
    public void spin() {
    	stage.act();
    	stage.draw();
    	doAccelerate();
    }
    
    /**
     * while rendering, this method accelerates a key.
     */
    private void doAccelerate() {
        if ((System.currentTimeMillis() - keyDownTimeMillis) >= KEY_REPEAT_THRESHOLD_MILLIS) {
            if (keyDown != -1) {
                handleAcceleratableKeys(keyDown);
            }
            else if (characterDown != -1) {
                handleChar((char)characterDown);
            }
        }
    }
    
    /**
     * flags a key to be accelerated during render.
     */
    private void startKeyAcceleration(int keycode) {
        // use when a key has been pushed down once.
        // it schedules acceleration.
        if (!modifierDown) {
            keyDown = keycode;
            keyDownTimeMillis = System.currentTimeMillis();
            characterDown = -1;
        }
    }
    
    /**
     * flags a character to be accelerated during render.
     */
    private void startCharacterAcceleration(char character) {
        // use when a charater has been pushed down once.
        // it schedules acceleration.
        if (!modifierDown) {
            characterDown = character;
            keyDownTimeMillis = System.currentTimeMillis();
            keyDown = -1;
        }
    }
    
    /**
     * stops a key/character being accelerated during render.
     */
    private void stopAllAcceleration() {
        // use when a key up has been detected
        if (!modifierDown) {
            keyDown = -1;
            characterDown = -1;
            keyDownTimeMillis = System.currentTimeMillis();
        }
    }
    
    /**
     * returns whether a character (not a key) is being accelerated
     * during render.
     */
    private boolean isAcceleratingCharacter(char character) {
        if (modifierDown) {
            return false;
        }
        else if (characterDown == -1) {
            return false;
        }
        else {
            return character == characterDown;
        }
    }
    
    /**
     * called when the modifier key (ctrl) has been pressed.
     */
    private void startModifier() {
        modifierDown = true;
        keyDown = -1;
        characterDown = -1;
        keyDownTimeMillis = System.currentTimeMillis();
    }
    
    /**
     * whether the modifier key is pressed.
     */
    private boolean isModifying() {
        return modifierDown;
    }
    
    /**
     * called when the modifier key is lifted.
     */
    private void stopModifier() {
        modifierDown = false;
    }
    
    /**
     * send a message via the textfield.
     */
    public void sendChat() {
        String message = getTextfield().getText();        
        if (message.length() > 0 && message.length() <= maxMessageLength) {
            context.sendPostMessagePacket(message);
        }
        getTextfield().setCursorPosition(0);
        getTextfield().setText("");
        getTextfield().clearSelection();
    }

    public TextField getTextfield() {
		return textfield;
	}

	public void setTextfield(TextField textfield) {
		this.textfield = textfield;
	}

	/**
     * custom key handler for LEFT
     */
    private void handleLeftButton(int keycode) {
        // move cursor to left of selection, if any,
        // and clear it
        if (getTextfield().getSelection().length() > 0) {
            getTextfield().setCursorPosition(getTextfield().getSelectionStart());
            getTextfield().clearSelection();
        }

        int p = getTextfield().getCursorPosition();
        if (p > 0)
        {
            getTextfield().setCursorPosition(p - 1);
        }
    }

    /**
     * custom key handler for RIGHT
     */
    private void handleRightButton(int keycode) {
        // move cursor to right of selection, if any,
        // and clear it
        int length = getTextfield().getSelection().length();
        if (length > 0) {
            getTextfield().setCursorPosition(getTextfield().getSelectionStart() + length);
            getTextfield().clearSelection();
        }

        getTextfield().clearSelection();
        int p = getTextfield().getCursorPosition();
        if (p < (getTextfield().getText().length()))
        {
            getTextfield().setCursorPosition(p + 1);
        }
    }

    /**
     * helper method to remove a selection from chat bar
     */
    private void removeSelection() {
        String selection = getTextfield().getSelection();
        int length = selection.length();
        if (length == 0) {
            return;
        }

        int start = getTextfield().getSelectionStart();

        // delete all indices not including end
        String text = getTextfield().getText();

        // get the first portion, checking the range.
        String textStart = "";
        if (start > 0) {
            textStart = text.substring(0, start - 1);
        }

        String newText = textStart + text.substring(start+length, text.length());
        getTextfield().setText(newText);
        getTextfield().setCursorPosition(start);
        getTextfield().clearSelection();
    }

    /**
     * custom key handler for backspace
     */
    private void handleBackspace(int keycode) {
        if (getTextfield().getSelection().length() > 0)
        {
            removeSelection();
        }
        else
        {
            String text = getTextfield().getText();
            int p = getTextfield().getCursorPosition();
            if (p > 0)
            {
                String newText =
                        text.substring(0, p - 1) +
                        text.substring(p, text.length()
                );
                getTextfield().setText(newText);
                getTextfield().setCursorPosition(p - 1);
            }
        }
    }

    /**
     * custom key handler for del
     */
    private void handleDel(int keycode) {
        if (getTextfield().getSelection().length() > 0)
        {
            removeSelection();
        }
        else
        {
            String text = getTextfield().getText();
            int p = getTextfield().getCursorPosition();
            if (p < text.length())
            {
                String newText =
                        text.substring(0, p) +
                        text.substring(p + 1, text.length()
                );
                getTextfield().setText(newText);
                getTextfield().setCursorPosition(p);
            }
        }
    }

    /**
     * custom key handler for any character
     */
    private void handleChar(char character) {
        // optionally remove any selection first
        if (getTextfield().getSelection().length() > 0)
        {
            removeSelection();
        }

        // insert char at cursor position
        String text = getTextfield().getText();
        int p = getTextfield().getCursorPosition();
        String newText =
            text.substring(0, p) +
            Character.toString(character) +
            text.substring(p, text.length()
        );
        if (newText.length() <= maxMessageLength) {
            getTextfield().setText(newText);
            getTextfield().setCursorPosition(p + 1);
        }
    }

    /**
     * custom key handler for Enter/Return
     */
    private void handleEnter(int keycode) {
        sendChat();
    }
    
    /**
     * custom key handler for Home
     */
    private void handleHome(int keycode) {
        // set cursor position to start of chat
        getTextfield().setCursorPosition(0);
    }
    
    /**
     * custom key handler for End
     */
    private void handleEnd(int keycode) {
        // set cursor position to end of chat
        getTextfield().setCursorPosition(getTextfield().getText().length());
    }
    
    /**
     * custom key handler for backspace
     * @param isLeft is the left key pressed?
     */
    private void handleCtrlArrow(boolean isLeft)
    {
        String text = getTextfield().getText();
        boolean found = false;
        if (isLeft)
        {
            // ctrl+left: set cursor to index of previous " ^[ ]"
            // treat all space chars the same
            for (int i=getTextfield().getCursorPosition()-1; i>=1; i--)
            {
                if (text.charAt(i-1) == ' ' && text.charAt(i) != ' ')
                {
                    getTextfield().setCursorPosition(i);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                getTextfield().setCursorPosition(0);
            }
        }
        else
        {           
            // ctrl+right: set cursor to index of next " ^[ ]", + 1
            for (int i=getTextfield().getCursorPosition(); i<text.length() - 1; i++)
            {
                if (text.charAt(i) == ' ' && text.charAt(i+1) != ' ')
                {
                    getTextfield().setCursorPosition(i+1);
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                getTextfield().setCursorPosition(text.length());
            }
        }
    }
    
    /**
     * event handler for the GameInputProcessor.keyDown event
     */
    public boolean keyDown(int keycode)
    {
    	if (isModifying()) {
            handleModifierChord(keycode);
            return true;
        }
        else if (keycode == Input.Keys.ENTER) {
            // enter shouldn't accelerate
        	stopAllAcceleration();
            handleEnter(keycode);
            return true;
        }
        else if (keycode == Input.Keys.CONTROL_LEFT) {
            startModifier();
            return true;
        }
        else if (keycode == Input.Keys.HOME) {
        	// home shouldn't accelerate
        	stopAllAcceleration();
        	handleHome(keycode);
        	return true;
        }
        else if (keycode == Input.Keys.END) {
        	// end shouldn't accelerate
        	stopAllAcceleration();
        	handleEnd(keycode);
        	return true;
        }
        else
        {
            startKeyAcceleration(keycode);
            return handleAcceleratableKeys(keycode);
        }
    }
    
    /**
     * event handler for the GameInputProcessor.keyUp event
     */
    public boolean keyUp(int keycode) {
    	if (keycode == Input.Keys.CONTROL_LEFT)
        {
            stopModifier();
        }
        else
        {
        	stopAllAcceleration();
        }

        // eat all teh keys
        return true;
    }
    
    /**
     * event handler for the GameInputProcessor.keyTyped event
     */
    public boolean keyTyped(char character) {
    	if (character >= 32 && character < 127)
        {
            if (!isAcceleratingCharacter(character))
                // only handle once
                {
            		startCharacterAcceleration(character);
                    handleChar(character); // printable ascii
                }
                return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * wrapper around textfield helper methods
     * returns true if handled, false otherwise
     */
    private boolean handleAcceleratableKeys(int keycode) {
        boolean handled = true; // assume we handle it
        if (keycode == Input.Keys.LEFT) {
            handleLeftButton(keycode);
        }
        else if (keycode == Input.Keys.RIGHT) {
            handleRightButton(keycode);
        }
        else if (keycode == Input.Keys.BACKSPACE)
        {
            handleBackspace(keycode);
        }
        else if (keycode == Input.Keys.FORWARD_DEL) { // not del!!
            handleDel(keycode);
        }
        else
        {
            handled = false;
        }

        return handled;
    }

    /**
     * custom method to handle modifier usage
     * @param keycode key that is pressed alongside modifier
     */
    private void handleModifierChord(int keycode) {
        switch (keycode) {
        case Input.Keys.LEFT:
            handleCtrlArrow(true);
            break;
        case Input.Keys.RIGHT:
            handleCtrlArrow(false);
            break;
        case Input.Keys.A:
            getTextfield().selectAll();
            break;
        case Input.Keys.C:
            StringSelection data = new StringSelection(getTextfield().getSelection());
             clipboard.setContents(data, data);
            break;
        case Input.Keys.V:
            try {
                 String pastedData = "";
                 Transferable t = clipboard.getContents(null);
                 if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
                 {
                    pastedData = (String)t.getTransferData(DataFlavor.stringFlavor);

                     // optionally remove any selection first
                     if (getTextfield().getSelection().length() > 0)
                     {
                         removeSelection();
                     }

                     // use the pasted data
                        String text = getTextfield().getText();
                        int p = getTextfield().getCursorPosition();
                        String newText =
                            text.substring(0, p) +
                            pastedData +
                            text.substring(p, text.length()
                        );
                        if (newText.length() <= maxMessageLength) {
                            getTextfield().setText(newText);
                            getTextfield().setCursorPosition(p + 1);
                        }
                 }
              } catch (Exception ex) {
                  // do nothing
              }
            break;
        default:
            // unrecognised
            break;
        }
    }
}