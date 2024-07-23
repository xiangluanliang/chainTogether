package com.ygame.chain.Client;

import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
                    for (Map.Entry<String, SharedClasses.PlayerState> player :
                            receivedMap.entrySet()) {
                        System.out.println(player.getValue().getX() + ", " + player.getValue().getY());
                    }
                    for (Map.Entry<String, SharedClasses.PlayerState> player : receivedMap.entrySet()) {
                        SharedClasses.playerMap.put(player.getKey(), player.getValue());
                    }
//                    SharedClasses.playerMap.clear();
//                    SharedClasses.playerMap.putAll(receivedMap);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerMap() {
        try {
            out.reset();
//            System.out.println("send!!!!!");
//            for (SharedClasses.PlayerState playerState : SharedClasses.playerMap.values()){
//                System.out.println(playerState.getX() + ", " + playerState.getY());
//            }
            out.writeObject(SharedClasses.playerMap);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
