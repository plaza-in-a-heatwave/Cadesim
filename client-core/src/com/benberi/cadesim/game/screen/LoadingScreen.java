package com.benberi.cadesim.game.screen;

import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.util.AbstractScreen;

public class LoadingScreen extends AbstractScreen{

	public LoadingScreen(GameContext context) {
	}

	@Override
	public void buildStage() {
//      
//      else {
//      	//make sure login cannot be activated accidentally
//      	buttonConn.setTouchable(Touchable.disabled);
//          /*
//           * Cheap way of animation dots lol...
//           */
//          String dot = "";
//
//          if (connectAnimationState == 0) {
//              dot = ".";
//          }
//          else if (connectAnimationState == 1) {
//              dot = "..";
//          }
//          else if (connectAnimationState == 2) {
//              dot = "...";
//          }
//          
//              font.setColor(Color.YELLOW);
//              String text = "(" + ((System.currentTimeMillis() - loginAttemptTimestampMillis)) + "ms) ";
//
//              if (state == ConnectionSceneState.CONNECTING) {
//                  text += "Connecting, please wait";
//              }
//              else if (state == ConnectionSceneState.CREATING_PROFILE) {
//                  text += "Connected - creating profile";
//              }
//              else if (state == ConnectionSceneState.CREATING_MAP) {
//                  text += "Connected - loading board map";
//              }
//
//              GlyphLayout layout = new GlyphLayout(font, text);
//              getBatch().begin();
//              font.draw(getBatch(), text + dot, Gdx.graphics.getWidth() / 2 - (layout.width / 2), 300);
//
//          if (System.currentTimeMillis() - lastConnectionAnimatinoStateChange >= 200) {
//              connectAnimationState++;
//              lastConnectionAnimatinoStateChange = System.currentTimeMillis();
//          }
//          if(connectAnimationState > 2) {
//              connectAnimationState = 0;
//          }
//          // if screen hangs on connecting for long period of time.
//          if(System.currentTimeMillis() - loginAttemptTimestampMillis >= 8000) {
//          	setState(ConnectionSceneState.DEFAULT);
//          }
//          getBatch().end();
//      }
	}
}
