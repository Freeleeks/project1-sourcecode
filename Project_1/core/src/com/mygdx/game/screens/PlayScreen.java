package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Enemy;
import com.mygdx.game.entities.Player;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.tools.WorldContactListener;

import java.util.ArrayList;

import static com.mygdx.game.MainGame.batch;
import static com.mygdx.game.MainGame.player;

public class PlayScreen implements Screen {
    public MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    public World world;
    //map instantiation
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int[] backGroundLayers = {0,1,2,3,4,5,6};
    private int[] foreGroundLayers = {7,8,9};
    private Music music;


    private Box2DDebugRenderer debugRenderer;

    private ArrayList<Enemy> enemies;


    public Hud hud;
    private boolean isDebugRenderOn;

    public PlayScreen(MainGame game) {
        this.game = game;
        this.world = new World(new Vector2(0,0),true);
        camera = new OrthographicCamera();
        viewport = new FitViewport(404, 228, camera);
        viewport.apply();
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
        //map initializing
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("finalWorldMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);


        debugRenderer = new Box2DDebugRenderer();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;
        for (MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2, rectangle.getHeight() / 2);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }
        music = Gdx.audio.newMusic(Gdx.files.internal("Golly_Gee.mp3"));
        music.setLooping(true);
        music.setVolume(0.2f);
        music.play();
        //player = new Player(world);
        enemies = SpawnGenerator.spawnEnemy(world,map);
        world.setContactListener(new WorldContactListener());

    }


    @Override
    public void show() {

    }

    private void handleInput(float delta){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            System.exit(0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
            isDebugRenderOn = !isDebugRenderOn;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            System.out.println("\nx: "+player.body.getPosition().x+"\ny: "+player.body.getPosition().y);
        }
    }

    public void removeEnemy(Enemy enemy){
        enemies.remove(enemy);
        enemy.killEnemy();
    }

    private void update(float delta){
        hud.update();
        renderer.setView(camera);
        camera.update();
        handleInput(delta);
        player.handleInput(delta);
        world.step(1/60f,6,2);


        for (Enemy enemy : enemies){
            enemy.movement(delta);
        }
    }



    @Override
    public void render(float delta) {
        update(delta);
        camera.position.set(player.body.getPosition().x,player.body.getPosition().y,0);
        cameraInBounds(map);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(backGroundLayers);
        if (isDebugRenderOn) {
            debugRenderer.render(world, camera.combined);
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        for (Enemy enemy : enemies){
            enemy.draw(batch);
        }
        batch.end();
        renderer.render(foreGroundLayers);
        MainGame.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
music.dispose();
    }

    private void cameraInBounds(TiledMap map) {
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

    public void createPlayer() {
        player = new Player(world);
    }
}
