package com.ychstudio.loaders;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;

public class MapLoader {
    public static float mapWidth = 0;
    public static float mapHeight = 0;
    
    private MapLoader(){};
    
    public static TiledMap loadTiledMap(String filename, World world) {
        ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
        
        TiledMap tiledMap = GM.getAssetManager().get("maps/" + filename, TiledMap.class);
        
        // create basic map and objects
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer1");
        if (tileLayer != null) {
            float tileSize = tileLayer.getTileWidth() / GM.PPM;
            float tileRatio = tileLayer.getTileWidth() / GM.PPM;
            
            // update map width and height
            mapWidth = tileLayer.getWidth() * tileRatio;
            mapHeight = tileLayer.getHeight() * tileRatio;
            
            
            for (int j = 0; j < tileLayer.getHeight(); j++) {
                for (int i = 0; i < tileLayer.getWidth(); i++) {
                    Cell cell = tileLayer.getCell(i, j);
                    if (cell == null) {
                        continue;
                    }
                    TiledMapTile tile = cell.getTile();
                    if (tile.getProperties().containsKey("Type")) {
                        switch ((String)tile.getProperties().get("Type")) {
                            case "Grass":
                                actorBuilder.createGrassTile(tile, (i + tileSize) * tileRatio, (j + tileSize) * tileRatio, tileSize, tileSize);
                                break;
                            case "Dirt":
                                actorBuilder.createDirtTile(tile, (i + tileSize) * tileRatio, (j + tileSize) * tileRatio, tileSize, tileSize);
                                break;
                            case "Block":
                                actorBuilder.createBlockTile(tile, (i + tileSize) * tileRatio, (j + tileSize) * tileRatio, tileSize, tileSize);
                                break;
                            case "Spike":
                                actorBuilder.createSpikeTile(tile, (i + tileSize) * tileRatio, (j + tileSize) * tileRatio, tileSize, tileSize);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        
        // create player
        MapLayer playerLayer = tiledMap.getLayers().get("Player");
        if (playerLayer == null) {
        	GM.playerSpawnPos.set(2.5f, 3.5f);
        }
        else {
        	for (MapObject object : playerLayer.getObjects()) {
        		Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
        		correctRectangle(rectangle);
        		
        		GM.playerSpawnPos.set(rectangle.x + rectangle.width / 2f, rectangle.y + rectangle.height / 2f);
        	}
        }
        actorBuilder.createPlayer(GM.playerSpawnPos.x, GM.playerSpawnPos.y);
        
        return tiledMap;
    }
    
    public static void correctRectangle(Rectangle rectangle) {
        rectangle.x /= GM.PPM;
        rectangle.y /= GM.PPM;
        rectangle.width /= GM.PPM;
        rectangle.height /= GM.PPM;
    }

}
