package com.ychstudio.actors.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.actors.RigidBodyActor;

public abstract class TileActor extends RigidBodyActor {

    public TileActor(World world, TextureRegion textureRegion, float x, float y, float width, float height) {
        super(world, textureRegion, x, y, width, height);
    }

}
