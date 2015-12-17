package com.ychstudio.actors;

import com.badlogic.gdx.physics.box2d.Body;

public interface Lethal {

    public void hit(Body otherBody);
}
