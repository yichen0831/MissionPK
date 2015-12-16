package com.ychstudio.actors;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.gamesys.GM;

public class Bullet extends RigidBodyActor implements Damagable {

    public enum State {
        FLY,
        EXPLODE
    }
    
    public static final int POWER = 1;
    
    private static final float RADIUS = 0.1f;
    private static final float SPEED = 12f;
    
    private static final Vector2 tmpV = new Vector2();

    private int hp = 1;
    
    private Map<String, Animation> animMap;
    private Animation animation;
    
    private float stateTime = 0;
    private boolean explode = false;
    
    private State state = State.FLY;
    
    public Bullet(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.gravityScale = 0f;
        
        body = world.createBody(bodyDef);
        body.setUserData(this);
        
        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GM.BULLET_BIT;
        fixtureDef.filter.maskBits = GM.BULLET_MASK_BITS;
        
        body.createFixture(fixtureDef);
        
        shape.dispose();
        
        animMap = new HashMap<>();
        Array<TextureRegion> keyFrames = new Array<>();
        
        // fly
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 *i, 0, 32, 32));
        }
        animation = new Animation(0.1f, keyFrames, PlayMode.LOOP);
        animMap.put("fly", animation);
        
        keyFrames.clear();
        
        // explode
        for (int i = 4; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 *i, 0, 32, 32));
        }
        
        animation = new Animation(0.05f, keyFrames, PlayMode.NORMAL);
        animMap.put("explode", animation);
        
    }
    
    public void setDirection(Vector2 dir) {
        setDirection(dir.x, dir.y);
    }
    
    public void setDirection(float x, float y) {
        tmpV.set(x, y);
        tmpV.setLength2(SPEED * SPEED);
        body.setLinearVelocity(tmpV);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        
        if (hp <= 0 || stateTime > 10f) {
            explode = true;
        }
        
        if (explode) {
            if(state != State.EXPLODE) {
                stateTime = 0;
                state = State.EXPLODE;
                body.setLinearVelocity(0, 0);
                for (Fixture fixture : body.getFixtureList()) {
                    Filter filter = fixture.getFilterData();
                    filter.categoryBits = GM.NOTHING_BIT;
                    fixture.setFilterData(filter);
                }
            }
        }
        
        switch (state) {
            case EXPLODE:
                animation = animMap.get("explode");
                
                if (animation.isAnimationFinished(stateTime)) {
                    queue_remove();
                }
                break;
            case FLY:
            default:
                animation = animMap.get("fly");
                break;
        }
        
        sprite.setRegion(animation.getKeyFrame(stateTime));
        
        x = body.getPosition().x;
        y = body.getPosition().y;
        
        sprite.setPosition(x - width / 2f, y - height / 2f);
        
    }
    
    public void hitObject() {
        hp = 0;
    }
    
    public boolean isAlive() {
        return hp > 0;
    }
    
    @Override
    public void dispose() {
        world.destroyBody(body);
    }

    @Override
    public void getDamaged(int damage) {
        hp -= damage;
    }


}
