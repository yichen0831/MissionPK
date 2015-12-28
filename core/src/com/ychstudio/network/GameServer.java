package com.ychstudio.network;

import java.io.IOException;
import java.util.HashSet;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer implements Disposable {
    
    Server server;
    Server udpServer; // for broadcasting
    
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
        udpServer.start();
    }
    
    public void stop() {
        server.stop();
        udpServer.stop();
    }

    @Override
    public void dispose() {
        server.close();
        udpServer.close();
    }

}
