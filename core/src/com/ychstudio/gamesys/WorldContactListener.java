package com.ychstudio.gamesys;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.Damagable;
import com.ychstudio.actors.Explodable;
import com.ychstudio.actors.RigidBodyActor;

public class WorldContactListener implements ContactListener {
    
    private final Vector2 tmpV1 = new Vector2();
    private final Vector2 tmpV2 = new Vector2();

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short categoryA = fixtureA.getFilterData().categoryBits;
        short categoryB = fixtureB.getFilterData().categoryBits;

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        
        if (categoryA == GM.EXPLOSION_BIT || categoryB == GM.EXPLOSION_BIT) {
            if (categoryA == GM.EXPLOSION_BIT) {
                
                if (bodyB.getType() == BodyType.DynamicBody) {
                    // apply force
                    tmpV1.set(bodyB.getPosition());
                    tmpV2.set(bodyA.getPosition());
                    
                    // direction
                    tmpV1.sub(tmpV2);
                    
                    float dist = tmpV1.len();
                    float explosionRadius = ((Explodable) bodyA.getUserData()).getExplosionRadius();
                    float length = dist > explosionRadius ? 0 : explosionRadius - dist;
                    bodyB.applyLinearImpulse(tmpV1.setLength(length * 8f).scl(bodyB.getMass()), bodyB.getWorldCenter(), true);
                }
                
                RigidBodyActor other = (RigidBodyActor) bodyB.getUserData();
                if (other instanceof Damagable) {
                    ((Damagable) other).getDamaged(((Explodable) bodyA.getUserData()).getExplosionPower());
                }
            }
            else {
                if (bodyA.getType() == BodyType.DynamicBody) {
                    // apply force
                    tmpV1.set(bodyA.getPosition());
                    tmpV2.set(bodyB.getPosition());
                    
                    // direction
                    tmpV1.sub(tmpV2);
                    
                    float dist = tmpV1.len();
                    float explosionRadius = ((Explodable) bodyB.getUserData()).getExplosionRadius();
                    float length = dist > explosionRadius ? 0 : explosionRadius - dist;
                    bodyA.applyLinearImpulse(tmpV1.setLength(length * 8f).scl(bodyA.getMass()), bodyA.getWorldCenter(), true);
                }
                
                RigidBodyActor other = (RigidBodyActor) bodyA.getUserData();
                if (other instanceof Damagable) {
                    ((Damagable) other).getDamaged(((Explodable) bodyB.getUserData()).getExplosionPower());
                }
            }
        }
        else if (categoryA == GM.BULLET_BIT || categoryB == GM.BULLET_BIT) {
            if (categoryA == GM.BULLET_BIT) {
                Bullet bullet = (Bullet) bodyA.getUserData();
                if (bullet.isAlive()) {
                    bullet.hitObject();

                    RigidBodyActor other = (RigidBodyActor) bodyB.getUserData();
                    if (other instanceof Damagable) {
                        ((Damagable) other).getDamaged(bullet.getExplosionPower());
                    }
                }
            } else {
                Bullet bullet = (Bullet) bodyB.getUserData();
                if (bullet.isAlive()) {
                    bullet.hitObject();

                    RigidBodyActor other = (RigidBodyActor) bodyA.getUserData();
                    if (other instanceof Damagable) {
                        ((Damagable) other).getDamaged(bullet.getExplosionPower());
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
