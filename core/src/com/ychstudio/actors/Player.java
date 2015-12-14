package com.ychstudio.actors;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class Player extends RigidBodyActor {

    public enum State {
        IDLE, WALK, JUMP, DIE,
    }

    private final float radius = 0.45f;
    
    private float speed = 6f;

    private Map<String, Animation> animMap;
    private Animation animation;

    private float stateTime = 0;
    
    private boolean faceRight = true;
    
    private final Vector2 tmpV = new Vector2();

    public Player(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GM.PLAYER_BIT;
        fixtureDef.filter.maskBits = GM.OBSTACLE_BIT | GM.BULLET_BIT | GM.PLAYER_BIT;

        body.createFixture(fixtureDef);
        body.setUserData(this);
        circleShape.dispose();

        animMap = new HashMap<>();
        Array<TextureRegion> keyFrames = new Array<>();

        // idle
        keyFrames.add(new TextureRegion(textureRegion, 0, 0, 32, 32));
        animation = new Animation(0.1f, keyFrames);
        animMap.put("idle", animation);

        keyFrames.clear();

        // move
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 * i, 0, 32, 32));
        }
        animation = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        animMap.put("move", animation);

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
            if (faceRight) {
                tmpV.set(1, 0);
                actorBuilder.createBullet(x + 0.7f, y - 0.2f, tmpV);
            }
            else {
                tmpV.set(-1, 0);
                actorBuilder.createBullet(x - 0.7f, y - 0.2f, tmpV);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.applyLinearImpulse(tmpV.set(0, speed), body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.applyLinearImpulse(tmpV.set(0, -speed), body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.applyLinearImpulse(tmpV.set(-speed - body.getLinearVelocity().x, 0), body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.applyLinearImpulse(tmpV.set(speed - body.getLinearVelocity().x, 0), body.getWorldCenter(), true);
        }
        
        if (Math.abs(body.getLinearVelocity().x) > 0.2f) {
            animation = animMap.get("move");
        } else {
            animation = animMap.get("idle");
        }
        
        sprite.setRegion(animation.getKeyFrame(stateTime));
        if (body.getLinearVelocity().x < 0) {
            faceRight = false;
        }
        else if (body.getLinearVelocity().x > 0) {
            faceRight = true;
        }
        
        sprite.setFlip(!faceRight, false);

        x = body.getPosition().x;
        y = body.getPosition().y;

        sprite.setPosition(x - width / 2f, y - height / 2f);
    }

}
