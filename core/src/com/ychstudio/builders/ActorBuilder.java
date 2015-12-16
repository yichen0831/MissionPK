package com.ychstudio.builders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.BlockTile;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.DirtTile;
import com.ychstudio.actors.GrassTile;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.SpikeTile;
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
    
    public void createGrassTile(TiledMapTile tile, float x, float y, float width, float height) {
        int hp = Integer.valueOf((String)tile.getProperties().get("HP"));
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        GrassTile grassTile = new GrassTile(world, textureAtlas.findRegion("Grass1"), x, y, width, height, hp);
        GM.getActorList().add(grassTile);
    }
    
    public void createDirtTile(TiledMapTile tile, float x, float y, float width, float height) {
        int hp = Integer.valueOf((String)tile.getProperties().get("HP"));
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        DirtTile dirtTile = new DirtTile(world, textureAtlas.findRegion("Dirt1"), x, y, width, height, hp);
        GM.getActorList().add(dirtTile);
    }
    
    public void createBlockTile(TiledMapTile tile, float x, float y, float width, float height) {
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        String mode = (String) tile.getProperties().get("Mode");
        BlockTile blockTile = new BlockTile(world, textureAtlas.findRegion("Block" + mode), x, y, width, height);
        GM.getActorList().add(blockTile);
    }
    
    public void createSpikeTile(TiledMapTile tile, float x, float y, float width, float height) {
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        String mode = (String) tile.getProperties().get("Mode");
        SpikeTile spikeTile = new SpikeTile(world, textureAtlas.findRegion("Spike" + mode), x, y, width, height);
        GM.getActorList().add(spikeTile);
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
