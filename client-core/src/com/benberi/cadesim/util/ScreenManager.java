package com.benberi.cadesim.util;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {
	
	// Singleton: unique instance
	private static ScreenManager instance;
	private static AbstractScreen currentScreen;
	
	// Reference to game
	private Game game;
	
	// Singleton: private constructor
	private ScreenManager() {
		super();
	}
	
	// Singleton: retrieve instance
	public static ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}
	
	// Singleton: retrieve instance
	public static AbstractScreen getScreen() {
		return currentScreen;
	}
	
	public void setCurrentScreen(AbstractScreen screen) {
		ScreenManager.currentScreen = screen;
	}
	
	// Initialization with the game class
	public void initialize(Game game) {
		this.game = game;
	}
	
	// Show in the game the screen which enum type is received
	public void showScreen(ScreenEnum screenEnum, Object... params) {
		
		// Get current screen to dispose it
		Screen currentScreen = game.getScreen();
		
		// Show new screen
		AbstractScreen newScreen = screenEnum.getScreen(params);
		setCurrentScreen(newScreen);
		newScreen.buildStage();
		game.setScreen(newScreen);
		// Dispose previous screen
		if (currentScreen != null) {
			currentScreen.dispose();
		}
	}
}