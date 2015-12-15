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
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class Player extends RigidBodyActor implements Damagable {

    public enum State {
        IDLE, WALK, FIRE, JUMP, DIE
    }

    private static final float RADIUS = 0.45f;

    private float moveForceGround = 1f;
    private float moveForceAir = 0.5f;
    private float speed = 4f;
    private float jumpForce = 7.2f;

    private int hp = 30;

    private static final float BULLET_CD = 0.2f;
    private float bullet_cd = 0;

    private Map<String, Animation> animMap;
    private Animation animation;

    private float stateTime = 0;
    private State state = State.IDLE;
    private State prevState = state;
    
    // action
    private boolean fire = false;

    
    // flag
    private boolean faceRight = true;
    private boolean grounded;
    private boolean dead = false;

    private final Vector2 tmpV1 = new Vector2();
    private final Vector2 tmpV2 = new Vector2();


    public Player(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RADIUS / 1.5f);
        circleShape.setPosition(tmpV1.set(0, 0.1f));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GM.PLAYER_BIT;
        fixtureDef.filter.maskBits = GM.PLAYER_MASK_BITS;

        body.createFixture(fixtureDef);
        
        circleShape.setPosition(tmpV1.set(0, -0.16f));
        body.createFixture(fixtureDef);
        
        circleShape.dispose();
        
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-(RADIUS / 1.6f), -(RADIUS + 0.05f), (RADIUS / 1.6f), -(RADIUS + 0.05f));
        
        fixtureDef.shape = edgeShape;
        body.createFixture(fixtureDef);
        
        edgeShape.dispose();

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

        keyFrames.clear();

        // fire
        for (int i = 4; i < 8; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 * i, 0, 32, 32));
        }
        animation = new Animation(BULLET_CD / 5f, keyFrames, Animation.PlayMode.NORMAL);
        animMap.put("fire", animation);

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        bullet_cd -= delta;

        if (hp <= 0 && !dead) {
            stateTime = 0;
            dead = true;
        }

        if (!dead) {
            checkGrounded();

            // fire
            if (Gdx.input.isKeyPressed(Input.Keys.X) && bullet_cd <= 0) {
                bullet_cd = BULLET_CD;
                fire = true;
                ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
                if (faceRight) {
                    tmpV1.set(1, 0);
                    actorBuilder.createBullet(x + 0.7f, y - 0.2f, tmpV1);
                } else {
                    tmpV1.set(-1, 0);
                    actorBuilder.createBullet(x - 0.7f, y - 0.2f, tmpV1);
                }
            }

            // jump
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(0, jumpForce), body.getWorldCenter(), true);
                }
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                // body.applyLinearImpulse(tmpV1.set(0, -speed),
                // body.getWorldCenter(), true);
            }

            // move left
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(-moveForceGround, 0), body.getWorldCenter(), true);
                }
                else {
                    body.applyLinearImpulse(tmpV1.set(-moveForceAir, 0), body.getWorldCenter(), true);
                }
                if (body.getLinearVelocity().x < -speed) {
                    body.setLinearVelocity(-speed, body.getLinearVelocity().y);
                }
            }

            // move right
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(moveForceGround, 0), body.getWorldCenter(), true);
                }
                else {
                    body.applyLinearImpulse(tmpV1.set(moveForceAir, 0), body.getWorldCenter(), true);
                }
                if (body.getLinearVelocity().x > speed) {
                    body.setLinearVelocity(speed, body.getLinearVelocity().y);
                }
            }
            
            // determine state 
            if (grounded) {
                if (Math.abs(body.getLinearVelocity().x) > 0.2f) {
                    state = State.WALK;
                    fire = false; // skip fire animation
                } else {
                    if (fire) {
                        state = State.FIRE;
                    }
                    else {
                        state = State.IDLE;
                    }
                }
            }
            else {
                if (fire) {
                    state = State.FIRE;
                }
                else {
                    state = State.JUMP;
                }
            }

        } else {
            state = State.DIE;
        }
        
        if (prevState != state) {
            stateTime = 0;
            prevState = state;
        }

        switch (state) {
            case JUMP:
                animation = animMap.get("idle");
                break;
            case WALK:
                animation = animMap.get("move");
                break;
            case FIRE:
                animation = animMap.get("fire");
                if (animation.isAnimationFinished(stateTime)) {
                    fire = false;
                }
                break;
            case DIE:
                // TODO add animation
                break;
            case IDLE:
            default:
                animation = animMap.get("idle");
                break;
        }

        sprite.setRegion(animation.getKeyFrame(stateTime));
        if (body.getLinearVelocity().x < 0) {
            faceRight = false;
        } else if (body.getLinearVelocity().x > 0) {
            faceRight = true;
        }

        sprite.setFlip(!faceRight, false);

        x = body.getPosition().x;
        y = body.getPosition().y;

        sprite.setPosition(x - width / 2f, y - height / 2f);
    }

    private boolean checkGrounded() {
        grounded = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getBody().equals(body)) {
                    return 1;
                }
                short categoryBits = fixture.getFilterData().categoryBits;
                if (categoryBits == GM.OBSTACLE_BIT || categoryBits == GM.PLAYER_BIT) {
                    grounded = true;
                }
                return 0;
            }
        };

        for (int i = -1; i < 1; i++) {
            tmpV1.set(body.getPosition().x + i * (RADIUS - 0.1f), body.getPosition().y);
            tmpV2.set(tmpV1.x, tmpV1.y - (RADIUS + 0.2f));
            world.rayCast(rayCastCallback, tmpV1, tmpV2);
        }

        return grounded;
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
