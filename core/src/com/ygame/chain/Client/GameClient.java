package com.ygame.chain.Client;

import com.ygame.chain.screens.LoginScreen;
import com.ygame.chain.utils.Player;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: GameClient
 * Package : com.ygame.chain.network
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/18 17:00
 * @Version 1.0
 */

public class GameClient implements Runnable {
    private static final String SERVER_ADDRESS = LoginScreen.getServerAddress();
    private static final int SERVER_PORT = 1234;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Player role;

    public GameClient(Player role){
        this.role = role;
        this.run();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//            new Thread(new ServerListener()).start();

            // Example of login
            out.println("LOGIN player");

            // Example of sending move
            // out.println("MOVE player x y");

            // Example of starting game
            // out.println("START");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "服务器链接失败，请检查网络链接！");
            e.printStackTrace();
        }
    }

    class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Server: " + serverMessage);
                    // Handle server messages, e.g., update game state
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}