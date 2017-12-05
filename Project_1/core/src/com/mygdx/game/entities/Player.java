package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MainGame;
import com.mygdx.game.screens.GameOverScreen;

import static com.mygdx.game.MainGame.playScreen;

public class Player extends Sprite {

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation;
    private Animation<TextureRegion> currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4;
    private float attackTimer;
    public int currentHealth, maxHealth;

    private World world;
    public Body body;
    private Body swordBody;

    Sprite swordSprite;

    public Player(World world) {
        this.world = world;

        //Define collision
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(100,100);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8,8);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = MainGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = MainGame.DEFAULT_BIT | MainGame.ENEMY_BIT | MainGame.AGGRO_BIT;
        body.createFixture(fixtureDef).setUserData(this);


        //Define health
        currentHealth = maxHealth = 3;


        //Define sword attack area
        BodyDef swordBodyDef = new BodyDef();
        swordBodyDef.type = BodyDef.BodyType.StaticBody;
        swordBody = world.createBody(swordBodyDef);
        FixtureDef swordFixtureDef = new FixtureDef();
        CircleShape swordAttack = new CircleShape();
        swordAttack.setRadius(30);
        swordFixtureDef.shape = swordAttack;
        swordFixtureDef.isSensor = true;
        swordFixtureDef.filter.categoryBits = MainGame.SWORD_BIT;
        swordFixtureDef.filter.maskBits = MainGame.ENEMY_BIT;
        swordBody.createFixture(swordFixtureDef).setUserData("sword attack");
        swordBody.setActive(false);

        swordSprite = new Sprite(new Texture("sword.png"));

        //Define animation
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



    public enum State{
        WALKING,IDLE, DEAD, ATTACKING
    }

    public enum Facing{
        LEFT,RIGHT,UP,DOWN
    }
    public Facing facing = Facing.DOWN;
    public State state = State.IDLE;

    public void setFacing(Facing facing){this.facing = facing;}

    public Facing getFacing(){return facing;}

    public void setState(State state){
        this.state = state;
    }

    private State getState(){
        return state;
    }

    public void draw(SpriteBatch batch){
        switch(getState()){
            case WALKING:
                batch.draw(currentFrame.getKeyFrame(MainGame.stateTime, true), body.getPosition().x-11, body.getPosition().y-8);
                break;
            case IDLE:
                batch.draw(idle,body.getPosition().x-11,body.getPosition().y-8);
                break;
            case ATTACKING:
                batch.draw(idle,body.getPosition().x-11,body.getPosition().y-8);
                drawSword(batch);
                break;
        }
    }

    private void drawSword(SpriteBatch batch){
        swordSprite.rotate((float) 6.0);
        swordSprite.draw(batch, 10);
    }

    public void takeDamage(){
        currentHealth-=1;
        if (currentHealth<=0){
            setState(State.DEAD);
            playScreen.game.setScreen(new GameOverScreen(MainGame.playScreen.game));
        }
    }

    public void handleInput(float delta) {
        float speed = 50f;
        attackTimer+=delta;
        swordBody.setTransform(body.getPosition().x,body.getPosition().y,0);
        swordSprite.setPosition(body.getPosition().x,body.getPosition().y);
        if (!getState().equals(State.ATTACKING) && !getState().equals(State.DEAD)) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                setFacing(Facing.UP);
                currentFrame = walkUpAnimation;
                idle = walkUpFrames[3];
                body.setLinearVelocity(0, speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                setFacing(Facing.DOWN);
                currentFrame = walkDownAnimation;
                idle = walkDownFrames[3];
                body.setLinearVelocity(0, -speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                setFacing(Facing.RIGHT);
                currentFrame = walkRightAnimation;
                idle = walkRightFrames[3];
                body.setLinearVelocity(speed, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                setFacing(Facing.LEFT);
                currentFrame = walkLeftAnimation;
                idle = walkLeftFrames[3];
                body.setLinearVelocity(-speed, 0);
            }
            if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                setState(Player.State.IDLE);
                body.setLinearVelocity(0, 0);

            } else {
                setState(Player.State.WALKING);
            }
        }else{
            if (attackTimer>.3f){
                setState(State.IDLE);
                swordBody.setActive(false);
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            if (attackTimer>.3f) {
                attackTimer = 0;
                setState(State.ATTACKING);
                body.setLinearVelocity(0, 0);
                attack();
            }
        }
    }

    private void attack() {
        swordBody.setActive(true);
        switch(facing){
            case UP:
                break;
            case DOWN:
                break;
            case LEFT:
                break;
            case RIGHT:
                break;
        }
    }
}