package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.mygdx.game.entities.Player;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.tools.WorldContactListener;

import static com.mygdx.game.MainGame.batch;
import static com.mygdx.game.MainGame.player;

public class BeachHouseScreen implements Screen {
    public MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    public World world;

    //map instantiation
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    int[] backgroundLayers = {0,1,2,3,4};
    int[] foregroundLayers = {7};


    private Box2DDebugRenderer debugRenderer;
    public Hud hud;
    private boolean isDebugRenderOn;
    private Body doorBody,body;

    public BeachHouseScreen(MainGame game) {
        this.game = game;
        this.world = new World(new Vector2(0,0),true);
        camera = new OrthographicCamera();
        viewport = new FitViewport(404, 228, camera);
        camera.position.set(viewport.getWorldWidth()/2-50, viewport.getWorldHeight()/2-37, 0);
        viewport.apply();
        camera.update();
        //map initializing
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("BeachHouse.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        debugRenderer = new Box2DDebugRenderer();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2, rectangle.getHeight() / 2);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }
        MapObject doorTransition = map.getLayers().get(6).getObjects().get(0);
        String whichTransition = doorTransition.getProperties().get("transitionId").toString();
        BodyDef doorBodyDef = new BodyDef();
        PolygonShape doorShape = new PolygonShape();
        FixtureDef doorFixtureDef = new FixtureDef();
        Rectangle rect = new Rectangle(doorTransition.getProperties().get("x",float.class), doorTransition.getProperties().get("y",float.class),16,16);
        doorBodyDef.type = BodyDef.BodyType.StaticBody;
        doorBodyDef.position.set(rect.getX()+rect.getWidth()/2,rect.getY()+rect.getWidth()/2);
        doorBody = world.createBody(doorBodyDef);
        doorBody.setUserData(whichTransition);


        doorShape.setAsBox(rect.getWidth()/2,rect.getHeight()/2);
        doorFixtureDef.shape = doorShape;
        doorFixtureDef.isSensor = true;
        doorBody.createFixture(doorFixtureDef).setUserData("transition");

        player = new Player(world);

        player.body.setTransform(viewport.getWorldWidth()/2,viewport.getWorldHeight()/2,0);
        world.setContactListener(new WorldContactListener());


    }

    @Override
    public void show() {

    }

    private void update(float delta){
        renderer.setView(camera);
        camera.update();
        player.handleInput(delta);
        world.step(1/60f,6,2);
    }
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        renderer.render(backgroundLayers);
        if (isDebugRenderOn) {
            debugRenderer.render(world, camera.combined);
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
        renderer.render(foregroundLayers);
    }

    @Override
    public void resize(int width, int height) {

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
}
