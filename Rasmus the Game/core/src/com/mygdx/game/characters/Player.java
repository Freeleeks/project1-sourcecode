package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.mygdx.game.RasmusGame;
import com.mygdx.game.world.TiledGameMap;

import java.awt.*;

import static com.mygdx.game.RasmusGame.collisionDetector;
import static com.mygdx.game.RasmusGame.player;


public class Player extends Sprite{

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation;
    private Animation<TextureRegion> currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4;

    Texture swordTexture = new Texture("sword.png");


    public Rectangle hitBox = new Rectangle(1,1);
    private KeyboardController controller = RasmusGame.controller;

    //Constructor
    public Player(){
    }

    public enum State{
        IDLE, ATTACKING, WALKING
    }

    public void setPlayerLocation(float x, float y){

        setPosition(x, y);
        hitBox.setLocation((int)(getX()),(int)(getY()));
    }

    private State state = State.IDLE;

    private void setState(State newState){
        this.state = newState;
    }

    private State getState(){
        return state;
    }

    public void movement(TiledGameMap tiledMap){
        MapProperties prop = tiledMap.getProperties();
        int mapWidth = prop.get("width", Integer.class)*16;
        int mapHeight = prop.get("height", Integer.class)*16;
        float speed = 1f;

        Rectangle tempHitBoxLocation = new Rectangle(16,16);

        if (controller.left && getX() - getWidth() / 2 > 0) {
            tempHitBoxLocation.setLocation((int)(-speed + getX()-getWidth()/2),(int)(getY()-getHeight()/2));
            if (!collisionDetector(tempHitBoxLocation)) {
                translateX(-speed);
            }
            currentFrame = walkLeftAnimation;
            idle = walkLeftFrames[3];

        }
        if (controller.right && getX() + 21< mapWidth) {
            tempHitBoxLocation.setLocation((int)(speed + getX()-getWidth()/2),(int)(getY()-getHeight()/2));
            if (!collisionDetector(tempHitBoxLocation)){
                translateX(speed);
            }
            currentFrame = walkRightAnimation;
            idle = walkRightFrames[3];

        }
        if (controller.down && getY() - getHeight() / 2 > 0) {
            tempHitBoxLocation.setLocation((int)(getX()-getWidth()/2),(int)(-speed + getY()-getHeight()/2));

            if (!collisionDetector(tempHitBoxLocation)) {
                translateY(-speed);
            }
            currentFrame = walkDownAnimation;
            idle = walkDownFrames[3];
        }
        if (controller.up && getY() + 29 <= mapHeight) {
            tempHitBoxLocation.setLocation((int)(getX()-getWidth()/2),(int)(speed + getY()-getHeight()/2));

            if (!collisionDetector(tempHitBoxLocation)) {
                translateY(speed);
            }
            currentFrame = walkUpAnimation;
            idle = walkUpFrames[3];
        }
        if (!controller.left && !controller.right && !controller.up && !controller.down) {
            setState(State.IDLE);
        } else {
            setState(State.WALKING);
        }
        hitBox.setLocation((int)(getX()),(int)(getY()));
    }

    public void createMovementAnimation(){
        Texture walkSheet = new Texture(Gdx.files.internal("knight.png"));
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);
        walkDownFrames = tmp[0];
        walkLeftFrames = tmp[1];
        walkRightFrames = tmp[2];
        walkUpFrames = tmp[3];

        walkDownAnimation = new Animation<>(0.25f, walkDownFrames);
        walkUpAnimation = new Animation<>(0.25f, walkUpFrames);
        walkLeftAnimation = new Animation<>(0.25f, walkLeftFrames);
        walkRightAnimation = new Animation<>(0.25f, walkRightFrames);
        currentFrame = walkDownAnimation;
        idle = walkDownFrames[3];

    }

    public void draw(SpriteBatch batch){
        switch(getState()){
            case WALKING:
                batch.draw(currentFrame.getKeyFrame(RasmusGame.stateTime, true), getX(), getY());
                break;
            case IDLE:
                batch.draw(idle,getX(),getY());
                break;
            case ATTACKING:

        }
    }

    private void attack(){
        Rectangle swordHitBox = new Rectangle (11,22);
        swordHitBox.setLocation((int)player.getX(),(int)player.getY());
    }
}
