package com.ygame.chain.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: GameServer
 * Package : com.ygame.chain.network
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/18 17:00
 * @Version 1.0
 */
public class GameServer implements Runnable {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();//ConcurrentHashMap.newKeySet();
    private static int connectedPlayers = 0;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    if (inputLine.startsWith("LOGIN")) {
                        handleLogin(inputLine);
                    } else if (inputLine.startsWith("MOVE")) {
                        handleMove(inputLine);
                    } else if (inputLine.startsWith("START")) {
                        handleStart(inputLine);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleLogin(String inputLine) {
            connectedPlayers++;
            broadcast("PLAYER_CONNECTED " + connectedPlayers);
            if (connectedPlayers == 3) {
                broadcast("ALL_PLAYERS_CONNECTED");
            }
        }

        private void handleMove(String inputLine) {
            broadcast(inputLine);
        }

        private void handleStart(String inputLine) {
            broadcast("START_GAME");
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
