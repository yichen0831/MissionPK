package com.ychstudio.gamesys;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.Lethal;

public class WorldContactListener implements ContactListener {
    
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short categoryA = fixtureA.getFilterData().categoryBits;
        short categoryB = fixtureB.getFilterData().categoryBits;

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        
        if (categoryA == GM.BULLET_BITS || categoryB == GM.BULLET_BITS) {
            if (categoryA == GM.BULLET_BITS) {
                Bullet bullet = (Bullet) bodyA.getUserData();
                bullet.hit(bodyB);
            } else {
                Bullet bullet = (Bullet) bodyB.getUserData();
                bullet.hit(bodyA);
            }
        }
        else if (categoryA == GM.TRAP_BITS || categoryB == GM.TRAP_BITS) {
        	if (categoryA == GM.TRAP_BITS) {
        		Lethal trap = (Lethal) bodyA.getUserData();
        		trap.hit(bodyB);
        	}
        	else {
        		Lethal trap = (Lethal) bodyB.getUserData();
        		trap.hit(bodyA);
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
