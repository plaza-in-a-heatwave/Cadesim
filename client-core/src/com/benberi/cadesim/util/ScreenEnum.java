package com.benberi.cadesim.util;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.screen.LoadingScreen;
import com.benberi.cadesim.game.screen.LobbyScreen;
import com.benberi.cadesim.game.screen.MapEditorScreen;
import com.benberi.cadesim.game.screen.SeaBattleScreen;

public enum ScreenEnum {
	LOADING {
		public AbstractScreen getScreen(Object... params) {
			return new LoadingScreen((GameContext)params[0]);
		}
	},
	LOBBY {
		public AbstractScreen getScreen(Object... params) {
			return new LobbyScreen((GameContext)params[0]);
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