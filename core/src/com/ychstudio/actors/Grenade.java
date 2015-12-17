package com.ychstudio.actors;

import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class Grenade extends RigidBodyActor implements Damagable, Lethal {

    public static final int POWER = 10;
    public static final float EXPLOSION_RADIUS = 1.6f;
    
	private static final float radius = 0.2f;
	private static final Vector2 tmpV1 = new Vector2();
	private static final Vector2 tmpV2 = new Vector2();

	private int hp = 1;
	private float countDown = 5f;
	
	private boolean explode = false;
	private HashSet<Body> hitBodySet = new HashSet<>(); 
	
	public Grenade(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
		super(world, textureRegion, x, y, width, height);
		
		sprite.setRegion(new TextureRegion(textureRegion, 0, 0, 32, 32));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.angularDamping = 0.8f;
		
		body = world.createBody(bodyDef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = GM.GRENADE_BITS;
		fixtureDef.filter.maskBits = GM.GRENADE_MASK_BITS;
		fixtureDef.density = 0.8f;
		fixtureDef.friction = 0.8f;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
	}
	
	public void setDirection(float x) {
		if (x > 0) {
			body.applyLinearImpulse(tmpV1.set(0.7f, 0.8f), body.getWorldCenter(), true);
		}
		else {
			body.applyLinearImpulse(tmpV1.set(-0.7f, 0.8f), body.getWorldCenter(), true);
		}
	}
	
	@Override
	public void getDamaged(int damage) {
		hp -= damage;
	}
	
	@Override
	public void update(float delta) {
		countDown -= delta;
		
        x = body.getPosition().x;
        y = body.getPosition().y;
        
        sprite.setPosition(x - width / 2f, y - height / 2f);

        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
        
        if (explode) {
        	queue_remove();
        }
        
        // explode
        if ((hp <=0 || countDown <= 0) && !explode) {
        	explode = true;
        	
        	// explosion check 
        	hitBodySet.clear();
        	float rad = 0;
        	for (int i = 0; i < 120; i++) {
        	    float x = body.getPosition().x + MathUtils.sin(rad) * EXPLOSION_RADIUS;
        	    float y = body.getPosition().y + MathUtils.cos(rad) * EXPLOSION_RADIUS;
        	    body.getWorld().rayCast(rayCastCallback, body.getPosition(), tmpV2.set(x, y));
        	    rad += MathUtils.PI2 / 120;
        	}
        	
        	// make particle effect
        	ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
        	for (int i = 0; i < 12; i++) {
        	    actorBuilder.createDebris(x, y, tmpV1.set(MathUtils.random(-8f, 8f), MathUtils.random(-1f, 12f)));
        	}
        }
	}
	
	private RayCastCallback rayCastCallback = new RayCastCallback() {
	    
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            
            short categoryBits = fixture.getFilterData().categoryBits;
            Body otherBody = fixture.getBody();
            if (!hitBodySet.contains(otherBody)) {
                hitBodySet.add(otherBody);
                
                // calculate force direction
                tmpV1.set(otherBody.getPosition());
                tmpV1.sub(body.getPosition());
                
                float dist = tmpV1.len();
                float force = dist > EXPLOSION_RADIUS ? 0 : EXPLOSION_RADIUS - dist;
                
                otherBody.applyLinearImpulse(tmpV1.nor().scl(force * 8f * otherBody.getMass()), otherBody.getWorldCenter(), true);
                
                RigidBodyActor other = (RigidBodyActor) otherBody.getUserData();
                if (other instanceof Damagable) {
                    ((Damagable) other).getDamaged(10);
                }
            }
            // terminate the ray
            if (categoryBits == GM.OBSTACLE_BITS || categoryBits == GM.PLAYER_BITS) {
                return 0;
            }
            return -1;
        }
	};
	
	@Override
	public void hit(Body otherBody) {
	    
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}


}
