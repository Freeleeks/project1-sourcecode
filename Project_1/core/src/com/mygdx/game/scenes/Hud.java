package com.mygdx.game.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MainGame;

import static com.mygdx.game.MainGame.playScreen;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label currentHealthLabel;
    Label timeLabel;
    Label scoreLabel;
    Label healthLabel;
    Label levelLabel;
    Label worldLabel;

    public Hud(SpriteBatch batch){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(MainGame.V_WIDTH,MainGame.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        currentHealthLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthLabel = new Label("PLAYER",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("",new Label.LabelStyle(new BitmapFont(), Color.WHITE));


        table.add(healthLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(currentHealthLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(scoreLabel).expandX();

        stage.addActor(table);
    }


    public void update(){
        currentHealthLabel.setText(String.format("%d/%d",playScreen.player.currentHealth,playScreen.player.maxHealth));
    }
}
