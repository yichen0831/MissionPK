package com.ychstudio.actors;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class Player extends RigidBodyActor implements Damagable {

    public enum State {
        IDLE, WALK, WALK_FIRE, FIRE, JUMP, DIE
    }

    private static final float RADIUS = 0.2f;

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
    
    private float reloadTime = 1.2f;
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
    private boolean fireButtonPressed = false;
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
        
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(RADIUS - 0.02f, RADIUS * 2, tmpV1.set(0, -0.04f), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.5f;
        fixtureDef.filter.categoryBits = GM.PLAYER_BITS;
        fixtureDef.filter.maskBits = GM.PLAYER_MASK_BITS;

        body.createFixture(fixtureDef);
        
        polygonShape.dispose();
        
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-RADIUS, -(RADIUS + 0.28f), RADIUS, -(RADIUS + 0.28f));
        
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
        animation = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
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
        
        keyFrames.clear();
        
        // fire when walking
        for (int i = 12; i < 18; i++) {
            keyFrames.add(new TextureRegion(textureRegion, 32 * i, 0, 32, 32));
        }
        animation = new Animation(BULLET_CD / 5f, keyFrames, Animation.PlayMode.NORMAL);
        animMap.put("fire_walk", animation);
        

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        bullet_cd -= delta;

        // dead
        if (hp <= 0 && !dead) {
            stateTime = 0;
            dead = true;
            
            // play death sound
            GM.playSound("Death.ogg");
        }
        
        // reload
        if (reload) {
        	reloadTimeLeft -= delta;
        }
        else {
            if (ammo <= 0) {
                reloadGun();
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
            	
            	// play Da sound
            	GM.playSound("Da.ogg");
            	
                reloadTime = 0.1f;
                grenadeGenTime = 0.1f;
            }

            // fire
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                fireButtonPressed = true;
            	if (ammo > 0 && !reload && bullet_cd <= 0) {
            		bullet_cd = BULLET_CD;
            		fire = true;
            		ammo -= 1;
            		ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
            		
            		// play shooting sound
            		GM.playSound("Shoot.ogg", 1.0f, MathUtils.random(0.8f, 1.2f), 0f);
            		
            		if (faceRight) {
            			tmpV1.set(1, 0);
            			actorBuilder.createBullet(x + (RADIUS + 0.2f), y - 0.2f, tmpV1);
            		} else {
            			tmpV1.set(-1, 0);
            			actorBuilder.createBullet(x - (RADIUS + 0.2f), y - 0.2f, tmpV1);
            		}
            	}
            }
            else {
                fireButtonPressed = false;
            }
            
            // reload 
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            	if (ammo < maxAmmo) {
            		reloadGun();
            	}
            }
            
            // throw a grenade
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                if (grenade > 0) {
                    grenade--;
                    
                    // play throw grenade sound
                    GM.playSound("ThrowGrenade.ogg", 1.0f, MathUtils.random(0.9f, 1.1f), 0);
                    
                    ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
                    actorBuilder.createGrenade(x + (faceRight ? 0.3f : -0.3f), y + 0.3f, faceRight ? 1 : -1);
                }
            }

            // jump
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                if (grounded) {
                    body.applyLinearImpulse(tmpV1.set(0, jumpForce * body.getMass()), body.getWorldCenter(), true);
                    
                    // play jump sound
                    GM.playSound("Jump.ogg");
                }
            }
            
            // aim down
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            }
            
            // aim up
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            }

            // move left
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                // if fire button is pressed, do not change facing direction
                if (!fireButtonPressed) {
                    faceRight = false;
                }
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
                // if fire button is pressed, do not change facing direction
                if (!fireButtonPressed) {
                    faceRight = true;
                }
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
                    if (fire) {
                        state = State.WALK_FIRE;
                    }
                    else {
                        state = State.WALK;
                    }
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
            case WALK_FIRE:
                animation = animMap.get("fire_walk");
                if (animation.isAnimationFinished(stateTime)) {
                    fire = false;
                }
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
            	sprite.setAlpha(0);
            	if (stateTime <= 0.1f) {
                	ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
                	for (int i = 0; i < 10; i++) {
                	    actorBuilder.createDebris(x + MathUtils.random(RADIUS) * MathUtils.randomSign(), y + MathUtils.random(RADIUS) * MathUtils.randomSign(), tmpV1.set(MathUtils.random(-8f, 8f), MathUtils.random(-1f, 6f)));
                	}
            	}
            	else {
            		queue_remove();
            		GM.setPlayer(null);
            	}
            	body.setGravityScale(0);
            	body.setLinearVelocity(0, 0);
                break;
            case IDLE:
            default:
                animation = animMap.get("idle");
                break;
        }

        sprite.setRegion(animation.getKeyFrame(stateTime));

        sprite.setFlip(!faceRight, false);

        x = body.getPosition().x;
        y = body.getPosition().y;

        sprite.setPosition(x - sprite.getWidth() / 2f, y - sprite.getHeight() / 2f);
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
            tmpV2.set(tmpV1.x, tmpV1.y - (RADIUS + 0.3f));
            world.rayCast(rayCastCallback, tmpV1, tmpV2);
        }

        return grounded;
    }
    
    private void reloadGun() {
        reload = true;
        GM.playSound("Reload.ogg");
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
