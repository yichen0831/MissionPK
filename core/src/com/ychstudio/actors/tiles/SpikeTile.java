package com.ychstudio.actors.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.gamesys.GM;

public class SpikeTile extends TileActor {
    
    public static final int power = 10;

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
        fixtureDef.filter.categoryBits = GM.TRAP_BIT;
        fixtureDef.filter.maskBits = GM.TRAP_MASK_BITS;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        body.setUserData(this);
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
        
    }

    @Override
    public void update(float delta) {
        
    }

}
