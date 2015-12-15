package com.ychstudio.screens;

import java.util.Iterator;

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
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;
import com.ychstudio.gamesys.WorldContactListener;
import com.ychstudio.gui.InfoGUI;
import com.ychstudio.loaders.MapLoader;

public class PlayScreen implements Screen {

    private final float WIDTH = 16f;
    private final float HEIGHT = 9f;
    
    private FitViewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    private World world;
    
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebugRenderer = true;
    
    private Array<AbstractActor> actorList;
    
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
        
        ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
        actorBuilder.createPlayer(6f, 6f);
        
        MapLoader.loadTiledMap("level_01.tmx", world);
        
        box2DDebugRenderer = new Box2DDebugRenderer();
        
        infoGUI = new InfoGUI();
    }
    
    
    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer;
        }
        
        for(Iterator<AbstractActor> iter = actorList.iterator(); iter.hasNext();) {
            AbstractActor actor = iter.next();
            if (actor.isToBeRemoved()) {
                actor.dispose();
                iter.remove();
            } else {
              actor.update(delta);  
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.607843137f, 0.737254902f, 0.058823529f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        world.step(Math.min(delta, 1 / 60f), 8, 3);
        
        update(delta);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (AbstractActor actor : actorList) {
            actor.draw(batch);
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
