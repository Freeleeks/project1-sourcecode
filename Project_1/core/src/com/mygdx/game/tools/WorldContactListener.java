package com.mygdx.game.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MainGame;
import com.mygdx.game.entities.Enemy;
import com.mygdx.game.entities.Player;

public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        swordContact(contact);
        aggroContact(contact);
        enemyContact(contact);
    }

    private void enemyContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.getFilterData().categoryBits == MainGame.ENEMY_BIT || fixB.getFilterData().categoryBits == MainGame.ENEMY_BIT){
            Fixture enemy = fixA.getFilterData().categoryBits == MainGame.ENEMY_BIT ? fixA : fixB;
            Fixture object = enemy == fixA ? fixB : fixA;
            if (object.getUserData() != null && Player.class.isAssignableFrom(object.getUserData().getClass())){
                ((Player) object.getUserData()).takeDamage();
                ((Enemy) enemy.getUserData()).bounceBack();
            }
        }
    }

    private void aggroContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.getFilterData().categoryBits == MainGame.AGGRO_BIT || fixB.getFilterData().categoryBits == MainGame.AGGRO_BIT){
            Fixture aggroRadius = fixA.getFilterData().categoryBits == MainGame.AGGRO_BIT ? fixA : fixB;
            Fixture object = aggroRadius == fixA ? fixB : fixA;
            if (object.getUserData() != null && Player.class.isAssignableFrom(object.getUserData().getClass())){
                ((Enemy)aggroRadius.getUserData()).setChasing();
            }
        }
    }

    private void swordContact(Contact contact){
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        if(fixA.getUserData() == "sword attack" || fixB.getUserData() == "sword attack"){
            Fixture swordAttack = fixA.getUserData() == "sword attack" ? fixA : fixB;
            Fixture object = swordAttack == fixA ? fixB : fixA;

            if (object.getUserData() != null && Enemy.class.isAssignableFrom(object.getUserData().getClass())){
                ((Enemy) object.getUserData()).takeDamage();
                ((Enemy) object.getUserData()).bounceBack();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
