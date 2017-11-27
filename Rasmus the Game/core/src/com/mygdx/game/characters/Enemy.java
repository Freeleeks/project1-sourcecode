package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.RasmusGame;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.security.SecureRandom;

import static com.mygdx.game.RasmusGame.deltaTime;
import static com.mygdx.game.RasmusGame.player;
import static java.lang.Math.abs;

public class Enemy extends Sprite {

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation;
    private Animation<TextureRegion> currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4;

    private static final SecureRandom random = new SecureRandom();

    private float enemyX,enemyY;
    private Rectangle tempHitBoxLocation = new Rectangle(21,29);
    private Rectangle hitBox = new Rectangle(10,29);

    private float currentSpeed, speed, enemyTimer,deltaX,deltaY, bounceYCoefficient, bounceXCoefficient, bounceXSpeed,bounceYSpeed;
    private Coordinates spawnCoordinates;


    public Enemy(Texture texture){
        super(texture);
    }
    public Enemy(){}

    private void setState(State state) {
        this.state = state;
    }

    private State getState() {
        return state;
    }

    public enum State{
        WALKING,
        CHASING,
        IDLE, BOUNCEBACK,


    }

    public enum Facing{
        LEFT,RIGHT,DOWN,UP;
        public static Facing getRandom() {
            return values()[random.nextInt(values().length)];
        }
    }



    private Facing facing = Facing.DOWN;
    private State state = State.WALKING;

    void create(Coordinates coordinates){
        spawnCoordinates = coordinates;
        speed = 0.5f;
        currentSpeed = speed;
        createMovementAnimation("enemy1.png");
        setPosition(coordinates.getX(),coordinates.getY());
        hitBox.setLocation((int)(getX()-getWidth()/2),(int)(getY()-getHeight()/2));
        changeDirection();
    }

    private void isChasing(){
        Circle aggroCircle = new Circle(32);
        aggroCircle.setCenterX(getX());
        aggroCircle.setCenterY(getY());
        if (aggroCircle.intersects(player.hitBox.getX(), player.hitBox.getY(),16,16)){
            setState(State.CHASING);
        }
    }

    private void createMovementAnimation(String textureMap){
        Texture walkSheet = new Texture(Gdx.files.internal(textureMap));
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

    private void setFacing(Facing newFacing) {this.facing = newFacing;}

    private Facing getFacing(){return facing;}

    private Coordinates chooseDirection() {
        setFacing(Facing.getRandom());
        Coordinates point = new Coordinates(0,0);
        switch (facing) {
            case UP:
                tempHitBoxLocation.setLocation((int) (getX() - getWidth() / 2), (int) (getY() + speed - getHeight() / 2));
                if (!RasmusGame.collisionDetector(tempHitBoxLocation)) {
                    point.setY(1f);
                } else {
                    point.setY(-1f);
                }
                currentFrame = walkUpAnimation;
                idle = walkUpFrames[3];
                break;
            case DOWN:
                tempHitBoxLocation.setLocation((int) (getX() - getWidth()), (int) (getY() - speed - getHeight() / 2));
                if (!RasmusGame.collisionDetector(tempHitBoxLocation)) {
                    point.setY(-1f);
                } else {
                    point.setY(1f);
                }
                currentFrame = walkDownAnimation;
                idle = walkDownFrames[3];
                break;
            case LEFT:
                tempHitBoxLocation.setLocation((int) (getX() - speed - getWidth() / 2), (int) (-getHeight() / 2 + getY()));
                if (!RasmusGame.collisionDetector(tempHitBoxLocation)) {
                    point.setX(-1f);
                } else {
                    point.setX(1f);
                }
                currentFrame = walkLeftAnimation;
                idle = walkLeftFrames[3];
                break;
            case RIGHT:
                tempHitBoxLocation.setLocation((int) (getX() + speed - getWidth() / 2), (int) (-getHeight() / 2 + getY()));
                if (!RasmusGame.collisionDetector(tempHitBoxLocation)) {
                    point.setX(1f);
                } else {
                    point.setX(-1f);
                }
                currentFrame = walkRightAnimation;
                idle = walkRightFrames[3];
                break;
        }
        return point;
    }

    private void changeDirection(){
        Coordinates enemyVector = chooseDirection();
        enemyX = enemyVector.getX()*speed;
        enemyY = enemyVector.getY()*speed;
    }

    public void movement(){
        isChasing();
        hitBox.setLocation((int) (getX() - getWidth() / 2), (int) (getY() - getHeight() / 2));
        if (hitBox.intersects(player.hitBox) && getState().equals(State.CHASING)) {
            setState(State.BOUNCEBACK);
            enemyTimer = 0;
            if (deltaX > 0) {
                bounceXSpeed = 2.5f;
            } else {
                bounceXSpeed =-2.5f;
            }
            if(deltaY > 0) {
                bounceYSpeed = 2.5f;
            }else{
                bounceYSpeed = -2.5f;
            }
            bounceYCoefficient = abs(deltaY/(abs(deltaY)+abs(deltaX)));
            bounceXCoefficient = abs(deltaX/(abs(deltaX)+abs(deltaY)));
        }
        switch(state){
            case WALKING:
                walking();
                break;
            case IDLE:
                idle();
            break;
            case CHASING:
                chasing();
            case BOUNCEBACK:
                bounceBack();
        }

    }

    private void bounceBack() {
        tempHitBoxLocation.setLocation((int)(getX()-getWidth()/2+bounceXSpeed*bounceXCoefficient),(int)(getY()-getHeight()/2+bounceYSpeed*bounceYCoefficient));
        if(enemyTimer<0.5f && !RasmusGame.collisionDetector(tempHitBoxLocation)) {
            translate(bounceXSpeed*bounceXCoefficient,bounceYSpeed*bounceYCoefficient);
        }else if (enemyTimer>3f){
            currentSpeed = 0.5f;
            speed = 0.5f;
            setState(State.CHASING);
        }
        enemyTimer+=deltaTime;
    }

    private void chasing() {
        deltaX = (getX()- player.getX());
        deltaY = (getY()- player.getY());

        if((int)deltaX>0){
            if((int)abs(deltaX)>(int)abs(deltaY))
            currentFrame = walkLeftAnimation;
            tempHitBoxLocation.setLocation((int) (getX() - currentSpeed - getWidth() / 2), (int) (getY()  - getHeight() / 2));
            if(!RasmusGame.collisionDetector(tempHitBoxLocation))
            translateX(-currentSpeed);
        }else if ((int)deltaX<0){
            if((int)abs(deltaX)>(int)abs(deltaY))
                currentFrame = walkRightAnimation;
            tempHitBoxLocation.setLocation((int) (getX() + currentSpeed - getWidth() / 2), (int) (getY() - getHeight() / 2));
            if(!RasmusGame.collisionDetector(tempHitBoxLocation))
                translateX(currentSpeed);

        }
        if((int)deltaY>0){
            if((int)abs(deltaX)<(int)abs(deltaY))
                currentFrame = walkDownAnimation;
            tempHitBoxLocation.setLocation((int) (getX() - getWidth() / 2), (int) (getY() - currentSpeed - getHeight() / 2));
            if(!RasmusGame.collisionDetector(tempHitBoxLocation))
                translateY(-currentSpeed);
        }else if ((int)deltaY<0){
            if((int)abs(deltaX)<(int)abs(deltaY))
                currentFrame = walkUpAnimation;
            tempHitBoxLocation.setLocation((int) (getX() - getWidth() / 2), (int) (getY() + currentSpeed - getHeight() / 2));
            if(!RasmusGame.collisionDetector(tempHitBoxLocation))
                translateY(currentSpeed);
        }
    }

    private void idle() {
        enemyTimer += RasmusGame.deltaTime;
        float limitTimer=random.nextInt(20)*.1f+.1f;
        if(enemyTimer>limitTimer) {
            changeDirection();
            enemyTimer-=limitTimer;
            setState(State.WALKING);
        }
    }

    private void walking() {
        enemyTimer += RasmusGame.deltaTime;
        float limitTimer = 1f;
        if (enemyTimer <= limitTimer) {
            if (!RasmusGame.collisionDetector(hitBox) && getX()>0 && getY()>0 && getY()<RasmusGame.mapHeight && getX()<RasmusGame.mapWidth) {
                translate(enemyX, enemyY);
            }
        } else if (enemyTimer > limitTimer) {
            translate(-enemyX, -enemyY);
            enemyTimer -= limitTimer;
            setState(State.IDLE);
        }
    }

    public void draw(SpriteBatch batch) {
        if (getState().equals(State.WALKING) || getState().equals(State.CHASING)) {
            batch.draw(currentFrame.getKeyFrame(RasmusGame.stateTime, true), getX(), getY());
        } else {
            batch.draw(idle, getX(), getY());
        }
    }
}
