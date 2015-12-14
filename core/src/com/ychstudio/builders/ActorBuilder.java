package com.ychstudio.builders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.Player;
import com.ychstudio.gamesys.GM;

public class ActorBuilder {
    private static final ActorBuilder instance = new ActorBuilder();
    
    private World world;
    private AssetManager assetManager = GM.getAssetManager();
    private Array<AbstractActor> actorList = GM.getActorList(); 
    
    private ActorBuilder() {}
    
    public static ActorBuilder getInstance(World world) {
        instance.world = world;
        return instance;
    }
    
    public void createPlayer(float x, float y) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        actorList.add(new Player(world, textureAtlas.findRegion("Gunner"), x, y, 32 / GM.PPM, 32 / GM.PPM));
    }
    
    public void createBullet(float x, float y, Vector2 dir) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Bullet bullet = new Bullet(world, textureAtlas.findRegion("Bullet"), x, y, 32 / GM.PPM, 32 / GM.PPM);
        bullet.setDirection(dir);
        actorList.add(bullet);
    }

}
