package com.ychstudio.loaders;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.gamesys.GM;

public class MapLoader {
    
    private MapLoader(){};
    
    public static TiledMap loadTiledMap(String filename, World world) {
        TiledMap tiledMap = GM.getAssetManager().get("maps/" + filename, TiledMap.class);
        
        MapLayer mapLayer = tiledMap.getLayers().get("Obstacles");
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
        
        return tiledMap;
    }
    
    public static void correctRectangle(Rectangle rectangle) {
        rectangle.x /= GM.PPM;
        rectangle.y /= GM.PPM;
        rectangle.width /= GM.PPM;
        rectangle.height /= GM.PPM;
    }

}
