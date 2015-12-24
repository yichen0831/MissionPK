package com.ychstudio.network;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GameClient implements Disposable {
    
    Client client;
    
    public GameClient(String host) {
        client = new Client();
        Network.register(client);
        
        client.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                // send login request
            }

            @Override
            public void received(Connection connection, Object object) {
            }
            
            @Override
            public void disconnected(Connection connection) {
            }
        });
        
        client.start();
        try {
            client.connect(5000, host, Network.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void dispose() {
        client.close();
    }

}
