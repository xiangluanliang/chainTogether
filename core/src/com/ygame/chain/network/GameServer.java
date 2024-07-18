package com.ygame.chain.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
public class GameServer {
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        int port = 54555;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    // Echo the message back to the client
                    out.println("Server: " + inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    public void start(int port) {
//        try {
//            serverSocket = new ServerSocket(port);
//            System.out.println("Server started on port " + port);
//            while (true) {
//                new ClientHandler(serverSocket.accept()).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void stop() {
//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private static class ClientHandler extends Thread {
//        private Socket clientSocket;
//        private PrintWriter out;
//        private BufferedReader in;
//
//        public ClientHandler(Socket socket) {
//            this.clientSocket = socket;
//        }
//
//        public void run() {
//            try {
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//                String inputLine;
//                while ((inputLine = in.readLine()) != null) {
//                    System.out.println("Received: " + inputLine);
//                    // Echo the message back to the client
//                    out.println("Server: " + inputLine);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    in.close();
//                    out.close();
//                    clientSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
