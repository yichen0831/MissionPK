package com.ychstudio.builders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Bullet;
import com.ychstudio.actors.Debris;
import com.ychstudio.actors.Grenade;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.tiles.BlockTile;
import com.ychstudio.actors.tiles.DirtTile;
import com.ychstudio.actors.tiles.GrassTile;
import com.ychstudio.actors.tiles.SpikeTile;
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
        GM.getTileList().add(grassTile);
    }
    
    public void createDirtTile(TiledMapTile tile, float x, float y, float width, float height) {
        int hp = Integer.valueOf((String)tile.getProperties().get("HP"));
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        DirtTile dirtTile = new DirtTile(world, textureAtlas.findRegion("Dirt1"), x, y, width, height, hp);
        GM.getTileList().add(dirtTile);
    }
    
    public void createBlockTile(TiledMapTile tile, float x, float y, float width, float height) {
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        String mode = (String) tile.getProperties().get("Mode");
        BlockTile blockTile = new BlockTile(world, textureAtlas.findRegion("Block" + mode), x, y, width, height);
        GM.getTileList().add(blockTile);
    }
    
    public void createSpikeTile(TiledMapTile tile, float x, float y, float width, float height) {
        TextureAtlas textureAtlas = assetManager.get("img/tiles.pack", TextureAtlas.class);
        String mode = (String) tile.getProperties().get("Mode");
        SpikeTile spikeTile = new SpikeTile(world, textureAtlas.findRegion("Spike" + mode), x, y, width, height);
        GM.getTileList().add(spikeTile);
    }
    
    public void createPlayer(float x, float y) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Player player = new Player(world, textureAtlas.findRegion("Gunner"), x, y, 32 / GM.PPM, 32 / GM.PPM);
        GM.setPlayer(player);
        actorList.add(player);
    }
    
    public void createBullet(float x, float y, Vector2 dir) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Bullet bullet = new Bullet(world, textureAtlas.findRegion("Bullet"), x, y, 32 / GM.PPM, 32 / GM.PPM);
        bullet.setDirection(dir);
        actorList.add(bullet);
    }
    
    public void createGrenade(float x, float y, float dir) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Grenade grenade= new Grenade(world, textureAtlas.findRegion("Grenade"), x, y, 32 / GM.PPM, 32 / GM.PPM);
        grenade.setDirection(dir);
        actorList.add(grenade);
    }
    
    public void createDebris(float x, float y, Vector2 velocity) {
        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Debris debris = new Debris(world, new TextureRegion(textureAtlas.findRegion("Debris"), 32 * MathUtils.random(3), 0, 32, 32), x, y, 32 / GM.PPM, 32 / GM.PPM);
        debris.setLinearVelocity(velocity);
        actorList.add(debris);
    }

}
