package com.ygame.chain.Client;

import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String userID;


    public GameClient(String serverAddress, int port, String userID) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        new SharedClasses();
        this.userID = userID;
        sendUserID(userID);
        new Thread(this::listenForUpdates).start();
    }

    private void listenForUpdates() {
        try {
            Object object;
            while ((object = in.readObject()) != null) {
                if (object instanceof SharedClasses.PlayerState) {
                    SharedClasses.PlayerState state = (SharedClasses.PlayerState) object;
                    SharedClasses.playerMap.put(state.userID, state);
//                    HashMap<String, SharedClasses.PlayerState> receivedMap =
//                            (HashMap<String, SharedClasses.PlayerState>) object;
//                    for (Map.Entry<String, SharedClasses.PlayerState> player : receivedMap.entrySet()) {
//                        SharedClasses.playerMap.put(player.getKey(), player.getValue());
//                    }
                }
                if (object instanceof Boolean) {
                    SharedClasses.gameStart = (boolean) object;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPlayerMap(SharedClasses.PlayerState state) {
        try {
            out.reset();
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUserID(String userID) {
        try {
            out.writeObject(userID);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startGame() {
        try {
            out.writeObject(SharedClasses.gameStart);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
