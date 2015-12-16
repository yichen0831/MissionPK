package com.ychstudio.actors.tiles;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.actors.Damagable;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class GrassTile extends TileActor implements Damagable {

    private static final TextureRegion damagedTextureRegion = GM.getAssetManager().get("img/tiles.pack", TextureAtlas.class).findRegion("Grass2");
    private static final float fullHP = 20;
    int hp;
    
    private static final Vector2 tmpV = new Vector2();
    
    public GrassTile(World world, TextureRegion textureRegion, float x, float y, float width, float height, int hp) {
        super(world, textureRegion, x, y, width, height);
        this.hp = hp;
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KinematicBody;
        bodyDef.position.set(x, y);
        
        body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GM.OBSTACLE_BIT;
        fixtureDef.filter.maskBits = GM.OBSTACLE_MASK_BITS;
        
        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
        
    }

    @Override
    public void update(float delta) {
        if (hp <= 0) {
            for (Fixture fixture : body.getFixtureList()) {
                Filter filter = fixture.getFilterData();
                filter.categoryBits = GM.NOTHING_BIT;
                fixture.setFilterData(filter);
            }
            
            // make particle effect
            ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
            for (int i = 0; i < 16; i++) {
                actorBuilder.createDebris(x, y, tmpV.set(MathUtils.random(-2f, 2f), MathUtils.random(0, 4f)));
            }
            queue_remove();
        }
        else if (hp <= fullHP / 2) {
            // change to the damaged texture
            sprite.setRegion(damagedTextureRegion);
        }
    }

    @Override
    public void getDamaged(int damage) {
        hp -= damage;
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }

}
