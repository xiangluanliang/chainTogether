package com.ygame.chain.Client;

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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ygame.chain.utils.SharedClasses;

import java.io.IOException;

public class GameClient {
    private Client client;

    public GameClient(String host) throws IOException {
        client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(SharedClasses.RegisterName.class);
        kryo.register(SharedClasses.UpdatePosition.class);
        kryo.register(SharedClasses.RoomCode.class);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof SharedClasses.RegisterName) {
                    sendRoomCode();
                    SharedClasses.RegisterName registerName = (SharedClasses.RegisterName) object;
                    System.out.println(registerName);
                } else if (object instanceof SharedClasses.UpdatePosition) {
                    SharedClasses.UpdatePosition updatePosition = (SharedClasses.UpdatePosition) object;
                } else if (object instanceof String) {
                    String roomCode = (String) object;
                    System.out.println(roomCode);
                    SharedClasses.RoomCode.roomCode = roomCode;
                }
            }
        });

        client.start();
        client.connect(5000, host, 54555, 54777);

        // 发送注册消息
        SharedClasses.RegisterName registerName = new SharedClasses.RegisterName();
        registerName.name = "Player1";

//        // 发送位置更新
//        SharedClasses.UpdatePosition updatePosition = new SharedClasses.UpdatePosition();
//        client.sendTCP(updatePosition);

    }

    public void sendRoomCode() {
        client.sendTCP(new SharedClasses.RoomCode());
    }

    public void close() {
        client.close();
    }

}