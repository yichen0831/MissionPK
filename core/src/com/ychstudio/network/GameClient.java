package com.ychstudio.network;

import java.io.IOException;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ychstudio.network.Network.LoginApprove;
import com.ychstudio.network.Network.LoginRequest;
import com.ychstudio.network.Network.LoginRequire;
import com.ychstudio.network.Network.NewPlayerLogin;
import com.ychstudio.network.Network.PlayerLogout;

public class GameClient implements Disposable {
    
    Client client;
    int id;
    
    public GameClient(String host) {
        client = new Client();
        Network.register(client);
        
        client.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
            }

            @Override
            public void received(Connection connection, Object object) {
                
                if (object instanceof LoginRequire) {
                    // send login request
                    LoginRequest loginRequest = new LoginRequest();
                    connection.sendTCP(loginRequest);
                    return;
                }
                
                if (object instanceof LoginApprove) {
                    LoginApprove loginApprove = (LoginApprove) object;
                    id = loginApprove.id;
                    System.out.println("Login success @id: " + id);
                    return;
                }
                
                if (object instanceof NewPlayerLogin) {
                    NewPlayerLogin npl = (NewPlayerLogin) object;
                    System.out.println("NewPlayerLogin @id: " + npl.player.id);
                    return;
                }
                
                if (object instanceof PlayerLogout) {
                    PlayerLogout playerLogout = (PlayerLogout) object;
                    System.out.println("Player logout @id: " + playerLogout.id);
                }
            }
            
            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnected from server...");
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
