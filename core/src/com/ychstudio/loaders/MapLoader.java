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
import com.ychstudio.actors.GrassTile;
import com.ychstudio.gamesys.GM;

public class MapLoader {
    
    private MapLoader(){};
    
    public static TiledMap loadTiledMap(String filename, World world) {
        TiledMap tiledMap = GM.getAssetManager().get("maps/" + filename, TiledMap.class);
        
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("TileLayer1");
        if (tileLayer != null) {
            float tileWidth = tileLayer.getTileWidth();
            float tileHeight = tileLayer.getTileHeight();
            
            
            
            for (int i = 0; i < tileLayer.getHeight(); i++) {
                for (int j = 0; j < tileLayer.getWidth(); j++) {
                    Cell cell = tileLayer.getCell(j, i);
                    if (cell == null) {
                        continue;
                    }
                    TiledMapTile tile = cell.getTile();
                    if (tile.getProperties().containsKey("Type")) {
                        switch ((String)tile.getProperties().get("Type")) {
                            case "Grass":
                                // TODO use ActorBuilder
                                GrassTile grassTile = new GrassTile(world, tile.getTextureRegion(), (j + 0.5f) / 2f, (i + 0.5f) / 2f, tileWidth / GM.PPM, tileHeight / GM.PPM, 60);
                                GM.getActorList().add(grassTile);
                                break;
                            case "Dirt":
                                break;
                            case "Block":
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
