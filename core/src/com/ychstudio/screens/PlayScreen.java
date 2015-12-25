package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.MissionPK;
import com.ychstudio.actors.AbstractActor;
import com.ychstudio.actors.Player;
import com.ychstudio.actors.tiles.TileActor;
import com.ychstudio.builders.ActorBuilder;
import com.ychstudio.gamesys.GM;
import com.ychstudio.gamesys.WorldContactListener;
import com.ychstudio.gui.InfoGUI;
import com.ychstudio.gui.OptionGUI;
import com.ychstudio.loaders.MapLoader;

public class PlayScreen implements Screen {

    public static final float WIDTH = 16f;
    public static final float HEIGHT = 9f;
    
    private float mapWidth;
    private float mapHeight;
    
    private final MissionPK game;
    
    private FitViewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    
    private World world;
    
    private Box2DDebugRenderer box2DDebugRenderer;
    private boolean showBox2DDebugRenderer = true;
    
    private Array<AbstractActor> actorList;
    private Array<TileActor> tileList;
    
    private InfoGUI infoGUI;
    private OptionGUI optionGUI;
    private boolean showOptionGUI = false;
    
    private boolean restart = false;
    private boolean backToMainMenu = false;
    
    public PlayScreen(MissionPK game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        
        world = new World(new Vector2(0, -20f), true);
        world.setContactListener(new WorldContactListener());

        actorList = GM.getActorList();
        actorList.clear();
        
        tileList = GM.getTileList();
        tileList.clear();
        
        MapLoader.loadTiledMap("level_01.tmx", world);
        mapWidth = MapLoader.mapWidth;
        mapHeight = MapLoader.mapHeight;
        
        camera.position.set(GM.player1SpawnPos, 0);
        
        box2DDebugRenderer = new Box2DDebugRenderer();
        
        infoGUI = new InfoGUI();
        optionGUI = new OptionGUI(this);
        
    }
    
    
    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            showOptionGUI = !showOptionGUI;
        }
        
        if (GM.getPlayer() == null) {
            // re-spawn player
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                ActorBuilder actorBuilder = ActorBuilder.getInstance(world);
                actorBuilder.createPlayer(GM.player1SpawnPos.x, GM.player1SpawnPos.y);
            }
        }
        
        for (int i = tileList.size - 1; i >= 0; i--) {
            TileActor actor = tileList.get(i);
            if (actor.isToBeRemoved()) {
                actor.dispose();
                tileList.removeIndex(i);
            }
            else {
                actor.update(delta);
            }
        }

        for (int i = actorList.size -1; i >= 0; i--) {
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
            float targetX = player.getPosition().x;
            float targetY = player.getPosition().y;
            
            if (targetX < WIDTH / 2f) {
            	targetX = WIDTH / 2f;
            } 
            else if (targetX > (mapWidth - WIDTH / 2f)) {
            	targetX = mapWidth - WIDTH / 2f;
            }
            
            if (targetY < HEIGHT / 2f) {
            	targetY = HEIGHT / 2f;
            }
            else if (targetY > (mapHeight - HEIGHT /2f)) {
            	targetY = mapHeight - HEIGHT / 2f;
            }
            
            targetX = MathUtils.lerp(camera.position.x, targetX, 0.1f);
            targetY = MathUtils.lerp(camera.position.y, targetY, 0.1f);
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
        if (showOptionGUI) {
            optionGUI.draw();
        }
        
        if (restart) {
            game.setScreen(new PlayScreen(game));
            return;
        }
        
        if (backToMainMenu) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }
    }
    
    public void restart() {
        restart = true;
    }
    
    public void backToMainMenu() {
        backToMainMenu = true;
    }
 
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        optionGUI.resize(width, height);
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
        optionGUI.dispose();
    }

}
