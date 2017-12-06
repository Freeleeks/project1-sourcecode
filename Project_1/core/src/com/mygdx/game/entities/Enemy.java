package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MainGame;

import java.security.SecureRandom;

import static java.lang.Math.abs;

public class Enemy extends Sprite {

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation;
    private Animation<TextureRegion> currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4;
    private static SecureRandom random = new SecureRandom();
    private FixtureDef fixtureDef;

    public World world;
    public Body body;
    private float enemyTimer,speed;
    private int health;

    public Enemy(World world, float x, float y){
        this.world = world;

        //Define collision
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        body = world.createBody(bodyDef);
        fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8,8);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = MainGame.ENEMY_BIT;
        body.createFixture(fixtureDef).setUserData(this);

        //Define Aggro Radius

        FixtureDef aggroFixtureDef = new FixtureDef();
        CircleShape aggroShape = new CircleShape();
        aggroShape.setRadius(45);
        aggroFixtureDef.shape = aggroShape;
        aggroFixtureDef.isSensor = true;
        aggroFixtureDef.filter.categoryBits = MainGame.AGGRO_BIT;
        body.createFixture(aggroFixtureDef).setUserData(this);





        //Animation
        Texture walkSheet = new Texture(Gdx.files.internal("Enemy1.png"));
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

        enemyTimer=0;
        health = 2;
        speed = 25f;
    }

    public void killEnemy(){
        Filter filter = new Filter();
        filter.categoryBits = MainGame.DEAD_BIT;
        for(Fixture fixture : body.getFixtureList()) {
                fixture.setFilterData(filter);
        }
    }

    private void setState(State state) {
        this.state = state;
    }

    private State getState() {
        return state;
    }

    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    public Facing getFacing(){
        return facing;
    }

    public void setChasing() {
        setState(State.CHASING);
    }

    public void stop() {
        body.setLinearVelocity(0,0);
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



    private Facing facing = Facing.getRandom();
    private State state = State.WALKING;



    public void takeDamage(){
        health-=1;
        if (health<=0){
            MainGame.playScreen.removeEnemy(this);
        }
    }

    public void bounceBack(){
        float deltaX = MainGame.playScreen.player.body.getPosition().x-this.body.getPosition().x;
        float deltaY = MainGame.playScreen.player.body.getPosition().y-this.body.getPosition().y;
        float coeffX = -deltaX/(abs(deltaX)+abs(deltaY));
        float coeffY = -deltaY/(abs(deltaX)+abs(deltaY));
        this.body.setLinearVelocity(700*coeffX,700*coeffY);
        setState(State.BOUNCEBACK);
        enemyTimer = 0;

    }
    private void walking() {
        float limitTimer = 2f;
        if (enemyTimer <= limitTimer) {
            switch(facing){
                case UP:
                    currentFrame = walkUpAnimation;
                    idle = walkUpFrames[3];
                    body.setLinearVelocity(0,speed);
                    break;
                case DOWN:
                    currentFrame = walkDownAnimation;
                    idle = walkDownFrames[3];
                    body.setLinearVelocity(0,-speed);
                    break;
                case LEFT:
                    currentFrame = walkLeftAnimation;
                    idle = walkLeftFrames[3];
                    body.setLinearVelocity(-speed,0);
                    break;
                case RIGHT:
                    currentFrame = walkRightAnimation;
                    idle = walkRightFrames[3];
                    body.setLinearVelocity(speed,0);
                    break;
            }
        } else if (enemyTimer > limitTimer) {
            enemyTimer -= limitTimer;
            setState(State.IDLE);
            body.setLinearVelocity(0,0);
        }
    }

    private void idle() {
        float limitTimer=random.nextInt(20)*.1f+.1f;
        if(enemyTimer>limitTimer) {
            setFacing(Facing.getRandom());
            enemyTimer-=limitTimer;
            setState(State.WALKING);
        }
    }

    public void movement(float delta){
        enemyTimer += delta;

        isChasing();
        switch(state){
            case WALKING:
                walking();
                break;
            case IDLE:
                idle();
                break;
            case CHASING:
                chasing();
                break;
            case BOUNCEBACK:
                if (enemyTimer>.3f){
                    setState(State.CHASING);
                }
        }
    }

    private void chasing() {
        float deltaX = MainGame.playScreen.player.body.getPosition().x-body.getPosition().x;
        float deltaY = MainGame.playScreen.player.body.getPosition().y-body.getPosition().y;
        float coeffX = deltaX/(abs(deltaX)+abs(deltaY));
        float coeffY = deltaY/(abs(deltaX)+abs(deltaY));
        body.setLinearVelocity(speed*coeffX,speed*coeffY);

        if((int)deltaX<0){
            if((int)abs(deltaX)>(int)abs(deltaY))
                currentFrame = walkLeftAnimation;
        }else if ((int)deltaX>0){
            if((int)abs(deltaX)>(int)abs(deltaY))
                currentFrame = walkRightAnimation;
        }
        if((int)deltaY<0){
            if((int)abs(deltaX)<(int)abs(deltaY))
                currentFrame = walkDownAnimation;
        }else if ((int)deltaY>0){
            if((int)abs(deltaX)<(int)abs(deltaY))
                currentFrame = walkUpAnimation;
        }

    }

    private void isChasing() {

    }




    public void draw(SpriteBatch batch) {
        float bodyX = body.getPosition().x-12;
        float bodyY = body.getPosition().y-8;
        if (getState().equals(State.WALKING) || getState().equals(State.CHASING)) {
            batch.draw(currentFrame.getKeyFrame(MainGame.stateTime, true),bodyX,bodyY);
        } else {
            batch.draw(idle, bodyX, bodyY);
        }
    }
}
