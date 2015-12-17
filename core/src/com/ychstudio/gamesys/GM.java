package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.tiles.TileActor;

public class GM {
    private static final GM instance = new GM();
    
    // category bits
    public static final short NOTHING_BITS = 0;
    public static final short PLAYER_BITS = 1 << 0;
    public static final short OBSTACLE_BITS = 1 << 1;
    public static final short BULLET_BITS = 1 << 2;
    public static final short GRENADE_BITS = 1 << 3;
    public static final short TRAP_BITS = 1 << 4;
    public static final short DEBRIS_BITS = 1 << 5;
    
    // mask bits
    public static final short PLAYER_MASK_BITS = PLAYER_BITS | OBSTACLE_BITS | BULLET_BITS | GRENADE_BITS | TRAP_BITS;
    public static final short OBSTACLE_MASK_BITS = PLAYER_BITS | BULLET_BITS | GRENADE_BITS | DEBRIS_BITS;
    public static final short BULLET_MASK_BITS = PLAYER_BITS | OBSTACLE_BITS | BULLET_BITS | GRENADE_BITS | TRAP_BITS;
    public static final short GRENADE_MASK_BITS = PLAYER_BITS | OBSTACLE_BITS | BULLET_BITS | GRENADE_BITS | TRAP_BITS;
    public static final short TRAP_MASK_BITS = PLAYER_BITS | BULLET_BITS | GRENADE_BITS;
    public static final short DEBRIS_MASK_BITS = OBSTACLE_BITS;
    
    public static final float PPM = 32;
    
    private Player player = null;

    private AssetManager assetManager;
    
    private Array<AbstractActor> actorList;
    private Array<TileActor> tileList;
    
    public static final Vector2 playerPos = new Vector2();
    
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
