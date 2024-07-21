package com.ygame.chain.Server;

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


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;

public class GameServer {
    private Server server;

    public GameServer() throws IOException {
        server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(SharedClasses.RegisterName.class);
        kryo.register(SharedClasses.UpdatePosition.class);
        kryo.register(SharedClasses.RoomCode.class);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.RegisterName) {
                    SharedClasses.RegisterName registerName = (SharedClasses.RegisterName) object;
                    SharedClasses.RegisterName tempregisterName = new SharedClasses.RegisterName();
                    tempregisterName.name = registerName.name;
                    for (Connection conn : server.getConnections()) {
                        if (conn != connection) { // 不发送给发送者自己
                            conn.sendTCP(tempregisterName);
                        }
                    }
                } else if (object instanceof SharedClasses.UpdatePosition) {
                    SharedClasses.UpdatePosition updatePosition = (SharedClasses.UpdatePosition) object;
                } else if (object instanceof SharedClasses.RoomCode) {
                    SharedClasses.RoomCode exitroomCode = (SharedClasses.RoomCode) object;
                    SharedClasses.RoomCode tempRoomCode = new SharedClasses.RoomCode();
                    exitroomCode.roomCode = tempRoomCode.roomCode;
                    for (Connection conn : server.getConnections()) {
                        if (conn != connection) { // 不发送给发送者自己
                            conn.sendTCP(tempRoomCode);
                        }
                    }
                }
            }
        });

        server.bind(54555, 54777);
        server.start();
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }
}