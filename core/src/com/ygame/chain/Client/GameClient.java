package com.ygame.chain.Client;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ygame.chain.screens.Level0;
import com.ygame.chain.utils.GameUtil;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameClient {
    private Client client;
    private Game game;
    private Level0 level0;

    public GameClient(Game game, String host) throws IOException {
        this.game = game;
        client = new Client();
        GameUtil.KryoHelper.registerClasses(client.getKryo());

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.RoomJoinResponse) {
                    SharedClasses.RoomJoinResponse response = (SharedClasses.RoomJoinResponse) object;
                    level0 = new Level0(response.playerStates);
                    game.setScreen(level0);
                } else if (object instanceof HashMap) {
                    Map<String, SharedClasses.PlayerState> states = (Map<String, SharedClasses.PlayerState>) object;
                    level0.updatePlayerStates(states);
                }
            }
        });

        client.start();
        client.connect(5000, host, 54555, 54777);
    }

    public void createRoom(String roomCode) {
        SharedClasses.RoomJoinRequest request = new SharedClasses.RoomJoinRequest();
        request.roomCode = roomCode;
        client.sendTCP(request);
    }

    public void joinRoom(String roomCode) {
        SharedClasses.RoomJoinRequest request = new SharedClasses.RoomJoinRequest();
        request.roomCode = roomCode;
        client.sendTCP(request);
    }

    public void updateState(SharedClasses.PlayerState state) {
        client.sendTCP(state);
    }

    public void closeClient() {
        client.close();
    }
}
