package com.ychstudio.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    
    public static final int PORT = 19234;
    
    public static final int FAKE_PORT = 19238; // not actually used
    public static final int UDP_PORT = 19239; // for broadcasting
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(LoginRequire.class);
        kryo.register(LoginRequest.class);
        kryo.register(LoginApprove.class);
        kryo.register(LoginReject.class);
        kryo.register(NewPlayerLogin.class);
        kryo.register(PlayerLogout.class);
        kryo.register(StartGame.class);
        kryo.register(UpdatePosition.class);
        kryo.register(Shoot.class);
        kryo.register(ThrowGrenade.class);
        
    }
    
    public static class LoginRequire {
        
    }
    
    public static class LoginRequest {
        
    }
    
    public static class LoginApprove {
        public int id;
        
    }
    
    public static class NewPlayerLogin {
        public int id;
    }
    
    public static class PlayerLogout {
        public int id;
    }
    
    public static class LoginReject {
        
    }
    
    public static class StartGame {
        
    }
    
    public static class UpdatePosition {
        int id;
        public float x;
        public float y;
    }
    
    public static class Shoot {
        int id;
        public float x;
        public float y;
    }
    
    public static class ThrowGrenade {
        int id;
        public float x;
        public float y;
    }

}
