package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.entities.Player;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.BeachHouseScreen;
import com.mygdx.game.screens.OldHouseScreen;
import com.mygdx.game.screens.PlayScreen;
import com.mygdx.game.screens.ShopHouseScreen;

public class MainGame extends Game {
	public static final int V_WIDTH = 600;
	public static final int V_HEIGHT =400;

	public static SpriteBatch batch;
	public static float stateTime;
	public static PlayScreen playScreen;

	public static BeachHouseScreen beachHouseScreen;
	public static ShopHouseScreen shopHouseScreen;
	public static OldHouseScreen oldHouseScreen;

	public static final short DEFAULT_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short AGGRO_BIT = 4;
	public static final short SWORD_BIT = 8;
	public static final short ENEMY_BIT = 16;
	public static final short DEAD_BIT = 32;
	public static final short BLOCK_BIT = 64;
	public static Player player;
	public static World world;
	public static int currentHealth;
	public static int maxHealth;


	@Override
	public void create () {
		resetPlayerStats();
		batch = new SpriteBatch();
		playScreen = new PlayScreen(this);
		beachHouseScreen = new BeachHouseScreen(this);
		shopHouseScreen = new ShopHouseScreen(this);
		oldHouseScreen = new OldHouseScreen(this);
		setScreen(beachHouseScreen);
		playScreen.hud = new Hud(batch);
		stateTime=0;
	}

	public void resetPlayerStats(){
		currentHealth = maxHealth = 3;
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
