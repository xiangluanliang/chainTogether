package com.ygame.chain.Client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ygame.chain.screens.Level0;
import com.ygame.chain.screens.Level1;
import com.ygame.chain.utils.GameUtil;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;

public class GameClient {
    private Client client;
    private Game game;
    private Level0 level0;
    private String userID;

    public GameClient(Game game, String serverAddress, String userID) throws IOException {
        this.game = game;
        this.userID = userID;
        client = new Client();
        GameUtil.KryoHelper.registerClasses(client.getKryo());

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.JoinResponse) {
                    SharedClasses.JoinResponse response = (SharedClasses.JoinResponse) object;
//                    level0 = new Level0(userID, response.texturePath);
//                    game.setScreen(level0);
                    game.setScreen(new Level1());
                } else if (object instanceof SharedClasses.PlayerMove) {
                    SharedClasses.PlayerMove move = (SharedClasses.PlayerMove) object;
                    level0.updatePlayerPosition(move.userID, move.position);
                }
            }
        });

        client.start();
        client.connect(5000, serverAddress, 54555, 54777);

        SharedClasses.JoinRequest joinRequest = new SharedClasses.JoinRequest(userID);
        client.sendTCP(joinRequest);
    }

    //    public void createRoom(String roomCode){
//        SharedClasses.RoomCode room = new SharedClasses.RoomCode(roomCode);
//        client.sendTCP(room);
//
//    }
    public void sendMove(Vector2 position) {
        SharedClasses.PlayerMove move = new SharedClasses.PlayerMove(userID, position);
        client.sendTCP(move);
    }

    public void closeClient() {
        client.close();
    }

}
