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
import com.ychstudio.gamesys.GM;
import com.ychstudio.network.GameServer;

public class MainMenuScreen implements Screen {
    
    private final MissionPK game;
    
    private SpriteBatch batch;
    private Stage stage;
    
    private VisTable mainTable;
    private VisTable multiplayerTable;
    private VisTable hostTable;
    private VisTable clientTable;
    private VisWindow window;
    
    private VisTable currentTable;
    
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
        multiPlayerButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(multiplayerTable);
            }
            
        });
        
        mainTable = new VisTable();
        mainTable.top().center();
        mainTable.add(singlePlayerButton).padBottom(12f);
        mainTable.row();
        mainTable.add(multiPlayerButton);
        
        
        VisTextButton hostButton = new VisTextButton("Host");
        hostButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(hostTable);
                
                if (GM.getInstance().gameServer != null) {
                    GM.getInstance().gameServer.dispose();
                }
                GM.getInstance().gameServer = new GameServer();
                GM.getInstance().gameServer.start();
            }
            
        });
        
        VisTextButton clientButton = new VisTextButton("Client");
        clientButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(clientTable);
            }
            
        });
        
        VisTextButton backToMainMenuButton = new VisTextButton("Back to MainMenu");
        backToMainMenuButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(mainTable);
            }
            
        });
        
        
        multiplayerTable = new VisTable();
        multiplayerTable.top().center();
        multiplayerTable.add(hostButton).padBottom(12f);
        multiplayerTable.row();
        multiplayerTable.add(clientButton).padBottom(12f);
        multiplayerTable.row();
        multiplayerTable.add(backToMainMenuButton);
        
        hostTable = new VisTable();
        VisTextButton startButton = new VisTextButton("Start");
        VisTextButton closeButton = new VisTextButton("Close");
        closeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GM.getInstance().gameServer != null) {
                    GM.getInstance().gameServer.stop();
                }

                changeToTable(multiplayerTable);
            }
            
        });
        hostTable.add(startButton).pad(6f);
        hostTable.add(closeButton).pad(6f);
        
        
        clientTable = new VisTable();
        VisTextButton connectButton = new VisTextButton("Connect");
        VisTextButton disconnectButton = new VisTextButton("Disconnect");
        disconnectButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(multiplayerTable);
            }
            
        });
        
        clientTable.add(connectButton).pad(6f);
        clientTable.add(disconnectButton).pad(6f);
        
        
        window = new VisWindow("Mission PK");
        window.setSize(320f, 360f);
        
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2, (Gdx.graphics.getHeight() - window.getHeight()) / 2); 
        
        stage.addActor(window);
        Gdx.input.setInputProcessor(stage);
        
        // make mainTable the current table
        changeToTable(mainTable);
    }
    
    private void changeToTable(VisTable targetTable) {
        if (currentTable != null) {
            currentTable.remove();
        }
        
        currentTable = targetTable;
        window.add(currentTable);
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
