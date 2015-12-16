package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.tiles.TileActor;

public class GM {
    private static final GM instance = new GM();
    
    // category bits
    public static final short NOTHING_BIT = 0;
    public static final short PLAYER_BIT = 1 << 0;
    public static final short OBSTACLE_BIT = 1 << 1;
    public static final short BULLET_BIT = 1 << 2;
    public static final short GRENADE_BIT = 1 << 3;
    public static final short TRAP_BIT = 1 << 4;
    public static final short EXPLOSION_BIT = 1 << 5;
    
    // mask bits
    public static final short PLAYER_MASK_BITS = PLAYER_BIT | OBSTACLE_BIT | BULLET_BIT | GRENADE_BIT | TRAP_BIT | EXPLOSION_BIT;
    public static final short OBSTACLE_MASK_BITS = PLAYER_BIT | BULLET_BIT | GRENADE_BIT | EXPLOSION_BIT;
    public static final short BULLET_MASK_BITS = PLAYER_BIT | OBSTACLE_BIT | BULLET_BIT | GRENADE_BIT | TRAP_BIT | EXPLOSION_BIT;
    public static final short GRENADE_MASK_BITS = PLAYER_BIT | OBSTACLE_BIT | BULLET_BIT | GRENADE_BIT | TRAP_BIT | EXPLOSION_BIT;
    public static final short TRAP_MASK_BITS = PLAYER_BIT | BULLET_BIT | GRENADE_BIT | EXPLOSION_BIT;
    public static final short EXPLOSION_MASK_BITS = PLAYER_BIT | OBSTACLE_BIT | BULLET_BIT | GRENADE_BIT | TRAP_BIT |EXPLOSION_BIT;
    
    public static final float PPM = 32;
    
    private Player player = null;

    private AssetManager assetManager;
    
    private Array<AbstractActor> actorList;
    private Array<TileActor> tileList;
    
    private GM() {

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        
        assetManager.load("img/actors.pack", TextureAtlas.class);
        assetManager.load("img/tiles.pack", TextureAtlas.class);
        assetManager.load("maps/level_01.tmx", TiledMap.class);
        assetManager.finishLoading();
        
        actorList = new Array<>();
        tileList = new Array<>();
    }
    
    public static GM getInstance() {
        return instance;
    }
    
    public static AssetManager getAssetManager() {
        return instance.assetManager;
    }
    
    public static Array<AbstractActor> getActorList() {
        return instance.actorList;
    }
    
    public static Array<TileActor> getTileList() {
        return instance.tileList;
    }
    
    public static void setPlayer(Player player) {
        instance.player = player;
    }
    
    public static Player getPlayer() {
        return instance.player;
    }
    
    public void dispose() {
        assetManager.dispose();
        actorList.clear();
        tileList.clear();
    }
    
}
