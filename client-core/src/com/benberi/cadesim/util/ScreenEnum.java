package com.benberi.cadesim.util;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.screen.LoadingScreen;
import com.benberi.cadesim.game.screen.LoginScreen;
import com.benberi.cadesim.game.screen.MapEditorScreen;
import com.benberi.cadesim.game.screen.SeaBattleScreen;
import com.benberi.cadesim.game.screen.SelectionScreen;

public enum ScreenEnum {
	LOADING {
		public AbstractScreen getScreen(Object... params) {
			return new LoadingScreen((GameContext)params[0],(String)params[1]);
		}
	},
	
	SELECTION {
		public AbstractScreen getScreen(Object... params) {
			return new SelectionScreen((GameContext)params[0]);
		}
	},
	
	LOGIN {
		public AbstractScreen getScreen(Object... params) {
			return new LoginScreen((GameContext)params[0]);
		}
	},
	
	MAPEDITOR {
		public AbstractScreen getScreen(Object... params) {
			return new MapEditorScreen((GameContext)params[0]);
		}
	},
	
	GAME {
		public AbstractScreen getScreen(Object... params) {
			return new SeaBattleScreen((GameContext)params[0]);
		}
	};
	
	public abstract AbstractScreen getScreen(Object... params);
}