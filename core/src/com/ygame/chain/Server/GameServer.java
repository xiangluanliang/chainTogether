package com.ygame.chain.Server;

import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameServer {
    private ServerSocket serverSocket;
    //    private ConcurrentMap<String, PlayerConnection> players = new ConcurrentHashMap<>();
    private ConcurrentMap<String, SharedClasses.PlayerState> playerStates = new ConcurrentHashMap<>();

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(this::listenForConnections).start();
    }

    public static void main(String[] args) throws IOException {
        new GameServer(12345);
        new SharedClasses();
    }

    private void listenForConnections() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new PlayerConnection(socket).start();
                System.out.println(serverSocket.getInetAddress().getHostName() + ":" +
                        serverSocket.getInetAddress().getHostAddress() + "已连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PlayerConnection extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public PlayerConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        public void update(HashMap<String, SharedClasses.PlayerState> playerMap) {
            try {
                out.writeObject(playerMap);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            try {
                Object object;
//                synchronized (SharedClasses.playerMap) {
////                    out.writeObject(new HashMap<>(SharedClasses.playerMap));
////                    out.writeObject(new SharedClasses().playerMap);
//                    out.writeObject(SharedClasses.playerMap);
//                }
                while ((object = in.readObject()) != null) {
                    if (object instanceof HashMap) {
                        HashMap<String, SharedClasses.PlayerState> playerMap
                                = (HashMap<String, SharedClasses.PlayerState>) object;
//                        for (Map.Entry<String,SharedClasses.PlayerState> player :playerMap.entrySet()){
//                            players.put(player.getKey(),this);
//                            playerStates.put(player.getKey(), player.getValue());
//                        }
                        SharedClasses.playerMap = playerMap;
                        update(SharedClasses.playerMap);
//                        out.writeObject(SharedClasses.playerMap);
//                        for (PlayerConnection conn : players.values()){
//                            if (conn != this){
//                                conn.update(playerMap);
//                            }
//                        }
//                        System.out.println("rec server");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
//                if (!players.isEmpty()) {
//                    players.remove(userID);
//                    playerStates.remove(userID);
//                    broadcastStateUpdate();
//                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}