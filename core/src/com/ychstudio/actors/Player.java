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

    private static final float RADIUS = 0.3f;

    private float moveForceGround = 0.8f;
    private float moveForceAir = 0.5f;
    private float speed = 4f;
    private float jumpForce = 8f;

    private int hp = 100;
    
    private int maxAmmo = 8;
    private int ammo = maxAmmo;
    
    private int maxGrenade = 5;
    private int grenade = maxGrenade;
    
    private float grenadeGenTime = 3f;
    private float grenadeGenTimeLeft = grenadeGenTime;
    
    private float reloadTime = 3f;
    private float reloadTimeLeft = 0;

    private static final float BULLET_CD = 0.2f;
    private float bullet_cd = 0;

    private Map<String, Animation> animMap;
    private Animation animation;

    private float stateTime = 0;
    private State state = State.IDLE;
    private State prevState = state;
    
    // action
    private boolean fire = false;
    private boolean reload = false;
    
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
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius((RADIUS / 1.2f));
        circleShape.setPosition(tmpV1.set(0, 0.1f));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.5f;
        fixtureDef.filter.categoryBits = GM.PLAYER_BITS;
        fixtureDef.filter.maskBits = GM.PLAYER_MASK_BITS;

        body.createFixture(fixtureDef);
        
        circleShape.setRadius(RADIUS);
        circleShape.setPosition(tmpV1.set(0, -0.16f));
        body.createFixture(fixtureDef);
        
        circleShape.dispose();
        
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-RADIUS, -(RADIUS + 0.18f), RADIUS, -(RADIUS + 0.18f));
        
        fixtureDef.shape = edgeShape;
        fixtureDef.friction = 0.5f;
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
        
        keyFrames.clear();
        
        // jump
        for (int i = 8; i < 12; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 * i, 0, 32, 32));
        }
        animation = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        animMap.put("jump", animation);

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        bullet_cd -= delta;

        // dead
        if (hp <= 0 && !dead) {
            stateTime = 0;
            dead = true;
        }
        
        // reload
        if (reload) {
        	reloadTimeLeft -= delta;
        }
        else {
            if (ammo <= 0) {
                reload = true;
            }
            reloadTimeLeft = reloadTime;
        }
        
        // reload is done
        if (reloadTimeLeft <= 0) {
        	ammo = maxAmmo;
        	reload = false;
        }
        
        // generate grenade
        if (grenade < maxGrenade) {
            grenadeGenTimeLeft -= delta;
            if (grenadeGenTimeLeft <= 0) {
                grenade++;
                grenadeGenTimeLeft = grenadeGenTime;
            }
        }
        
        if (!dead) {
            checkGrounded();
            
            // cheat code
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                reloadTime = 0.1f;
                grenadeGenTime = 0.1f;
            }

            // fire
            if (Gdx.input.isKeyPressed(Input.Keys.A) && bullet_cd <= 0) {
            	if (ammo > 0 && !reload) {
            		bullet_cd = BULLET_CD;
            		fire = true;
            		ammo -= 1;
            		ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
            		if (faceRight) {
            			tmpV1.set(1, 0);
            			actorBuilder.createBullet(x + 0.5f, y - 0.2f, tmpV1);
            		} else {
            			tmpV1.set(-1, 0);
            			actorBuilder.createBullet(x - 0.5f, y - 0.2f, tmpV1);
            		}
            	}
            }
            
            // reload 
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            	if (ammo < maxAmmo) {
            		reload =true;
            	}
            }
            
            // throw a grenade
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                if (grenade > 0) {
                    grenade--;
                    ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
                    actorBuilder.createGrenade(x + (faceRight ? 0.3f : -0.3f), y + 0.3f, faceRight ? 1 : -1);
                }
            }

            // jump
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(0, jumpForce * body.getMass()), body.getWorldCenter(), true);
                }
            }
            
            // aim down
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                // body.applyLinearImpulse(tmpV1.set(0, -speed),
                // body.getWorldCenter(), true);
            }
            
            // aim up
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                // body.applyLinearImpulse(tmpV1.set(0, -speed),
                // body.getWorldCenter(), true);
            }

            // move left
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(-moveForceGround * body.getMass(), 0), body.getWorldCenter(), true);
                }
                else {
                    body.applyLinearImpulse(tmpV1.set(-moveForceAir * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (body.getLinearVelocity().x < -speed) {
                    body.setLinearVelocity(-speed, body.getLinearVelocity().y);
                }
            }

            // move right
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(moveForceGround * body.getMass(), 0), body.getWorldCenter(), true);
                }
                else {
                    body.applyLinearImpulse(tmpV1.set(moveForceAir * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (body.getLinearVelocity().x > speed) {
                    body.setLinearVelocity(speed, body.getLinearVelocity().y);
                }
            }
            
            // determine state 
            if (grounded) {
                if (Math.abs(body.getLinearVelocity().x) > 0.05f) {
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
                animation = animMap.get("jump");
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
        if (body.getLinearVelocity().x < -1f) {
            faceRight = false;
        } else if (body.getLinearVelocity().x > 1f) {
            faceRight = true;
        }

        sprite.setFlip(!faceRight, false);

        x = body.getPosition().x;
        y = body.getPosition().y;

        sprite.setPosition(x - width / 2f, y - height / 2f);
    }
    
    private RayCastCallback rayCastCallback = new RayCastCallback() {

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getBody().equals(body)) {
                return -1;
            }
            short categoryBits = fixture.getFilterData().categoryBits;
            if (categoryBits == GM.DEBRIS_BITS) {
                return -1;
            }
            if (categoryBits == GM.OBSTACLE_BITS || categoryBits == GM.PLAYER_BITS || categoryBits == GM.GRENADE_BITS) {
                grounded = true;
            }
            return 0;
        }
    };

    private boolean checkGrounded() {
        grounded = false;

        for (int i = -1; i <= 1; i++) {
            tmpV1.set(body.getPosition().x + i * RADIUS, body.getPosition().y);
            tmpV2.set(tmpV1.x, tmpV1.y - (RADIUS + 0.25f));
            world.rayCast(rayCastCallback, tmpV1, tmpV2);
        }

        return grounded;
    }
    
    public Vector2 getPosition() {
        return body.getPosition();
    }
    
    public int getMaxAmmo() {
		return maxAmmo;
	}

	public int getAmmo() {
		return ammo;
	}

    public float getReloadTime() {
		return reloadTime;
	}

	public float getReloadTimeLeft() {
		return reloadTimeLeft;
	}
	
	public int getMaxGrenade() {
	    return maxGrenade;
	}
	
	public int getGrenade() {
	    return grenade;
	}
	
	public int getHP() {
	    return hp;
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
