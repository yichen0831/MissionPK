package com.ychstudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ychstudio.MissionPK;
import com.ychstudio.gamesys.GM;
import com.ychstudio.network.GameClient;
import com.ychstudio.network.GameServer;
import com.ychstudio.network.Network.LoggedInPlayer;
import com.ychstudio.network.Network.LoginRequest;

public class MainMenuScreen implements Screen {
    
    private final MissionPK game;
    
    private SpriteBatch batch;
    private Stage stage;
    
    private Array<LoggedInPlayer> players;
    private VisList<String> playerList;
    
    private VisTable mainTable;
    private VisTable multiplayerTable;
    private VisTable hostTable;
    private VisTable clientTable;
    private VisWindow window;
    
    private VisTable currentTable;
    
    private FitViewport viewport;
    
    private boolean needUpdatePlayerList = false;
    
    
    class ServerListener extends Listener {

        @Override
        public void disconnected(Connection connection) {
            needUpdatePlayerList = true;
        }

        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof LoginRequest) {
                needUpdatePlayerList = true;
            }
        }
        
    }
    
    
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
        
        // -- main table --
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
                GM.getInstance().gameServer.addListener(new ServerListener());
                GM.getInstance().gameServer.start();
                
                players = GM.getInstance().gameServer.getPlayers();
                
                // connect to the server 
                 if (GM.getInstance().gameClient != null) {
                     GM.getInstance().gameClient.dispose();
                 }
                 
                 GM.getInstance().gameClient = new GameClient("localhost");
                 
            }
            
        });
        
        VisTextButton clientButton = new VisTextButton("Client");
        clientButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(clientTable);
                
                if (GM.getInstance().gameClient != null) {
                    GM.getInstance().gameClient.dispose();
                }
                
                // TODO search LAN and manually enter IP address
                GM.getInstance().gameClient = new GameClient("localhost");
            }
            
        });
        
        VisTextButton backToMainMenuButton = new VisTextButton("Back to MainMenu");
        backToMainMenuButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeToTable(mainTable);
            }
            
        });
        
        
        // -- multiplayer table --
        multiplayerTable = new VisTable();
        multiplayerTable.top().center();
        multiplayerTable.add(hostButton).padBottom(12f);
        multiplayerTable.row();
        multiplayerTable.add(clientButton).padBottom(12f);
        multiplayerTable.row();
        multiplayerTable.add(backToMainMenuButton);
        
        
        // -- host table --
        hostTable = new VisTable();
        // player list
        playerList = new VisList<>();
        playerList.setFillParent(true);
        
        VisScrollPane scrollPane = new VisScrollPane(playerList);
        scrollPane.setFillParent(true);
        
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
        hostTable.add(scrollPane).padBottom(6f);
        hostTable.row();
        hostTable.add(startButton).pad(6f);
        hostTable.add(closeButton).pad(6f);
        
        
        // -- client table --
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
        
        
        // -- window --
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
    
    public void updatePlayerList() {
        Array<String> playerInfo = new Array<>();
        
        for (int i = 0; i < players.size; i++) {
            playerInfo.add("ID: " + players.get(i).id + "  -  " + players.get(i).hostString);
        }
        
        playerList.setItems(playerInfo);
    }

    @Override
    public void render(float delta) {
        if (needUpdatePlayerList) {
           updatePlayerList();
           needUpdatePlayerList = false;
        }
        
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
