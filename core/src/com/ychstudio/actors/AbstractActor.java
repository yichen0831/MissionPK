package com.ychstudio.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractActor implements Disposable {
    
    protected Sprite sprite;
    protected float x, y;
    
    protected boolean removed = false;
    
    public AbstractActor(TextureRegion textureRegion, float x, float y, float width, float height) {
        sprite = new Sprite(textureRegion);
        sprite.setBounds(x, y, width, height);
        this.x = x;
        this.y = y;
    }
    
    public abstract void update(float delta);
    
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    };
    
    public void queue_remove() {
        removed = true;
    }
    
    public boolean isToBeRemoved() {
        return removed;
    }
}
