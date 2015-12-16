package com.ychstudio.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class RigidBodyActor extends AbstractActor {
    
    protected World world;
    protected Body body;

    public RigidBodyActor(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(textureRegion, x, y, width, height);
        this.world = world;
        sprite.setPosition(x - width / 2f, y - height / 2f);
        sprite.setOriginCenter();
    }

}
