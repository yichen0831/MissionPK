package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.tiles.TileActor;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;
import com.ychstudio.gamesys.WorldContactListener;
import com.ychstudio.gui.InfoGUI;
import com.ychstudio.loaders.MapLoader;

public class PlayScreen implements Screen {

    private final float WIDTH = 16f;
    private final float HEIGHT = 9f;
    
    private float mapWidth;
    private float mapHeight;
    
    private FitViewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    private World world;
    
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebugRenderer = true;
    
    private Array<AbstractActor> actorList;
    private Array<TileActor> tileList;
    
    private InfoGUI infoGUI;
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        camera.translate(WIDTH / 2f, HEIGHT / 2f);
        
        world = new World(new Vector2(0, -20f), true);
        world.setContactListener(new WorldContactListener());

        actorList = GM.getActorList();
        actorList.clear();
        
        tileList = GM.getTileList();
        
        ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
        actorBuilder.createPlayer(6f, 6f);
        
        MapLoader.loadTiledMap("level_01.tmx", world);
        mapWidth = MapLoader.mapWidth;
        mapHeight = MapLoader.mapHeight;
        
        box2DDebugRenderer = new Box2DDebugRenderer();
        
        infoGUI = new InfoGUI();
    }
    
    
    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer;
        }
        
        for (int i = 0; i < tileList.size; i++) {
            AbstractActor actor = tileList.get(i);
            if (actor.isToBeRemoved()) {
                actor.dispose();
                tileList.removeIndex(i);
            }
            else {
                actor.update(delta);
            }
        }

        for (int i = 0; i < actorList.size; i++) {
            AbstractActor actor = actorList.get(i);
            if (actor.isToBeRemoved()) {
                actor.dispose();
                actorList.removeIndex(i);
            }
            else {
                actor.update(delta);
            }
        }
        
    }
    
    public void handleCamera(float delta) {
        Player player = GM.getPlayer();
        // follow player
        if (player != null) {
            Vector2 playerPos = player.getPosition();
            float targetX = camera.position.x;
            float targetY = camera.position.y;
            
            if (playerPos.x > WIDTH / 2f && playerPos.x < (mapWidth - WIDTH / 2f)) {
                targetX = playerPos.x;
            }
           
            if (playerPos.y > HEIGHT / 2f && playerPos.y < (mapHeight - HEIGHT / 2f)) {
                targetY = playerPos.y;
            }
            camera.position.set(targetX, targetY, 0);
        }
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.607843137f, 0.737254902f, 0.058823529f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        world.step(Math.min(delta, 1 / 60f), 8, 3);
        
        update(delta);
        
        handleCamera(delta);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // draw tiles
        for (int i = 0; i < tileList.size; i++) {
            tileList.get(i).draw(batch);
        }
        
        // draw actors
        for (int i = 0; i < actorList.size; i++) {
            actorList.get(i).draw(batch);
        }
        
        batch.end();       
        
        if (showBox2DDebugRenderer) {
            box2DDebugRenderer.render(world, camera.combined);
        }
        
        infoGUI.draw();
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
        world.dispose();
        infoGUI.dispose();
    }

}
