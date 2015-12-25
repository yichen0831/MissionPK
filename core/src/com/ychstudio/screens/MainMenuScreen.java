package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ychstudio.MissionPK;

public class MainMenuScreen implements Screen {
    
    private final MissionPK game;
    
    private SpriteBatch batch;
    private Stage stage;
    
    private FitViewport viewport;
    
    
    public MainMenuScreen(MissionPK game) {
        this.game = game;
        batch = new SpriteBatch();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        
        VisTextButton singlePlayerButton = new VisTextButton("Single Player");
        singlePlayerButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeScreen(new PlayScreen(game));
            }
        });
        VisTextButton multiPlayerButton = new VisTextButton("Multi Player");
        
        VisTable mainTable = new VisTable();
        mainTable.top().center();
        mainTable.add(singlePlayerButton).padBottom(12f);
        mainTable.row();
        mainTable.add(multiPlayerButton);
        
        VisWindow window = new VisWindow("Mission PK");
        window.add(mainTable);
        window.setSize(320f, 240f);
        
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2, (Gdx.graphics.getHeight() - window.getHeight()) / 2); 
        
        stage.addActor(window);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0.607843137f, 0.737254902f, 0.058823529f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
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
        stage.dispose();
    }

}
