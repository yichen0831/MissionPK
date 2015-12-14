package com.ychstudio.gamesys;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.ychstudio.actors.AbstractActor;

public class GM {
    private static final GM instance = new GM();
    
    public static final short NOTHING_BIT = 0;
    public static final short PLAYER_BIT = 1 << 0;
    public static final short OBSTACLE_BIT = 1 << 1;
    public static final short BULLET_BIT = 1 << 2;
    
    public static final float PPM = 32;

    private AssetManager assetManager;
    
    public Array<AbstractActor> actorList;
    
    private GM() {

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        
        assetManager.load("img/actors.pack", TextureAtlas.class);
        assetManager.load("maps/map_01.tmx", TiledMap.class);
        assetManager.finishLoading();
        
        actorList = new Array<>();
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
    
    public void dispose() {
        assetManager.dispose();
        actorList.clear();
    }
    
}
