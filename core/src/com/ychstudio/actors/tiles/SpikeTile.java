package com.ychstudio.actors.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.actors.Damagable;
import com.ychstudio.actors.Lethal;
import com.ychstudio.actors.RigidBodyActor;
import com.ychstudio.gamesys.GM;

public class SpikeTile extends TileActor implements Lethal {
    
    public static final int POWER = 9999; // make sudden death

    public SpikeTile(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KinematicBody;
        bodyDef.position.set(x, y);
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2.4f, height / 2.4f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GM.TRAP_BITS;
        fixtureDef.filter.maskBits = GM.TRAP_MASK_BITS;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        body.setUserData(this);
    }

    @Override
    public void update(float delta) {
        
    }

	@Override
	public void hit(Body otherBody) {
        RigidBodyActor other = (RigidBodyActor) otherBody.getUserData();
        if (other instanceof Damagable) {
            ((Damagable) other).getDamaged(POWER);
        }
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
		
	}
}
