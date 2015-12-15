package com.ychstudio.gamesys;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.Damagable;
import com.ychstudio.actors.RigidBodyActor;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short categoryA = fixtureA.getFilterData().categoryBits;
        short categoryB = fixtureB.getFilterData().categoryBits;

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        if (categoryA == GM.BULLET_BIT || categoryB == GM.BULLET_BIT) {
            if (categoryA == GM.BULLET_BIT) {
                Bullet bullet = (Bullet) bodyA.getUserData();
                if (bullet.isAlive()) {
                    bullet.getDamaged(1);

                    RigidBodyActor other = (RigidBodyActor) bodyB.getUserData();
                    if (other instanceof Damagable) {
                        ((Damagable) other).getDamaged(1);
                    }
                }
            } else {
                Bullet bullet = (Bullet) bodyB.getUserData();
                if (bullet.isAlive()) {
                    bullet.getDamaged(1);

                    RigidBodyActor other = (RigidBodyActor) bodyA.getUserData();
                    if (other instanceof Damagable) {
                        ((Damagable) other).getDamaged(1);
                    }
                }
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
