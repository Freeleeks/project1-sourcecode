package com.mygdx.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Player;
import com.mygdx.game.scenes.Hud;

import static com.mygdx.game.MainGame.batch;
import static com.mygdx.game.MainGame.playScreen;
import static com.mygdx.game.MainGame.player;


public class StartMenu implements Screen {
    private MainGame game;
    private Stage stage;
    private Music music;
    private Texture backGround;
    private Image BGI;

    public StartMenu(final MainGame game) {
        this.game = game;
        backGround = new Texture("Menuimage.png");
        BGI = new Image(backGround);
        BGI.setSize(backGround.getWidth() / 2, backGround.getHeight() / 2);
        BGI.setPosition(350, 0);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        Skin MenuButtons = new Skin(Gdx.files.internal("ButtonsMenu.json"));
        Button StartGame = new TextButton("Start Game", MenuButtons);
        StartGame.setSize(150, 50);
        StartGame.setPosition(550, 305);

        Button loadGame = new TextButton("Load Game", MenuButtons);
        loadGame.setSize(150, 50);
        loadGame.setPosition(550, 205);


        Button exitGame = new TextButton("Exit Game", MenuButtons);
        exitGame.setSize(150, 50);
        exitGame.setPosition(550, 105);

        StartGame.addListener(new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                player = new Player(playScreen.world);
                player.body.setTransform(50,50,0);
                game.setScreen(playScreen);
                playScreen.hud = new Hud(batch);
                music.dispose();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        exitGame.addListener(new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(BGI);
        stage.addActor(StartGame);
        stage.addActor(loadGame);
        stage.addActor(exitGame);

        music = Gdx.audio.newMusic(Gdx.files.internal("Fluffing_a_Duck.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

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
        music.dispose();

    }
public void createNewGame(){
   playScreen = new PlayScreen(game);

    player = new Player(playScreen.world);
    player.body.setTransform(50,50,0);
    game.setScreen(playScreen);
    playScreen.hud = new Hud(batch);

}
}