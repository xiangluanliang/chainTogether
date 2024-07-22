package com.ygame.chain.Client;

import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
//    private Map<String, SharedClasses.PlayerState> playerMap = new HashMap<>();
//    SharedClasses sharedClasses;


    public GameClient(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        new SharedClasses();
        new Thread(this::listenForUpdates).start();
    }

    private void listenForUpdates() {
        try {
            Object object;
            while ((object = in.readObject()) != null) {
                if (object instanceof HashMap) {
                    HashMap<String, SharedClasses.PlayerState> receivedMap =
                            (HashMap<String, SharedClasses.PlayerState>) object;
                    SharedClasses.playerMap.clear();
                    SharedClasses.playerMap.putAll(receivedMap);
                    putPlayerMap(SharedClasses.playerMap);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerMap() {
        try {
            out.writeObject(SharedClasses.playerMap);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putPlayerMap(HashMap<String, SharedClasses.PlayerState> playerMap) {
        SharedClasses.playerMap = playerMap;
//        for (Map.Entry<String, SharedClasses.PlayerState> player :
//                SharedClasses.playerMap.entrySet()) {
//            System.out.println(player.getValue().getX() + ", " + player.getValue().getY());
//        }
//        System.out.println("send successful");
    }

}
