package com.ychstudio.network;

import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ychstudio.network.Network.LoggedInPlayer;
import com.ychstudio.network.Network.LoginApprove;
import com.ychstudio.network.Network.LoginReject;
import com.ychstudio.network.Network.LoginRequest;
import com.ychstudio.network.Network.LoginRequire;
import com.ychstudio.network.Network.NewPlayerLogin;
import com.ychstudio.network.Network.PlayerLogout;

public class GameServer implements Disposable {
    
    Server server;
    Server udpServer; // for broadcasting
    
    Array<LoggedInPlayer> players;
    
    private boolean gameStarted = false;
    
    class PlayerConnection extends Connection {
        public LoggedInPlayer player;
    }
    
    public GameServer() {
        players = new Array<>();
        
        udpServer = new Server();
        try {
            udpServer.bind(Network.FAKE_PORT, Network.UDP_PORT);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        server = new Server() {

            @Override
            protected Connection newConnection() {
                return new PlayerConnection();
            }
            
        };
        Network.register(server);
        
        server.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                LoginRequire loginRequire = new LoginRequire();
                connection.sendTCP(loginRequire);
            }

            @Override
            public void disconnected(Connection connection) {
                PlayerConnection playerConnection = (PlayerConnection) connection;
                // if not logged in, ignore
                if (playerConnection.player == null) {
                    return;
                }
                
                PlayerLogout playerLogout = new PlayerLogout();
                playerLogout.id = playerConnection.player.id;
                
                server.sendToAllTCP(playerLogout);
                
                for (int i = players.size - 1; i >=0; i--) {
                    if (players.get(i).id == playerConnection.player.id) {
                        players.removeIndex(i);
                    }
                }
            }

            @Override
            public void received(Connection connection, Object object) {
                PlayerConnection playerConnection = (PlayerConnection) connection;
                
                if (object instanceof LoginRequest) {
                    // if already logged in, ignore
                    if (playerConnection.player != null) {
                        return;
                    }
                    
                    // game already started, no more login
                    if (gameStarted) {
                        LoginReject loginReject = new LoginReject();
                        playerConnection.sendTCP(loginReject);
                        return;
                    }
                    
                    // remove existing id
                    for (int i = players.size - 1; i >= 0; i--) {
                        if (players.get(i).id == connection.getID()) {
                            players.removeIndex(i);
                        }
                    }
                    
                    // approve connection 
                    LoggedInPlayer player = new LoggedInPlayer();
                    player.id = connection.getID();
                    player.hostString = connection.getRemoteAddressTCP().getHostString();
                    player.port = connection.getRemoteAddressTCP().getPort();
                    playerConnection.player = player;
                    
                    players.add(player);
                    
                    // announce to existing connections
                    NewPlayerLogin newPlayerLogin = new NewPlayerLogin();
                    newPlayerLogin.player = player;
                    server.sendToAllExceptTCP(connection.getID(), newPlayerLogin);
                    
                    LoginApprove loginApprove = new LoginApprove();
                    loginApprove.id = player.id;
                    
                    playerConnection.sendTCP(loginApprove);
                    
                    // announce existing players to the new player
                    for (LoggedInPlayer p : players) {
                        NewPlayerLogin npl = new NewPlayerLogin();
                        npl.player = p;
                        connection.sendTCP(npl);
                    }
                    return;
                }
            }
            
        });
        
        try {
            server.bind(Network.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        server.start();
        udpServer.start();
    }
    
    public void stop() {
        server.stop();
        udpServer.stop();
    }
    
    public void addListener(Listener listener) {
        server.addListener(listener);
    }
    
    public void removeListener(Listener listener) {
        server.removeListener(listener);
    }
    
    public void setGameStarted() {
        gameStarted= true;
    }
    
    public Array<LoggedInPlayer> getPlayers() {
        return players;
    }

    @Override
    public void dispose() {
        server.close();
        udpServer.close();
    }

}
