package com.ygame.chain.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public GameClient(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // 发送消息给服务器
        out.println("Hello, Server!");

        // 读取服务器的回应
        String response = in.readLine();
        System.out.println("Server response: " + response);
    }
}
