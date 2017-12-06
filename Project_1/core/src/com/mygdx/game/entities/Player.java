package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MainGame;
import com.mygdx.game.screens.GameOverScreen;

import static com.mygdx.game.MainGame.playScreen;

public class Player extends Sprite {

    //Animation
    private TextureRegion[] walkDownFrames,walkLeftFrames,walkRightFrames,walkUpFrames, blockFrames;
    private TextureRegion idle;
    private Animation<TextureRegion> walkDownAnimation,walkUpAnimation,walkLeftAnimation,walkRightAnimation, blockAnimation,currentFrame;
    private static final int FRAME_COLS = 4, FRAME_ROWS = 4,SHIELD_FRAME_COLS = 3,SHIELD_FRAME_ROWS = 2;
    private float attackTimer, attackSpeed, blockTimer, blockSpeed, blockAnimationTimer;
    public int currentHealth, maxHealth;
    Texture blockSheet;

    private World world;
    public Body body;
    private Body swordBody;
    private Body blockBody;

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

        BodyDef blockBodyDef = new BodyDef();
        blockBodyDef.type = BodyDef.BodyType.StaticBody;
        blockBody = world.createBody(blockBodyDef);
        FixtureDef blockFixtureDef = new FixtureDef();
        CircleShape blockShape = new CircleShape();
        blockShape.setRadius(20);
        blockFixtureDef.shape = blockShape;
        blockFixtureDef.filter.categoryBits = MainGame.BLOCK_BIT;
        blockFixtureDef.filter.maskBits = MainGame.ENEMY_BIT;
        blockBody.createFixture(blockFixtureDef);
        blockBody.setActive(false);


        swordSprite = new Sprite(new Texture("sword.png"));
        attackSpeed = 1/3f;
        blockSpeed = 1f;
        swordSprite.setOrigin(0,0);

        //Define Walking animation
        Texture walkSheet = new Texture("knight.png");
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);
        walkDownFrames = tmp[0];
        walkLeftFrames = tmp[1];
        walkRightFrames = tmp[2];
        walkUpFrames = tmp[3];

        walkDownAnimation = new Animation<TextureRegion>(0.25f, walkDownFrames);
        walkUpAnimation = new Animation<TextureRegion>(0.25f, walkUpFrames);
        walkLeftAnimation = new Animation<TextureRegion>(0.25f, walkLeftFrames);
        walkRightAnimation = new Animation<TextureRegion>(0.25f, walkRightFrames);
        currentFrame = walkDownAnimation;
        idle = walkDownFrames[3];

        //Define Shield Animation
        blockSheet = new Texture("bubblepop.png");
        tmp = TextureRegion.split(blockSheet,
                blockSheet.getWidth()/SHIELD_FRAME_COLS,
                blockSheet.getHeight()/SHIELD_FRAME_ROWS);
        int index = 0;
        blockFrames = new TextureRegion[SHIELD_FRAME_ROWS*SHIELD_FRAME_COLS];
        for(int i=0;i<SHIELD_FRAME_ROWS;i++){
            for(int j=0;j<SHIELD_FRAME_COLS;j++){
                blockFrames[index++] = tmp[i][j];
            }
        }
        blockAnimation = new Animation<TextureRegion>(0.2f, blockFrames);
    }



    public enum State{
        WALKING,IDLE, DEAD, ATTACKING,BLOCKING
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
                swordSprite.draw(batch, 10);
                break;
            case BLOCKING:
                blockAnimationTimer +=Gdx.graphics.getDeltaTime();
                batch.draw(idle,body.getPosition().x-11,body.getPosition().y-8);
                batch.draw(blockAnimation.getKeyFrame(blockAnimationTimer,true), blockBody.getPosition().x- blockSheet.getWidth()/6, blockBody.getPosition().y- blockSheet.getHeight()/4);
        }
    }

    public void takeDamage(){
        currentHealth-=1;
        if (currentHealth<=0){
            setState(State.DEAD);
            playScreen.game.setScreen(new GameOverScreen(MainGame.playScreen.game));
        }
    }

    public void handleInput(float delta) {
        swordSprite.rotate( 17*attackSpeed);
        float speed = 50f;
        attackTimer+=delta;
        blockTimer +=delta;
        swordBody.setTransform(body.getPosition().x,body.getPosition().y,0);
        blockBody.setTransform(body.getPosition().x,body.getPosition().y,0);

        switch(getState()) {
            case WALKING:
                movementKeys(speed);
                break;
            case IDLE:
                movementKeys(speed);
                break;
            case ATTACKING:
                if (attackTimer>attackSpeed){
                    setState(State.IDLE);
                    swordBody.setActive(false);
                }
                break;
            case BLOCKING:
                if (blockTimer > blockSpeed){
                    setState(State.IDLE);
                    blockBody.setActive(false);
                }
                break;
            case DEAD:
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            if (attackTimer>attackSpeed) {
                attackTimer = 0;
                setState(State.ATTACKING);
                attack();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.X) && !getState().equals(State.ATTACKING)){

            if(blockTimer > blockSpeed) {
                blockAnimationTimer = 0;
                blockTimer = 0;
                block();
                setState(State.BLOCKING);
                body.setLinearVelocity(0,0);
            }
        }
    }

    private void block() {
        blockBody.setActive(true);
    }

    private void movementKeys(float speed) {
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
    }

    private void attack() {
        swordBody.setActive(true);
        switch(facing){
            case UP:
                swordSprite.setRotation(0);
                swordSprite.setPosition(body.getPosition().x,body.getPosition().y+10);
                break;
            case DOWN:
                swordSprite.setRotation(180);
                swordSprite.setPosition(body.getPosition().x,body.getPosition().y-10);
                break;
            case LEFT:
                swordSprite.setRotation(90);
                swordSprite.setPosition(body.getPosition().x-10,body.getPosition().y);
                break;
            case RIGHT:
                swordSprite.setRotation(270);
                swordSprite.setPosition(body.getPosition().x+10,body.getPosition().y);
                break;
        }
    }
}