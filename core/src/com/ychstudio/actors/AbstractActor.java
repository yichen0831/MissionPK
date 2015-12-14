package com.ychstudio.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class AbstractActor {
    
    protected Sprite sprite;
    protected float x, y;
    protected float width, height;
    
    public AbstractActor(TextureRegion textureRegion, float x, float y, float width, float height) {
        sprite = new Sprite(textureRegion);
        sprite.setBounds(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
}
