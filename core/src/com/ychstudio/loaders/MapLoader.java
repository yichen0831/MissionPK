package com.ychstudio.loaders;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
        
        MapLayer mapLayer = tiledMap.getLayers().get("Obstacles");
        if (mapLayer != null) {
            for (MapObject object : mapLayer.getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                correctRectangle(rectangle);

                BodyDef bodyDef= new BodyDef();
                bodyDef.type = BodyType.StaticBody;
                bodyDef.position.set(rectangle.x + rectangle.width / 2f, rectangle.y + rectangle.height / 2f);
                Body body = world.createBody(bodyDef);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rectangle.width / 2f, rectangle.height / 2f);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.filter.categoryBits = GM.OBSTACLE_BIT;
                fixtureDef.filter.maskBits = GM.PLAYER_BIT | GM.BULLET_BIT;
                fixtureDef.friction = 0.6f;

                body.createFixture(fixtureDef);
                shape.dispose();
            }
        }
        
        return tiledMap;
    }
    
    public static void correctRectangle(Rectangle rectangle) {
        rectangle.x /= GM.PPM;
        rectangle.y /= GM.PPM;
        rectangle.width /= GM.PPM;
        rectangle.height /= GM.PPM;
    }

}
