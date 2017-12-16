package com.mygdx.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MainGame;
import com.mygdx.game.scenes.Hud;

import static com.mygdx.game.MainGame.*;


public class GameOverScreen implements Screen{
    private MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    public GameOverScreen(MainGame game){
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(404, 228, camera);
        viewport.apply();
        font = new BitmapFont();
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
    }
    @Override
    public void show() {

    }

    private void handleInput(float delta){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            System.exit(0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            playScreen = new PlayScreen(game);
            game.setScreen(playScreen);
            playScreen.hud = new Hud(batch);
            game.resetPlayerStats();
        }
    }

    private void update(float delta){
        handleInput(delta);
        camera.update();

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch,"GAME OVER",V_WIDTH/2,V_HEIGHT/2);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {viewport.update(width,height);

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
}
