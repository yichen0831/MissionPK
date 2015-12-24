package com.ychstudio.network;

import java.io.IOException;
import java.util.HashSet;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer implements Disposable {
    
    Server server;
    
    HashSet<LoggedInPlayer> players;
    
    class LoggedInPlayer {
        public int id;
        public float x;
        public float y;
    }
    
    class PlayerConnection extends Connection {
        public LoggedInPlayer player;
    }
    
    public GameServer() {
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
            }

            @Override
            public void disconnected(Connection connection) {
            }

            @Override
            public void received(Connection connection, Object object) {
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
    }
    
    public void stop() {
        server.stop();
    }

    @Override
    public void dispose() {
        server.close();
    }

}
