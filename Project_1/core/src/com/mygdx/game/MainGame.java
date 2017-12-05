package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;

public class MainGame extends Game {
	public static final int V_WIDTH = 600;
	public static final int V_HEIGHT =400;

	public static SpriteBatch batch;
	public static float stateTime;
	public static PlayScreen playScreen;

	public static final short DEFAULT_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short AGGRO_BIT = 4;
	public static final short SWORD_BIT = 8;
	public static final short ENEMY_BIT = 16;
	public static final short DEAD_BIT = 32;

	@Override
	public void create () {
		batch = new SpriteBatch();
		playScreen = new PlayScreen(this);
		setScreen(playScreen);
		playScreen.hud = new Hud(batch);

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
