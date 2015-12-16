package com.ychstudio.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class Grenade extends RigidBodyActor implements Damagable {

	private static final float radius = 0.2f;
	private static final Vector2 tmpV = new Vector2();

	private int hp = 1;
	private float countDown = 5f;
	
	private boolean explode = false;
	
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
		fixtureDef.filter.categoryBits = GM.GRENADE_BIT;
		fixtureDef.filter.maskBits = GM.GRENADE_MASK_BITS;
		fixtureDef.density = 0.8f;
		fixtureDef.friction = 0.8f;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
	}
	
	public void setDirection(float x) {
		if (x > 0) {
			body.applyLinearImpulse(tmpV.set(0.7f, 0.8f), body.getWorldCenter(), true);
		}
		else {
			body.applyLinearImpulse(tmpV.set(-0.7f, 0.8f), body.getWorldCenter(), true);
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
        	
        	CircleShape shape = new CircleShape();
        	shape.setRadius(radius * 3f);
        	
        	FixtureDef fixtureDef = new FixtureDef();
        	fixtureDef.shape = shape;
        	fixtureDef.isSensor = true;
        	fixtureDef.filter.categoryBits = GM.EXPLOSION_BIT;
        	fixtureDef.filter.maskBits = GM.EXPLOSION_MASK_BITS;
        	
        	body.createFixture(fixtureDef);
        	shape.dispose();
        	
        	// TODO make particle effect
        	ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
        	for (int i = 0; i < 12; i++) {
        	    actorBuilder.createDebris(x, y, tmpV.set(MathUtils.random(-8f, 8f), MathUtils.random(-8f, 8f)));
        	}
        }
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}

}
