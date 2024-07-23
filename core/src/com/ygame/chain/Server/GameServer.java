package com.ygame.chain.Server;

import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.ygame.chain.utils.SharedClasses.playerMap;

public class GameServer {
    public static int threadNumber = 0;
    private ServerSocket serverSocket;
    private ConcurrentMap<String, PlayerConnection> players = new ConcurrentHashMap<>();

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
                threadNumber++;
                System.out.println(threadNumber);

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
            this.setName(readUserID());
            players.put(this.getName(), this);
            System.out.println(this.getName());
        }

        public synchronized void update(SharedClasses.PlayerState playerState) {
            try {
                out.reset();
                out.writeObject(playerState);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            try {
                Object object;
                while ((object = in.readObject()) != null) {
                    if (object instanceof SharedClasses.PlayerState) {
                        SharedClasses.PlayerState state = (SharedClasses.PlayerState) object;
//                        Map<String, SharedClasses.PlayerState> playerMap
//                                = (HashMap<String, SharedClasses.PlayerState>) object;
                        playerMap.put(this.getName(), state);

//                        for (Map.Entry<String, SharedClasses.PlayerState> player : playerMap.entrySet()) {
//                            SharedClasses.playerMap.get(player.getKey()).update(player.getValue().getX(), player.getValue().getY());
////                            SharedClasses.playerMap.get(player.getKey()).linearImpulse = player.getValue().linearImpulse;
//                        }
//
                        for (PlayerConnection conn : players.values()) {
                            if (conn != this) {
                                conn.update(playerMap.get(this.getName()));
                            }
                        }
//                        System.out.println("rec server");
                    }
                    if (object instanceof Boolean) {
                        SharedClasses.gameStart = (boolean) object;
                        for (PlayerConnection conn : players.values()) {
                            if (conn != this) {
                                conn.startGame(SharedClasses.gameStart);
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (!players.isEmpty()) {
                    players.remove(this.getName());
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void startGame(boolean gameStart) {
            try {
                out.reset();
                out.writeObject(gameStart);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String readUserID() {
            try {
                return (String) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}