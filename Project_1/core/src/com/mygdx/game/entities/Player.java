package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MainGame;

public class Player extends Sprite {

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation;
    private Animation<TextureRegion> currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4;

    public World world;
    public Body body;
    public Player(World world) {
        this.world = world;
        definePlayer();

    }

    public void setDown() {
        currentFrame = walkDownAnimation;
        idle = walkDownFrames[3];
    }

    public void setUp() {
        currentFrame = walkUpAnimation;
        idle = walkUpFrames[3];
    }

    public void setLeft() {
        currentFrame = walkLeftAnimation;
        idle = walkLeftFrames[3];
    }

    public void setRight() {
        currentFrame = walkRightAnimation;
        idle = walkRightFrames[3];
    }


    public enum State{
        WALKING,IDLE,ATTACKING
    }
    public State state = State.IDLE;

    public void setState(State state){
        this.state = state;
    }
    private State getState(){
        return state;
    }

    public void draw(SpriteBatch batch){
        switch(getState()){
            case WALKING:
                batch.draw(currentFrame.getKeyFrame(MainGame.stateTime, true), body.getPosition().x-11, body.getPosition().y-15);
                break;
            case IDLE:
                batch.draw(idle,body.getPosition().x-11,body.getPosition().y-15);
                break;
            case ATTACKING:

        }
    }

    public void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(100,100);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10,15);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        Texture walkSheet = new Texture(Gdx.files.internal("knight.png"));
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);
        walkDownFrames = tmp[0];
        walkLeftFrames = tmp[1];
        walkRightFrames = tmp[2];
        walkUpFrames = tmp[3];

        walkDownAnimation = new Animation(0.25f, walkDownFrames);
        walkUpAnimation = new Animation(0.25f, walkUpFrames);
        walkLeftAnimation = new Animation(0.25f, walkLeftFrames);
        walkRightAnimation = new Animation(0.25f, walkRightFrames);
        currentFrame = walkDownAnimation;
        idle = walkDownFrames[3];
    }



    public void handleInput(float delta) {
        float speed = 50f;
        float velocityX = body.getLinearVelocity().x;
        float velocityY = body.getLinearVelocity().y;
        Vector2 playerPosition = new Vector2(body.getPosition().x,body.getPosition().y);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            currentFrame = walkUpAnimation;
            idle = walkUpFrames[3];
            body.setLinearVelocity(0,speed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            currentFrame = walkDownAnimation;
            idle = walkDownFrames[3];
            body.setLinearVelocity(0,-speed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            currentFrame = walkRightAnimation;
            idle = walkRightFrames[3];
            body.setLinearVelocity(speed,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            currentFrame = walkLeftAnimation;
            idle = walkLeftFrames[3];
            body.setLinearVelocity(-speed,0);
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            setState(Player.State.IDLE);
            body.setLinearVelocity(0,0);

        } else {
            setState(Player.State.WALKING);
        }
    }
}