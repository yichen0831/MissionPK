package com.ychstudio.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.gamesys.GM;

public class Debris extends RigidBodyActor {

    private static final float radius = 0.05f;
    
    private float countDown = 30f;
    
    public Debris(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 0.5f;
        bodyDef.angularDamping = 1f;
        
        body = world.createBody(bodyDef);
        
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GM.DEBRIS_BIT;
        fixtureDef.filter.maskBits = GM.DEBRIS_MASK_BITS;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0.8f;
        
        body.createFixture(fixtureDef);
        body.setUserData(this);
        
        shape.dispose();
        
    }
    
    public void setLinearVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    @Override
    public void update(float delta) {
        countDown -= delta;
        
        if (countDown <= 0) {
            queue_remove();
        }
        
        x = body.getPosition().x;
        y = body.getPosition().y;
        
        sprite.setPosition(x - width / 2f, y - height / 2f);
        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }
    
    
    @Override
    public void dispose() {
        world.destroyBody(body);
    }


}
