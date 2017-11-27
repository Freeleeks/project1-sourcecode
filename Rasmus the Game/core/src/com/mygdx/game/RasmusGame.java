package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.characters.*;
import com.mygdx.game.world.TiledGameMap;

import java.awt.*;
import java.util.ArrayList;

public class RasmusGame extends ApplicationAdapter {

	private static SpriteBatch batch;
	private OrthographicCamera camera;
	public static KeyboardController controller;

	public static Player player;

	private ArrayList<Enemy> enemies;
	private Rectangle spawnRectangle;

	private TiledGameMap tiledMap;
	private static ArrayList<Rectangle> collisionRectangles;
	public static float stateTime,deltaTime,mapWidth,mapHeight;

	@Override
	public void create () {

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		controller = new KeyboardController();
		batch = new SpriteBatch();

		//create map
		tiledMap = new TiledGameMap("worldMap2.tmx");
		mapHeight = tiledMap.getHeight()*16;
		mapWidth = tiledMap.getWidth()*16;
		//create collision map
		collisionRectangles = new ArrayList<>();
		MapLayer collisionMapLayer = tiledMap.getLayers("Mountains");
		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) collisionMapLayer;
		for(int i=0;i<tiledMap.getWidth();i++){
			for(int j=0;j<tiledMap.getHeight();j++){
				TiledMapTileLayer.Cell cell = collisionLayer.getCell(i, j);
				if (cell == null) continue; // There is no cell
				if (cell.getTile() == null) continue; // No tile inside cell
				if (cell.getTile().getId() != 0){
					Rectangle temp = new Rectangle(i*16,j*16,16,16);
					collisionRectangles.add(temp);
				}
			}
		}

		//create player
		player = new Player();
		SpawnGenerator.spawnPlayer(tiledMap);
		stateTime=0f;

		//Create camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w,h);
		Viewport viewport = new FitViewport(404,227,camera);
		spawnRectangle = new Rectangle(420,250);
		viewport.apply();
		camera.position.set(player.getX(),player.getY(),0);
		camera.update();

		//create enemy
		enemies = SpawnGenerator.spawnEnemy(tiledMap);
	}

	@Override
	public void render () {

		//Background
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.input.setInputProcessor(controller);

		//Timers
		stateTime += Gdx.graphics.getDeltaTime();
		deltaTime = Gdx.graphics.getDeltaTime();

		//Movement
		player.movement(tiledMap);
		for (Enemy enemy : enemies) {
			 enemy.movement();
		}

		camera.position.set(player.getX(),player.getY(),0);
		cameraInBounds(tiledMap);
		spawnRectangle.setLocation((int)(player.getX()-camera.viewportWidth/2),(int)(player.getY()-camera.viewportHeight/2));



		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tiledMap.render(camera);
		batch.begin();
		for(Enemy enemy : enemies){
			enemy.draw(batch);
		}
		player.draw(batch);
		batch.end();
	}


	public static boolean collisionDetector(Rectangle playerRectangle){
		boolean isCollision = false;
		for(Rectangle rectangle : collisionRectangles){
			if(rectangle.intersects(playerRectangle)){
				 isCollision = true;
			}
		}
		return isCollision;
	}

	private void cameraInBounds(TiledGameMap map) {
		MapProperties prop = map.getProperties();
		int mapWidth = prop.get("width", Integer.class)*16;
		int mapHeight = prop.get("height", Integer.class)*16;
		float cameraX = camera.position.x;
		float cameraY = camera.position.y;
		float viewportHalfHeight = camera.viewportHeight/2;
		float viewportHalfWidth = camera.viewportWidth/2;

		//Left Clamp
		if(cameraX < viewportHalfWidth) {
			camera.position.x = viewportHalfWidth;
		}
		//Right Clamp
		if(cameraX+viewportHalfWidth>mapWidth){
			camera.position.x = mapWidth-viewportHalfWidth;
		}
		//Top Clamp
		if(cameraY+viewportHalfHeight>mapHeight){
			camera.position.y = mapHeight-viewportHalfHeight;
		}
		//Bottom Clamp
		if(cameraY<viewportHalfHeight){
			camera.position.y = viewportHalfHeight;
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		tiledMap.dispose();
	}

}
