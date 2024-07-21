package com.ygame.chain.Server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygame.chain.utils.GameUtil;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer {
    private Server server;
    private List<String> playerTextures;
    private List<String> rooms;
    private Map<String, Integer> playerOrder;
    private int playerCount = 0;

    public GameServer() throws IOException {
        server = new Server();
        playerTextures = Arrays.asList(
                "./ball/smallGreenBall.png",
                "./ball/smallPurpleBall.png",
                "./ball/smallRedBall.png"
        );
        playerOrder = new HashMap<>();

        GameUtil.KryoHelper.registerClasses(server.getKryo());

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.JoinRequest) {
                    SharedClasses.JoinRequest request = (SharedClasses.JoinRequest) object;
                    int order = playerCount++;
                    playerOrder.put(request.userID, order);
                    SharedClasses.JoinResponse response = new SharedClasses.JoinResponse(order, playerTextures.get(order));
                    connection.sendTCP(response);
                } else if (object instanceof SharedClasses.PlayerMove) {
                    server.sendToAllTCP(object);
                }
//                else if (object instanceof SharedClasses.RoomCode) {
//                    SharedClasses.RoomCode room = (SharedClasses.RoomCode) object;
//                    rooms.add(room.roomCode);
//                    server.sendToAllTCP();
//                }
            }
        });

        server.bind(54555, 54777);
        server.start();
    }

    public static void main(String[] args) {
        try {
            new GameServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}