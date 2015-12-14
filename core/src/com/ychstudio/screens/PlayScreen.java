package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Player;
import com.ychstudio.gamesys.GM;
import com.ychstudio.loaders.MapLoader;

public class PlayScreen implements Screen {

    private final float WIDTH = 32f;
    private final float HEIGHT = 18f;
    
    private FitViewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    private World world;
    
    private OrthogonalTiledMapRenderer mapRenderer;
    
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebugRenderer = true;
    
    Array<AbstractActor> actors;
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.translate(WIDTH / 2f, HEIGHT / 2f);
        
        world = new World(new Vector2(0, -9.8f), true);
        
        actors = GM.getActorList();
        actors.clear();
        
        TextureAtlas textureAtlas = GM.getAssetManager().get("img/actors.pack", TextureAtlas.class);
        actors.add(new Player(world, textureAtlas.findRegion("Gunner"), 6, 6, 32 / GM.PPM, 32 / GM.PPM));
        
        TiledMap map = MapLoader.loadTiledMap("map_01.tmx", world);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / GM.PPM, batch);
        
        box2DDebugRenderer = new Box2DDebugRenderer();
    }
    
    
    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer;
        }
        
        
        for (AbstractActor actor : actors) {
            actor.update(delta);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.607843137f, 0.737254902f, 0.058823529f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        world.step(Math.min(delta, 1 / 60f), 8, 3);
        
        update(delta);
        
        batch.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.begin();
        for (AbstractActor actor : actors) {
            actor.draw(batch);
        }
        batch.end();       
        
        if (showBox2DDebugRenderer) {
            box2DDebugRenderer.render(world, camera.combined);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapRenderer.dispose();
    }

}
