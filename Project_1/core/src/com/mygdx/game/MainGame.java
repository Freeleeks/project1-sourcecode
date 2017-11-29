package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.PlayScreen;

public class MainGame extends Game {
	public static final int V_WIDTH = 600;
	public static final int V_HEIGHT =400;
	public static SpriteBatch batch;
	public static float stateTime;
	public static PlayScreen playScreen;
	@Override
	public void create () {
		batch = new SpriteBatch();
		playScreen = new PlayScreen(this);
		setScreen(playScreen);
		stateTime=0;
	}

	@Override
	public void render () {
		stateTime+= Gdx.graphics.getDeltaTime();
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
