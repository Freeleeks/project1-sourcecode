package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.RasmusGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Rasmus: The Search for the Magical Hat";
		config.useGL30 = true;
		config.height=768;
		config.width=1366;
		config.fullscreen=false;
		new LwjglApplication(new RasmusGame(), config);
	}
}
