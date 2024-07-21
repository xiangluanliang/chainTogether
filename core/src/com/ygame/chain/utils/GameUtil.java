package com.ygame.chain.utils;

import com.esotericsoftware.kryo.Kryo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;

/**
 * ProjectName: chain_together_Yhr
 * ClassName: GameUtil
 * Package : com.ygame.chain.utils
 * Description:
 *
 * @Author Lxl
 * @Create 2024/7/21 11:04
 * @Version 1.0
 */
public class GameUtil {
    public static String generateRoomNumber() {
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return String.valueOf(number);
    }

    public static String getServerAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static class KryoHelper {
        public static void registerClasses(Kryo kryo) {
            kryo.register(SharedClasses.PlayerState.class);
            kryo.register(SharedClasses.PlayerType.class);
            kryo.register(SharedClasses.RoomJoinRequest.class);
            kryo.register(SharedClasses.RoomJoinResponse.class);
            kryo.register(HashMap.class);
            kryo.register(SharedClasses.PlayerState[].class);
        }
    }
}
