package com.ygame.chain.Server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygame.chain.utils.GameUtil;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private Server server;
    private Map<String, Room> rooms;

    public GameServer() throws IOException {
        server = new Server();
        rooms = new HashMap<>();

        GameUtil.KryoHelper.registerClasses(server.getKryo());

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.RoomJoinRequest) {
                    SharedClasses.RoomJoinRequest request = (SharedClasses.RoomJoinRequest) object;
//                    Room room = new Room(request.roomCode);
//                    room.addPlayer();
//                    rooms.put(request.roomCode,);


                    Room room = rooms.computeIfAbsent(request.roomCode, k -> new Room(request.roomCode));
                    room.addPlayer(connection);
                    connection.sendTCP(new SharedClasses.RoomJoinResponse(room.getPlayerStates()));


                } else if (object instanceof SharedClasses.PlayerState) {
                    SharedClasses.PlayerState state = (SharedClasses.PlayerState) object;
                    Room room = findRoomForPlayer(connection);
                    if (room != null) {
                        room.updatePlayerState(connection, state);
                    }
                }
            }
        });

        server.bind(54555, 54777);
        server.start();
    }

    private Room findRoomForPlayer(Connection connection) {
        for (Room room : rooms.values()) {
            if (room.hasPlayer(connection)) {
                return room;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }
}

class Room {
    private String roomCode;
    private Map<Connection, SharedClasses.PlayerState> players;

    public Room() {

    }

    public Room(String roomCode) {
        this.roomCode = roomCode;
        this.players = new HashMap<>();
    }

    public void addPlayer(Connection connection) {
        players.put(connection, new SharedClasses.PlayerState());
    }

    public void updatePlayerState(Connection connection, SharedClasses.PlayerState state) {
        players.put(connection, state);
        broadcastStateUpdate();
    }

    public boolean hasPlayer(Connection connection) {
        return players.containsKey(connection);
    }

    public void removePlayer(Connection connection) {
        players.remove(connection);
        broadcastStateUpdate();
    }

    public Map<String, SharedClasses.PlayerState> getPlayerStates() {
        Map<String, SharedClasses.PlayerState> states = new HashMap<>();
        for (Map.Entry<Connection, SharedClasses.PlayerState> entry : players.entrySet()) {
            states.put(entry.getKey().toString(), entry.getValue());
        }
        return states;
    }

    public void broadcastStateUpdate() {
        Map<String, SharedClasses.PlayerState> states = getPlayerStates();
        for (Connection connection : players.keySet()) {
            connection.sendTCP(states);
        }
    }
}