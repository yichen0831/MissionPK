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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.gamesys.GM;

public class Bullet extends RigidBodyActor {

    public enum State {
        FLY,
        EXPLODE
    }
    
    private static final float radius = 0.2f;
    private static final float speed = 12f;
    
    private Map<String, Animation> animMap;
    private Animation animation;
    
    private float stateTime = 0;
    
    private final Vector2 tmpV = new Vector2();
    
    public Bullet(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.gravityScale = 0.1f;
        
        body = world.createBody(bodyDef);
        
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GM.BULLET_BIT;
        fixtureDef.filter.maskBits = GM.OBSTACLE_BIT | GM.PLAYER_BIT | GM.BULLET_BIT;
        
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
        
        animation = new Animation(0.1f, keyFrames, PlayMode.NORMAL);
        animMap.put("explode", animation);
        
    }
    
    public void setDirection(Vector2 dir) {
        setDirection(dir.x, dir.y);
    }
    
    public void setDirection(float x, float y) {
        tmpV.set(x, y);
        tmpV.setLength2(speed * speed);
        body.setLinearVelocity(tmpV);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        
        animation = animMap.get("fly");
        
        sprite.setRegion(animation.getKeyFrame(stateTime));
        if (body.getLinearVelocity().x < 0) {
            sprite.flip(true, false);
        }
        
        x = body.getPosition().x;
        y = body.getPosition().y;
        
        sprite.setPosition(x - width / 2f, y - height / 2f);
    }


}
